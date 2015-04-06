package edu.team7_18842cmu.Network;

/**
 * Created by Srinath on 05/04/2015.
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

        import org.yaml.snakeyaml.Yaml;


public class Multicast {

    private static String configFile;
    private static String clockOption;
    private static String processName;

    static MessagePasser msgPasser = null;
    //static ClockService clock = null;

    static ArrayList<Group> groupList;
    public static ArrayList<Message> multicastReceiveQueue;

    public static ArrayList<Message> listOfMessagesAwaitingACKs;

    public static ArrayList<Message> listOfMulticasts;

    public static ArrayList<Message> listToBeLogged;

    static Thread thread;

    public static void main(String[] args) throws IOException, Exception
    {


        //configFile = args[0];
        //processName = args[1];
        configFile = "//C:\\Users\\Srinath\\Dropbox\\Share with Congshan\\config.txt";
        processName = "N1";

        //Our clock should always be vector in lab2
        clockOption = "vector";

        multicastReceiveQueue = new ArrayList<Message>();
        listOfMessagesAwaitingACKs = new ArrayList<Message>();
        listOfMulticasts = new ArrayList<Message>();
        listToBeLogged = new ArrayList<Message>();

        //DONE: read config file and construct a list of groups
        initGroupListFromConfigFile();


        while(true)
        {
            Scanner in = new Scanner(System.in);
            String option;

            System.out.print("\n-----------------------------------------------------------------------------------------------"
                    + "\n                1) Initialize MessagePasser with Receiver functionality"
                    + "\n                2) Construct Message Object and send to remote Nodes "
                    + "\n                3) Receive ie process the next message in the receiveQueue "
                    + "\n                4) Increase clock for arbitarary internal event "
                    + "\n                5) Print Clock "
                    + "\n                6) Multicast"
                    + "\n                7) Print Logger"
                    + "\nEnter your option number: ");

            option =in.nextLine();
            //System.out.println(option);

            if (option.equals("1"))
            {
                try
                {
                    //System.out.println("Starting...");
                    //C:\\Users\\Srinath\\Dropbox\\CMU\\4) DS\\Homeworks\\Lab 0\\config.txt
                    msgPasser = new MessagePasser(configFile,processName,clockOption);

                    //Start multicast loop
                    thread = new Thread(new MulticastReceiveLoop());
                    thread.start();


                    //System.out.println("Ending...");

                    continue;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            else if (option.equals("2"))
            {
                System.out.print("\nPlease enter destination name: ");
                String destName = in.nextLine();

                System.out.print("\nPlease enter message kind : ");
                String kind = in.nextLine();

                System.out.print("\nPlease enter message string : ");
                String message = in.nextLine();

                System.out.print("\nDo you want to log this message?  :  ");
                String toBeLogged = in.nextLine();

                Boolean bool;
                if (toBeLogged.equals("Y"))
                {
                    bool = true;
                }
                else
                {
                    bool = false;
                }
                Message msg = new Message(destName,kind,message,bool);

                try
                {
                    msgPasser.send(msg);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            else if (option.equals("3"))
            {
                Message recvMsg = msgPasser.receive();
                if(recvMsg!=null)
                    System.out.println("The next received message is: ("+ (String)recvMsg.destinationNodeName+","+(String)recvMsg.MessageType+","+(String)recvMsg.Payload+") , seqNum: "+ recvMsg.SeqNum);
                else
                    System.out.println("No message to recieve...");
            }

            else if (option.equals("4"))
            {
                System.out.println("Clock is being incremented");
                msgPasser.clock.sendAction();
                System.out.println("New clock is: " + msgPasser.clock.getTimeStamp().toString());
            }

            else if (option.equals("5"))
            {
                System.out.println("Current clock is: " + msgPasser.clock.getTimeStamp().toString());

            }
            else if (option.equals("6"))//Multicast
            {
                //TODO: get user input for message and groupName
                System.out.print("\nPlease enter destination group: ");
                String destGroup = in.nextLine();

                System.out.print("\nPlease enter message kind : ");
                String kind = in.nextLine();

                System.out.print("\nPlease enter message string : ");
                String message = in.nextLine();

                //System.out.print("\nDo you want to log this message?  :  ");
                String toBeLogged = "Y";

                Boolean bool;
                if (toBeLogged.equals("Y"))
                {
                    bool = true;
                }
                else
                {
                    bool = false;
                }
                Message msg = new Message(destGroup,kind,message,bool);
                msg.targetGroupName = destGroup;
                //TODO: call multicastSend(Message,Group)
                multicastSend(msg,destGroup);
            }

            else if (option.equals("7"))// Logger
            {
                //TODO: Causal Ordering...FIFO implementation of the vector clock
                //Provide group list
                System.out.println("Local host is in the following groups: ");
                for(Group group: groupList)
                {
                    for(String member: group.getGroupMembers())
                    {
                        if(member.equals(processName))
                        {
                            System.out.println(group.getGroupName() + " ");
                        }
                    }
                }
                System.out.println("Please indicate the group you'd like to log: ");
                String groupToBeLogged = in.nextLine();
                ArrayList<Message> subList = new ArrayList<Message>();
                for(Message msg: listToBeLogged)
                {
                    if(msg.targetGroupName.equals(groupToBeLogged))
                    {
                        Message temp = new Message(msg);

                        subList.add(temp);
                    }

                }

                checkMessages(subList);


            }
        }

    }

    private static void checkMessages(ArrayList<Message> listOfMessages)
    {
        System.out.println("Just entered checkMessages");
        if(listOfMessages.isEmpty())
        {
            System.out.println("List of messages is empty!!!!!!!");
        }
        for(int i = 0; i < listOfMessages.size(); i++)
        {
            Message msg1 = listOfMessages.get(i);
            TimeStamp ts1 = msg1.getTimestamp();
            for(int j = i+1; j < listOfMessages.size(); j++)
            {
                Message msg2 = listOfMessages.get(j);
                TimeStamp ts2 = msg2.getTimestamp();

                if( TimeStamp.equal(ts1, ts2) )
                {
                    System.out.print("\n(Src:" + msg1.getSourceNodeName()
                            + "; Dst:" + msg1.getDestinationNodeName()
                            + "; Event:" + msg1.getPayload().toString()
                            + "; SeqNum:" + msg1.getSeqNum()
                            + "; TimeStamp:" + ts1.toString());
                    System.out.print( ")  ============  (" );
                    System.out.print("Src:" + msg2.getSourceNodeName()
                            + "; Dst:" + msg2.getDestinationNodeName()
                            + "; Event:" + msg2.getPayload().toString()
                            + "; SeqNum:" + msg2.getSeqNum()
                            + "; TimeStamp:" + ts2.toString() + ")");
                }
                else if( TimeStamp.happenedBefore(ts1, ts2) )
                {
                    System.out.print("\n(Src:" + msg1.getSourceNodeName()
                            + "; Dst:" + msg1.getDestinationNodeName()
                            + "; Event:" + msg1.getPayload().toString()
                            + "; SeqNum:" + msg1.getSeqNum()
                            + "; TimeStamp:" + ts1.toString());
                    System.out.print( ")  ------------>  (" );
                    System.out.print("Src:" + msg2.getSourceNodeName()
                            + "; Dst:" + msg2.getDestinationNodeName()
                            + "; Event:" + msg2.getPayload().toString()
                            + "; SeqNum:" + msg2.getSeqNum()
                            + "; TimeStamp:" + ts2.toString() + ")");
                }
                else if(TimeStamp.happenedBefore(ts2, ts1))
                {

                    System.out.print("\n(Src:" + msg2.getSourceNodeName()
                            + "; Dst:" + msg2.getDestinationNodeName()
                            + "; Event:" + msg2.getPayload().toString()
                            + "; SeqNum:" + msg2.getSeqNum()
                            + "; TimeStamp:" + ts2.toString() );
                    System.out.print( ")  ------------>  (" );
                    System.out.print("Src:" + msg1.getSourceNodeName()
                            + "; Dst:" + msg1.getDestinationNodeName()
                            + "; Event:" + msg1.getPayload().toString()
                            + "; SeqNum:" + msg1.getSeqNum()
                            + "; TimeStamp:" + ts1.toString() + ")");

                }
                else
                {
                    System.out.print("\n(Src:" + msg1.getSourceNodeName()
                            + "; Dst:" + msg1.getDestinationNodeName()
                            + "; Event:" + msg1.getPayload().toString()
                            + "; SeqNum:" + msg1.getSeqNum()
                            + "; TimeStamp:" + ts1.toString());
                    System.out.print( ")  ------||-----  (" );
                    System.out.print("Src:" + msg2.getSourceNodeName()
                            + "; Dst:" + msg2.getDestinationNodeName()
                            + "; Event:" + msg2.getPayload().toString()
                            + "; SeqNum:" + msg2.getSeqNum()
                            + "; TimeStamp:" + ts2.toString() + ")");
                }
            }
        }
    }

    public static void initGroupListFromConfigFile()
    {
        groupList = new ArrayList<Group>();

        Yaml yaml = new Yaml();
        File file = new File(configFile);
        InputStream input = null;

        try
        {
            input = new FileInputStream(file);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        Map<String, ArrayList> data = (LinkedHashMap<String, ArrayList>) yaml.load(input);

        for(Entry<String, ArrayList> entry: data.entrySet() )
        {
            if(entry.getKey().equalsIgnoreCase("groups"))
            {
                ArrayList<Object> array = entry.getValue();
                if(array == null)
                    break;
                for(Object obj: array)
                {
                    Map<String, Object> map = (LinkedHashMap<String, Object>) obj;
                    //Group Initialization
                    Group group = new Group();
                    group.setGroupName((String)map.get("name"));
                    ArrayList<String> members = (ArrayList<String>) map.get("members");
                    for(String member:members)
                    {
                        group.addMember(member);
                    }
                    groupList.add(group);
                }
            }
        }

        for(Group group: groupList)
        {
            System.out.println("Group name: " + group.getGroupName());
            System.out.println("Members: ");
            for(String member: group.getGroupMembers())
            {
                System.out.println("\t" + member);
            }
        }
    }

    public static void multicastSend(Message message, String groupName)
    {
        Group group = null;
        //TODO: get Group from groupName
        //System.out.println("Getting group from Group name");
        group = getGroupFromGroupName(groupName);
        //System.out.println("Group is: " + group.groupName);
        //We have group
        if (group != null)
        {
            //System.out.println("Group is not null");
            for(String destinationHost: group.groupMembers)
            {
                //System.out.println("Current group member is" + destinationHost);
                //TODO: get Host from host(Group.groupMembers contains the name of Hosts inside this group)
                //Do we really need the above line?? we have the destination name already
                //Create new message
                //System.out.println("Creating new message");
                Message mess = new Message(message);
                mess.setDestinationNodeName(destinationHost);
                Boolean sent = false;

                System.out.println("Calling send to destination: " + mess.destinationNodeName);
                //TODO: Call send (message,destinationHost)
                try
                {
                    Message m = new Message(mess);
                    listToBeLogged.add(m);
                    sent = msgPasser.send(mess);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                //TODO: if send successful, add message to the listOfMessagesAwaitingACKs
                if (sent)
                {
                    listOfMessagesAwaitingACKs.add(mess);
                }

                //TODO: if message.src belongs to group(Otherwise there's no need to add)
                //System.out.println("Checking if node " + mess.sourceNodeName + " belongs to group " + group.groupName);
                if (checkNodeNameBelongsToGroup(mess.sourceNodeName,group.groupName))
                {
                    //TODO: if message.kind == multicast, listOfMulticasts.add(message)
                    //System.out.println("It does belong");
                    if(mess.getMessageType().equals("MULTICAST"))
                    {
                        //System.out.println("Adding to list of multicasts");
                        listOfMulticasts.add(mess);
                    }
                }
            }
        }


    }


    public static Boolean checkNodeNameBelongsToGroup(String nodeName, String groupName)
    {
        Group group = getGroupFromGroupName(groupName);
        for (String hostName: group.groupMembers)
        {
            if(hostName.equals(nodeName))
            {
                return true;
            }

        }

        return false;

    }
    public static Group getGroupFromGroupName(String groupName)
    {
        //System.out.println("Just entered getGroupFromGroupName with parameter: " + groupName);
        for(Group group: groupList)
        {
            //System.out.println("Group name: " + group.groupName);
            if(group.groupName.equals(groupName))
            {
                //System.out.println("group matched");
                return group;
            }
        }
        return null;

    }

    public static class MulticastReceiveLoop implements Runnable
    {

        public MulticastReceiveLoop()
        {
            super();
        }

        @Override
        public void run()
        {
            while(true)//Infinite loop
            {
                //TODO: keep polling processedReceiveQueue and add to multicastReceiveQueue
                //System.out.println("Just before listOfmessages is populated");
                while(true)//run till receiveQueue is empty
                {
                    Message msg = msgPasser.receive();
                    if(msg != null)
                    {
                        multicastReceiveQueue.add(msg);
                        //System.out.println("Just added a message to list of Messages!!!!! :D :D :D");
                    }
                    else
                    {
                        break;
                    }
                }
                //System.out.println("Just after listOfmessages is populated");

                //TODO: process the received messages
                if(!multicastReceiveQueue.isEmpty())
                {
                    //System.out.println("Just about to remove a message out of multicastQueue");
                    Message mess = multicastReceiveQueue.remove(0);
                    //System.out.println("Successfully removed out of multicastQueue");
                    //TODO: if msg.isToBeLogged==true, add to listToBeLogged
                    if(mess.isToBeLogged == true)
                    {
                        System.out.println("Message is to be logged");
                        Message temp = new Message(mess);
                        listToBeLogged.add(temp);
                    }
                    //TODO: check kind, payload ie the event, targetGroupName
                    //TODO: if ACK, check listOfMessagesAwaitingACKs...
                    if(mess.MessageType.equals("ACK"))
                    {
                        System.out.println("Messaage is of type ACK");
                        //Message is ACK
                        for (Message ackMessage: listOfMessagesAwaitingACKs)
                        {
                            //match event
                            if (ackMessage.Payload.equals(mess.Payload))
                            {
                                //check if you belongs to the targetGroupName. CORRECTION: we dont need this

                                //match mess.src to ackMessage.dest
                                if(mess.sourceNodeName.equals(ackMessage.destinationNodeName))
                                {
                                    //We have a match
                                    //We have received an ACK for a sent message,
                                    //so delete the message from listOfMessagesAwaitingACKs
                                    Boolean removeACKSuccessful = listOfMessagesAwaitingACKs.remove(ackMessage);
                                    System.out.println("removeACKSuccessfully = "+ removeACKSuccessful.toString());

                                    //also, delete this message from multicastReceiveQueue
                                    Boolean removeACKFromReceiveSuccessful = multicastReceiveQueue.remove(mess);
                                    System.out.println("removeACKFromReceiveSuccessfully = "+ removeACKFromReceiveSuccessful.toString());

                                    //break because we dont need to iterate thro this anymore
                                    break;
                                }


                            }
                        }


                    }
                    //TODO: if message.kind==Multicast,
                    else if (mess.MessageType.equals("MULTICAST"))
                    {
                        System.out.println("Message is of type MULTICAST");
                        //send ACK
						/*Message temp = new Message(mess);
						temp.setMessageType("ACK");
						temp.isToBeLogged = false;
						temp.setDestinationNodeName(mess.sourceNodeName);
						temp.setSourceNodeName(mess.destinationNodeName);
						temp.flag_duplicate = false;
						try
						{
							msgPasser.send(temp);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}*/

                        Boolean flag_alreadyInList = false;
                        //check listOfMulticasts
                        for(Message multicastMessage: listOfMulticasts)
                        {
                            //System.out.println("Message Type: " + mess.MessageType + " <-> " + multicastMessage.MessageType);
                            //System.out.println("Message Event: " + (String)mess.Payload + " <-> " + (String)multicastMessage.Payload);
                            //System.out.println("Message TargetGroup: " + mess.targetGroupName + " <-> " + multicastMessage.targetGroupName);
                            //match kind, event and targetGroup
                            if(multicastMessage.MessageType.equals(mess.MessageType)
                                    && multicastMessage.Payload.equals(mess.Payload)
                                    && multicastMessage.targetGroupName.equals(mess.targetGroupName))
                            {
                                //it is in the list, so it has already been broadcasted before so ignore
                                System.out.println("Message has been broadcasted before");
                                flag_alreadyInList = true;
                                break;
                            }
                            else
                            {
                                //TODO: Do nothing??? make sure if this is correct
                                //System.out.println("Multicast but in the list...Doing nothing");
                            }

                        }

                        // if not in the multicastlist
                        if(!flag_alreadyInList)
                        {
                            System.out.println("Multicast but NOT in the list so we are adding it");
                            //multicast message not in list
                            //add to listOfMulticasts
                            listOfMulticasts.add(mess);
                            //if source == itself, ignore
                            //if not, multicastSend(message,Group)
                            if(!(mess.sourceNodeName.equals(msgPasser.serverName)))
                            {
                                Message msgTemp = new Message(mess);
                                //msgTemp.setSourceNodeName(mess.destinationNodeName);//Not needed because msgpasser sets it.
                                System.out.println("Calling multicast send again to be reliable");
                                multicastSend(mess,mess.targetGroupName);
                            }

                        }


                        //delete message from multicastReceiveQueue
                        //multicastReceiveQueue.remove(mess);
                    }

                    else
                    {
                        //TODO: else (default case)
                        //This means that this message is meant for this destination.
                        System.out.println("Default case ie not MULTICAST...should not come here");

                        //TODO: Send ACK
						/*Message temp = new Message(mess);
						temp.setMessageType("ACK");
						temp.isToBeLogged = false;
						temp.setDestinationNodeName(mess.sourceNodeName);
						temp.setSourceNodeName(mess.destinationNodeName);
						temp.flag_duplicate = false;
						try
						{
							msgPasser.send(temp);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}*/

                        //TODO: print message maybe???
                    }


                    //mess = multicastReceiveQueue.remove(0);
                }


            }

        }

    }

}
