package edu.team7_18842cmu.Network;

/**
 * Created by Srinath on 05/04/2015.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;

public class ReceiveHandler implements Runnable{

    private Socket client;
    MessagePasser MP;
    ObjectInputStream IS;
    HostWithSocketAndStream hostTemp;

    public ReceiveHandler(Socket client, MessagePasser MP)
    {
        this .client = client;
        this.MP = MP;
    }

    public void run()
    {
        try
        {
            System.out.println("ReceiveHandler started with name:"+Thread.currentThread().getName());

            //Here we need to match the client source of the clientSocket to the destName in listofEverything
            for (HostWithSocketAndStream host: MP.listOfEverything)
            {
                //client.
                if(host.ipAddr.equals(client.getInetAddress().getHostAddress().toString()))//and clientPorts are not equal

                {
                    //Here we have matched the host in listOfeverything to this client socket that has connected
                    System.out.println("Found the host in listOfEverything ");
                    host.setSocket(client);
                    hostTemp = host;
                    //System.out.println("Creating an input stream for "+host.hostName+"@"+host.ipAddr+":"+host.port);
                    if (host.IS == null)
                    {
                        IS = new ObjectInputStream(client.getInputStream());
                        host.setIS(IS);
                        System.out.println("Input stream created for "+host.hostName+"@"+host.ipAddr+":"+host.port);
                    }
                    if (host.OS == null)
                    {
                        host.setOS(new ObjectOutputStream(client.getOutputStream()));
                        System.out.println("Output stream created for "+host.hostName+"@"+host.ipAddr+":"+host.port);
                    }



                    //Create new output stream
                    //host.setOS(new ObjectOutputStream(client.getOutputStream()));
                    break;
                }
            }


            while (true)
            {
                //System.out.println("Just before receiving message ie about to call readObject() ");
                Message msg = (Message) hostTemp.IS.readObject();
                //TODO: set receive timestamp for the message
                //System.out.println("Just after receiving message ");
                System.out.println("Message Received: ");
                System.out.println("************************************");
                //System.out.println("|  original src:       " + msg.maekawaUltimateSource);
                System.out.println("|  src:                " + msg.sourceNodeName);
                System.out.println("|  dest:               " + msg.destinationNodeName);
                System.out.println("|  seqNum:             " + msg.SeqNum);
                System.out.println("|  type:               " + msg.MessageType);
                //System.out.println("|  Maekawa field:      " + msg.maekawaField);
                System.out.println("|  Target Group:       " + msg.targetGroupName);
                System.out.println("|  content:            " + (String)msg.getPayload());
                System.out.println("|  timestamp:          " + msg.timestamp.toString());
                System.out.println("************************************");
                MP.receiveQueue.add(msg);
            }
            //doReceiveStuff();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
