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
    public ArrayList<HostWithSocketAndStream> listOfEverything = new ArrayList<HostWithSocketAndStream>();

    //Queues for sending and receiving
    Queue<Message> receiveQueue;

    //Thread for receiver
    Thread receiverThread;

    //PB may need to change
    int seqNumGlobal;

    //Port number of this Node
    public String serverIP;
    protected int serverPort;
    public String serverName;

    //Socket of this Node
    protected ServerSocket serverSocket;

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

        //PB: Set seq number and increment
        System.out.println("Made it to send");
        message.setSeqNum(seqNumGlobal);
        seqNumGlobal++;
        //message.setSourceNodeName(serverIP);
        //Set the source name
//        message.setSourceNodeName(this.getNameByIpAddrAndPort(serverIP, serverPort));

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



    //Read config file and parse and save the config state
    //YAML stuff here...populate currentRuleSet
    void loadConfigFile()
    {
        //update modification time
        //TODO: Changed here...just for reference
        //File configFile = new File(configFileName);
        //lastModifyTime = configFile.lastModified();
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

        }

        System.out.println("List of Hosts is: ");
        for(HostWithSocketAndStream host: listOfEverything){
            System.out.println("name=" + host.getHostName() + ";" +
                    "ip=" + host.getIpAddr() + ";" +
                    "port=" + host.getPort() + ";");
        }
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
                try
                {

                    host.OS.writeObject(message);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
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
                        //Create new input stream
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
                    host.getPw().flush();

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

