package edu.team7_18842cmu.Network;

/**
 * Created by Prabhanjan on 05/04/2015.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.yaml.snakeyaml.Yaml;

import edu.team7_18842cmu.dbutil.DBManager;


public class MessagePasserX
{
    public DBManager dbm;
    //List of all current rules
    ArrayList<Rule> currentRuleSet = new ArrayList<Rule>();

    //PB List of all Host connections
    ArrayList<Socket> connectedSendHosts = new ArrayList<Socket>();
    ArrayList<Socket> connectedReceiveHosts = new ArrayList<Socket>();
    ArrayList<HostWithSocketAndStream> listOfEverything = new ArrayList<HostWithSocketAndStream>();

    //Queues for sending and receiving
    Queue<Message> sendQueue,receiveQueue;
    Queue<Message> delayedSendQueue, delayedReceiveQueue, processedRecieveQueue;

    //List of all hosts a particular message must be sent to. This list will change every time the YAML file is read for a particular message
    ArrayList<Socket> destinationHostSockets = new ArrayList<Socket>();

    //Thread for receiver
    Thread receiverThread;

    //PB may need to change
    int seqNumGlobal;

    //Port number of this Node
    protected String serverIP;
    protected int serverPort;
    protected String serverName;

    //Socket of this Node
    protected ServerSocket serverSocket;

    //CL Last time configuration file is modified
    //Initialize when: first load
    //Update when:     reload
    long lastModifyTime;

    //CL Name of the configuration file
    //Used when: check if the config file has changed
    protected static String configFileName;

    ClockService clock;
    String clockOption;


    HostWithSocketAndStream Logger;

    int randomVarToDealWithGit;
    //TODO: where to deal with proc_name
    public MessagePasserX(String config_filename, String proc_name, String clockType, DBManager dbm) throws Exception
    {
        this.clockOption = clockType;
        this.serverName = proc_name;
        this.dbm = dbm;

        // CL Read config file and parse and save the config state
        //YAML stuff here...populate currentRuleSet
        configFileName = config_filename;
        loadConfigFile();

        //Initialize serverIP the server's address
        InetAddress addr  = InetAddress.getLocalHost();
        serverIP = addr.getHostAddress();

        this.serverName = serverIP;

        //Check if Host is Linux machine
//        if (serverIP.startsWith("127", 0))
//        {
//            NetworkInterface netint = NetworkInterface.getByName("eth0");
//            Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
//            for(InetAddress inetAddress: Collections.list(inetAddresses))
//            {
//                if(inetAddress.getHostAddress().startsWith("128", 0))
//                {
//                    serverIP = inetAddress.getHostAddress();
//                }
//            }
//        }
        System.out.println("The hosts IP addr is: " + serverIP);

        //Create clocks
        if (clockType.equals("logical"))
        {
            System.out.println("Creating LogicalClockService");
            clock = new LogicalClockService();
            System.out.println("LogicalClockService created...");
        }
        else if (clockType.equals("vector"))
        {
            System.out.println("Creating VectorClockService");
            clock = new VectorClockService(this.getNumberOfHosts(), this.getIndexOfLocalHost());
            System.out.println("VectorClockService created...");
        }



        //Initialize serverPort
//        serverPort = this.listOfEverything.get(getIndexOfLocalHost()).port;
        serverPort = 12000;


        //PB Setup message queues for sending and receiving.
        //sendQueue = new ArrayDeque<Message>();
        receiveQueue = new ArrayDeque<Message>();
        delayedSendQueue = new ArrayDeque<Message>();
        delayedReceiveQueue = new ArrayDeque<Message>();
        processedRecieveQueue = new ArrayDeque<Message>();

        //PB Init seq number
        seqNumGlobal = 0;

        receiverThreadFunctionality();


        //TODO: Send connect message to Logger and save the logger
        Message msgToLogger = new Message("Logger","ANY",new String("connect"));
        msgToLogger.setSourceNodeName(this.getNameByIpAddrAndPort(serverIP, serverPort));
        sendMessage(msgToLogger);


        for(HostWithSocketAndStream host: listOfEverything)
        {
            if (host.hostName.equals("Logger"))
            {
                Logger = host;
            }
        }
    }





    public Boolean send(Message message) throws Exception
    {
        //Check config file for change
        //Reload if changed
        /*if(checkConfigFileValidity() == false)
        {
            reloadConfigFile();
        }*/

        //PB: Set seq number and increment
        System.out.println("Made it to send");
        message.setSeqNum(seqNumGlobal);
        seqNumGlobal++;
        //Set the source name
        message.setSourceNodeName(this.getNameByIpAddrAndPort(serverIP, serverPort));

        //TODO: use ClockService to assign timestamp to message
        //Do clock activities
        try
        {
            clock.sendAction();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        message.timestamp = new TimeStamp(clock.lamportTimeStamp);//setTimestamp(new TimeStamp(clock.getTimeStamp()));
        message.timestamp.timeStamp = new ArrayList<Long>();
        for(int i = 0; i < clock.lamportTimeStamp.size(); i ++)
        {
            message.timestamp.timeStamp.add(clock.lamportTimeStamp.get(i));
        }
        System.out.println("The message's timestamp is: " + message.timestamp.toString());

        //Boolean sent = checkSendRule(message);

        if(message.getMessageType().equals("server"))
            sendMessageToServer(message);
        else
            sendMessage(message);

        return true;
    }

    public Message receive()
    {

        //Check config file for change
        //Reload if changed
        /*if(checkConfigFileValidity() == false)
        {
            reloadConfigFile();



        }*/


        //Process a single message waiting in the buffer
        //Check message for receive rules

        if(checkReceiveRule()){
            //Add delayed message to processed receive queue
            checkDelayedReceiveMessage();
        }
        //Get one message from processed receive queue
        if(!processedRecieveQueue.isEmpty())
        {
            Message recvMessage = fetchMessage();
            try
            {
                clock.receiveAction(recvMessage.getTimestamp());
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return recvMessage;
        }
        else
        {
            //System.out.println("There is no pending message in the processedReceiveQueue");
            return null;
        }

    }

    //DONE: check delayed receive message
    void checkDelayedReceiveMessage()
    {
        while(!delayedReceiveQueue.isEmpty())
        {
            Message message = this.delayedReceiveQueue.remove();
            this.processedRecieveQueue.add(message);
        }
        //else delayedReceiveQueue is empty
    }

    //DONE: get one message from processed receive queue
    Message fetchMessage(){
        if(!processedRecieveQueue.isEmpty())
            return this.processedRecieveQueue.remove();
        else
            return null;
    }

    //DONE:PB Need to implement this funtion as a runnable or its own internal class
    void receiverThreadFunctionality() throws IOException
    {
        //Receive side:
        //Setup listener thread that populates receive queue

        receiverThread = new Thread(new ReceiveLoop(serverSocket,serverPort,this, dbm));
        receiverThread.start();

        //return;




    }

    //Internal class that runs receive functionality on a separate listening thread
    private static class ReceiveLoop implements Runnable
    {

        ServerSocket serverSocket;
        int serverPort;
        MessagePasserX MP;
        DBManager dbm;



        public ReceiveLoop(ServerSocket serverSocket, int serverPort,MessagePasserX MP, DBManager dbm) {
            super();
            this.serverSocket = serverSocket;
            this.serverPort = serverPort;
            this.MP = MP;
            this.dbm = dbm;
        }



        public void run()
        {
            System.out.println("Starting the socket server at port:" + serverPort);
            try
            {
                System.out.println("Server port is:" + serverPort);
                serverSocket = new ServerSocket(serverPort);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            //Listen for clients. Block till one connects
            Socket client = null;
            while(true)
            {
                System.out.println("Waiting for clients...");
                try
                {
                    client = serverSocket.accept();
                    System.out.println("A connection has been accepted...");
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                System.out.println("The following client has connected to this node:"+client.getInetAddress().getCanonicalHostName());

                //A client has connected to this server. Give it its own thread.
                //Check if client has connected previously


                Thread thread = new Thread(new ReceiveHandler(client,MP,dbm));
                thread.start();
            }

        }

    }

    //Check send rules
		/*
		 * Check the send rules
		 * If successfully added a message to send queue, return true
		 * Else return false
		 * */
    Boolean checkSendRule(Message message) throws IOException{
        for(Rule rule: currentRuleSet){
            if(rule.getSendOrReceive().equalsIgnoreCase("send")){
                // Match
                if((rule.getDest()==null || rule.getDest().equalsIgnoreCase(message.getDestinationNodeName()))
                        &&(rule.getSrc()==null || rule.getSrc().equalsIgnoreCase(message.getSourceNodeName()))
                        &&(rule.getSeqNum()==-1 || rule.getSeqNum() == message.getSeqNum())
                        &&(rule.getKind()==null || rule.getKind().equalsIgnoreCase(message.getMessageType()))
                        &&(rule.getDuplicate()==null || rule.getDuplicate().equals("true") == message.getFlag_duplicate())){
                    String action = rule.getAction();
                    if(action == null){
                        //no action, send directly ??

                        return true;
                    }
                    else if(action.equalsIgnoreCase("drop")){
                        //drop the message
                        return false;
                    }
                    else if(action.equalsIgnoreCase("duplicate")){
                        //duplicate the message, send both of them
                        message.setFlag_duplicate(false);
                        //Send code here
                        sendMessage(message);

                        Message message2 = new Message(message);
                        message2.setFlag_duplicate(true);
                        //Send code here
                        sendMessage(message2);

                        return true;
                    }
                    else if(action.equalsIgnoreCase("delay")){
                        System.out.println("sending dalay");
                        this.delayedSendQueue.add(message);
                        return false;
                    }
                    else{
                        //Illegal action, drop the message ??
                        return false;
                    }
                }
            }
        }
        //No rule matches, send directly ?? or drop  ??

        sendMessage(message);
        //this.sendQueue.add(message);
        return true;
    }

    //DONE:CL check receive rules
		/*
		 * Check the receive rules
		 * If successfully added a message to processed queue, return true
		 * Else return false
		 * */
    Boolean checkReceiveRule()
    {
        if(receiveQueue.isEmpty())
            return false;
        Message message = this.receiveQueue.remove();
        for(Rule rule: currentRuleSet){
            if(rule.getDuplicate()!=null)
                System.out.println("rule.duplicate.equals(\"true\")  = " + rule.getDuplicate().equals("true") + ";"
                        + "message.getFlag_duplicate() = " + message.getFlag_duplicate());
            if(rule.sendOrReceive.equalsIgnoreCase("receive")){
                if((rule.getDest()==null || rule.getDest().equalsIgnoreCase(message.getDestinationNodeName()))
                        &&(rule.getSrc()==null || rule.getSrc().equalsIgnoreCase(message.getSourceNodeName()))
                        &&(rule.getSeqNum()==-1 || rule.getSeqNum() == message.getSeqNum())
                        &&(rule.getKind()==null || rule.getKind().equalsIgnoreCase(message.getMessageType()))
                        &&(rule.getDuplicate()==null || rule.getDuplicate().equals("true") == message.getFlag_duplicate())){
                    String action = rule.getAction();
                    if(action == null){
                        //no action, add directly ??
                        this.processedRecieveQueue.add(message);
                        return true;
                    }
                    else if(action.equalsIgnoreCase("drop")){
                        //drop the message
                        return false;
                    }
                    else if(action.equalsIgnoreCase("duplicate")){
                        //DONE: duplicate the message
                        //HOW?
                        //Add it twice to the processed queue
                        this.processedRecieveQueue.add(message);
                        this.processedRecieveQueue.add(message);
                        return true;
                    }
                    else if(action.equalsIgnoreCase("delay")){
                        //delay the message
                        this.delayedReceiveQueue.add(message);
                        return false;
                    }
                    else{
                        //Illegal action, drop the message ??
                        return false;
                    }
                }
            }
        }
        //no rule matches, add it directly
        this.processedRecieveQueue.add(message);
        return true;
    }

    //Read config file and parse and save the config state
    //YAML stuff here...populate currentRuleSet
    void loadConfigFile()
    {
        //update modification time
        //TODO: Changed here...just for reference
        //File configFile = new File(configFileName);
        //lastModifyTime = configFile.lastModified();


        //clear rule set
        currentRuleSet.clear();
        InputStream input = null;
        //TODO: changed here
        //input = new FileInputStream(configFile);

        try {
            input = (InputStream) new URL(configFileName).getContent();
        }
        catch (MalformedURLException e)
        {
            System.out.println("Config URL Malformed");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        {

        }
        Yaml yaml = new Yaml();
        @SuppressWarnings("unchecked")
        Map<String, ArrayList> data = (LinkedHashMap<String, ArrayList>) yaml.load(input);

        for(Entry<String, ArrayList> entry: data.entrySet() )
        {
            if(entry.getKey().equalsIgnoreCase("configuration")){

                ArrayList array = entry.getValue();
                for(Object obj: array){
                    Map<String, Object> map = (LinkedHashMap<String, Object>) obj;


                    HostWithSocketAndStream host = new HostWithSocketAndStream((String)map.get("name"),(String)map.get("ip"),(Integer)map.get("port"));
                    listOfEverything.add(host);
                }
            }
            else if(entry.getKey().equalsIgnoreCase("sendRules")){
                ArrayList array = entry.getValue();
                for(Object obj: array){
                    Map<String, Object> map = (LinkedHashMap<String, Object>) obj;
                    //Rule Initialization
                    Rule rule = new Rule("send", (String)map.get("action"),
                            (String)map.get("src"), (String)map.get("dest"),
                            (String)map.get("kind"),
                            (Integer)(map.get("seqNum")==null?-1:map.get("seqNum")),
                            null);
//                            (map.get("duplicate") == null)?null:( (Boolean) map.get("duplicate")?"true":"false" ));
                    currentRuleSet.add(rule);
                }
            }
            else if(entry.getKey().equalsIgnoreCase("receiveRules")){
                ArrayList array = entry.getValue();
                for(Object obj: array){
                    Map<String, Object> map = (LinkedHashMap<String, Object>) obj;
                    //Rule Initialization
                    Rule rule = new Rule("receive", (String)map.get("action"),
                            (String)map.get("src"), (String)map.get("dest"),
                            (String)map.get("kind"),
                            (Integer)(map.get("seqNum")==null?-1:map.get("seqNum")),
                            null);
//                            (map.get("duplicate") == null)?null:( (Boolean) map.get("duplicate")?"true":"false" ));
                    currentRuleSet.add(rule);
                }
            }
        }
        System.out.println("Current rules set is: ");
        for(Rule rule: currentRuleSet){
            System.out.println("sendOrReceive=" + rule.getSendOrReceive() + ";" +
                    "action=" + rule.getAction() + ";" +
                    "src=" + rule.getSrc() + ";" +
                    "dest=" + rule.getDest() + ";" +
                    "kind=" + rule.getKind() + ";" +
                    "seqNum=" + rule.getSeqNum() + ";" +
                    "duplicate = " + rule.getDuplicate() + ";");
        }
        System.out.println("List of Hosts is: ");
        for(HostWithSocketAndStream host: listOfEverything){
            System.out.println("name=" + host.getHostName() + ";" +
                    "ip=" + host.getIpAddr() + ";" +
                    "port=" + host.getPort() + ";");
        }
    }

    //DONE:CL update lastModifyTime
    //DONE:CL update the rules
    //DONE:CL Read config file and parse and save the config state(may need a separate class for this)
    //YAML stuff here...populate currentRuleSet
    void reloadConfigFile()
    {
        //update modification time
        File configFile = new File(configFileName);
        lastModifyTime = configFile.lastModified();
        //clear rule set
        currentRuleSet.clear();
        InputStream input;
        try {
            input = new FileInputStream(configFile);
            Yaml yaml = new Yaml();
            @SuppressWarnings("unchecked")
            Map<String, ArrayList> data = (LinkedHashMap<String, ArrayList>) yaml.load(input);

            for(Entry<String, ArrayList> entry: data.entrySet() )
            {
                //Ignore configuration items here:
                //they should only be used at initial setup
                if(entry.getKey().equalsIgnoreCase("sendRules")){
                    ArrayList array = entry.getValue();
                    for(Object obj: array){
                        Map<String, Object> map = (LinkedHashMap<String, Object>) obj;
                        //Rule Initialization
                        Rule rule = new Rule("send", (String)map.get("action"),
                                (String)map.get("src"), (String)map.get("dest"),
                                (String)map.get("kind"),
                                (Integer)(map.get("seqNum")==null?-1:map.get("seqNum")),
                                null);
//                            (map.get("duplicate") == null)?null:( (Boolean) map.get("duplicate")?"true":"false" ));
                        currentRuleSet.add(rule);
                    }
                }
                else if(entry.getKey().equalsIgnoreCase("receiveRules")){
                    ArrayList array = entry.getValue();
                    for(Object obj: array){
                        Map<String, Object> map = (LinkedHashMap<String, Object>) obj;
                        //Rule Initialization
                        Rule rule = new Rule("receive", (String)map.get("action"),
                                (String)map.get("src"), (String)map.get("dest"),
                                (String)map.get("kind"),
                                (Integer)(map.get("seqNum")==null?-1:map.get("seqNum")),
                                null);
//                            (map.get("duplicate") == null)?null:( (Boolean) map.get("duplicate")?"true":"false" ));
                        currentRuleSet.add(rule);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Current rules set is: ");
        for(Rule rule: currentRuleSet){
            System.out.println("sendOrReceive=" + rule.getSendOrReceive() + ";" +
                    "action=" + rule.getAction() + ";" +
                    "src=" + rule.getSrc() + ";" +
                    "dest=" + rule.getDest() + ";" +
                    "kind=" + rule.getKind() + ";" +
                    "seqNum=" + rule.getSeqNum() + ";");
        }
        System.out.println("List of Hosts is: ");
        for(HostWithSocketAndStream host: listOfEverything){
            System.out.println("name=" + host.getHostName() + ";" +
                    "ip=" + host.getIpAddr() + ";" +
                    "port=" + host.getPort() + ";");
        }
    }

    //DONE:CL Check if the config file has changed
    Boolean checkConfigFileValidity()
    {
        File configFile = new File(configFileName);
        //Last modify time different: config file has changed
        if(lastModifyTime != configFile.lastModified())
            return false;
        else
            return true;
    }

    //To get the IP address of the destination
    public String getIpAddrByName(String hostName){
        for(HostWithSocketAndStream host: listOfEverything){
            if(host.getHostName().equals(hostName))
                return host.getIpAddr();
        }
        return null;
    }

    public void sendCode(Message message) throws IOException
    {
        //Here we need to find the socket from destinationHostSockets that corresponds to the destination of the message. How?

        //Test 2 code:
        System.out.println("Just before sending message ");


        //DONE: recheck this code
        for(Socket socket: connectedSendHosts)
        {
            //Test 2 code:
            //System.out.println("Iteraate thro destinationHostSockets ");
            for(HostWithSocketAndStream host: listOfEverything)
            {
                //System.out.println("V The value of the ipAddr according to the socket is: " + socket.getInetAddress().getHostAddress().toString());
                //System.out.println("^ The value of the ipAddr according to the listOfHosts is:"+host.ipAddr);
                //DONE: Need to make sure this line is correct
                //Check if there is a connected Socket that corresponds to the message's destination node.
                if (socket.getInetAddress().getHostAddress().toString().equals(host.ipAddr))
                {
                    System.out.println("Sending message to "+socket.getInetAddress().getCanonicalHostName()+":"+socket.getPort());
                    ObjectOutputStream OS = new ObjectOutputStream(socket.getOutputStream());
                    OS.writeObject(message);
                    System.out.println("Message sent to "+socket.getInetAddress().getCanonicalHostName()+":"+socket.getPort());
                    OS.close();
                }
            }


        }

        //Test 2 code:
        System.out.println("Just after sending message ");
    }

    void sendMessage(Message message)
    {
        if(message.destinationNodeName.equals(this.serverName))
        {
            System.out.println("Why are you trying to connect to yourself???? I wont allow it!!!");
            return;
        }
        for(HostWithSocketAndStream host: listOfEverything)
        {
            //System.out.println("destinationHostName is : " + host.hostName + "--------------------------------------");
            //System.out.println(" message's destination is : " + message.destinationNodeName + "--------------------------------------");
            if(host.getHostName().equals(message.destinationNodeName))
            {
                //At this point, we need to check if socket exists
                //System.out.println("About to check if socket exists ie host.name == mess.dest" + "--------------------------------------");
                if (host.getSocket() == null)
                {
                    //Here, socket does not exist, so we are creating it
                    System.out.println("Socket does not exist so we are creating it");
                    System.out.println("Attempting to connect to "+host.hostName+ " @ "+ host.ipAddr+":"+host.port);
                    Socket socketClient = null;
                    try
                    {
                        socketClient = new Socket(host.ipAddr,host.port);
                        System.out.println("Connection Established with "+host.hostName+ " @ "+host.ipAddr+":"+host.port);
                        host.setSocket(socketClient);
                        //Create new output stream
                        host.setOS(new ObjectOutputStream(socketClient.getOutputStream()));
                        host.setIS(new ObjectInputStream(socketClient.getInputStream()));






                    }
                    catch (ConnectException e) //Done: handle timeouts
                    {
                        System.out.println("Connection Timeout for "+host.hostName+ "@"+host.ipAddr+":"+host.port);
                        e.printStackTrace();
                        continue;

                    }
                    catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    //create listener thread
                    Thread thread = new Thread(new ReceiveHandler(socketClient,this, dbm));
                    thread.start();
                }

                System.out.println("Sending message to "+ host.getHostName()+" @ "+host.getIpAddr()+":"+host.getPort()+ ", seqNum: "+ message.SeqNum);

                try
                {
                    Thread.sleep(3000);
                }
                catch (InterruptedException e1)
                {

                    e1.printStackTrace();
                }

					/*try
					{
						clock.sendAction();
					}
					catch (InterruptedException e1)
					{
						e1.printStackTrace();
					}*/

					/*if (clockOption.equals("logical"))
					{
						try
						{
							clock.sendAction();
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						message.setTimestamp(new TimeStamp(clock.getTimeStamp()));
					}
					else if (clockOption.equals("vector"))
					{
						try
						{
							clock.sendAction();
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}

						message.setTimestamp(new TimeStamp(clock.getTimeStamp()));
					}*/

					/*//Do clock activities....NOTE: this has been moved to before rules are applied
					try
					{
						clock.sendAction();
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					message.timestamp = new TimeStamp(clock.lamportTimeStamp);//setTimestamp(new TimeStamp(clock.getTimeStamp()));
					message.timestamp.timeStamp = new ArrayList<Long>();
					for(int i = 0; i < clock.lamportTimeStamp.size(); i ++)
					{
						message.timestamp.timeStamp.add(clock.lamportTimeStamp.get(i));
					}
					System.out.println("The message's timestamp is: " + message.timestamp.toString());*/

                //Write object to stream
                try
                {

                    host.OS.writeObject(message);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                if(message.isToBeLogged == true)
                {
						/*try
						{
							Logger.OS.writeObject(message);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}*/
                }


                System.out.println("---------------------------------------Message sent to "+ host.getHostName()+" @ "+host.getIpAddr()+":"+host.getPort());
                return;
            }


        }




    }

    void sendMessageToServer(Message message)
    {

        for(HostWithSocketAndStream host: listOfEverything)
        {
            //System.out.println("destinationHostName is : " + host.hostName + "--------------------------------------");
            //System.out.println(" message's destination is : " + message.destinationNodeName + "--------------------------------------");
            if(host.getHostName().equals(message.destinationNodeName))
            {
                //At this point, we need to check if socket exists
                //System.out.println("About to check if socket exists ie host.name == mess.dest" + "--------------------------------------");
                if (host.getSocket() == null)
                {
                    //Here, socket does not exist, so we are creating it
                    System.out.println("Socket does not exist so we are creating it");
                    System.out.println("Attempting to connect to "+host.hostName+ " @ "+ host.ipAddr+":"+host.port);
                    Socket socketClient = null;
                    try
                    {
                        socketClient = new Socket(host.ipAddr,host.port);
                        System.out.println("Connection Established with "+host.hostName+ " @ "+host.ipAddr+":"+host.port);
                        host.setSocket(socketClient);
                        //Create new output stream
                        InputStream is=socketClient.getInputStream();
                        BufferedReader br=new BufferedReader(new InputStreamReader(is));
                        //get output stream
                        OutputStream os=socketClient.getOutputStream();
                        PrintWriter pw=new PrintWriter(os);

                        host.setBr(br);
                        host.setPw(pw);

                    }
                    catch (ConnectException e) //Done: handle timeouts
                    {
                        System.out.println("Connection Timeout for "+host.hostName+ "@"+host.ipAddr+":"+host.port);
                        e.printStackTrace();
                        continue;

                    }
                    catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    //create listener thread
                    Thread thread = new Thread(new ServerReceiveHandler(socketClient,this, dbm));
                    thread.start();
                }

                System.out.println("Sending message to "+ host.getHostName()+" @ "+host.getIpAddr()+":"+host.getPort()+ ", seqNum: "+ message.SeqNum);

                try
                {
                    Thread.sleep(3000);
                }
                catch (InterruptedException e1)
                {

                    e1.printStackTrace();
                }

                    //write the string out to server
                    host.getPw().write((String)message.getPayload());

                System.out.println("---------------------------------------Message sent to "+ host.getHostName()+" @ "+host.getIpAddr()+":"+host.getPort());
                return;
            }


        }




    }


    //To get the name of the IP address
    public String getNameByIpAddrAndPort(String ipAddr, int port)
    {
        for(HostWithSocketAndStream host: listOfEverything)
        {
            if(host.getIpAddr().equals(ipAddr) && host.getPort() == port)
                return host.getHostName();
        }
        return null;
    }

    public int getNumberOfHosts(){
        return this.listOfEverything.size();
    }

    public int getIndexOfLocalHost()
    {
        for(int i = 0; i < this.listOfEverything.size(); i++){
            if(this.listOfEverything.get(i).getHostName().equals(serverName))
            {
                return i;
            }
        }
        return -1;
    }

}

