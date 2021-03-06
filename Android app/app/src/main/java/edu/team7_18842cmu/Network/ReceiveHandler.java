package edu.team7_18842cmu.Network;

/**
 * Created by Prabhanjan Batni on 05/04/2015.
 */

import android.content.Context;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Queue;

import edu.team7_18842cmu.NetworkService.MessagePasserService;
import edu.team7_18842cmu.StoredItem;
import edu.team7_18842cmu.activities.RequestPrice;
import edu.team7_18842cmu.dbutil.DBManager;

public class ReceiveHandler implements Runnable{

    private Socket client;
    MessagePasserX MP;
    ObjectInputStream IS;
    HostWithSocketAndStream hostTemp;
    DBManager dbm;


    public ReceiveHandler(Socket client, MessagePasserX MP, DBManager dbm)
    {
        this .client = client;
        this.MP = MP;
        this.dbm = dbm;
    }

    public void run()
    {
        Boolean flag_clientIsInPeerlistWhenConnected = true;
        try
        {
            System.out.println("ReceiveHandler started with name:"+Thread.currentThread().getName());

            //Here we need to match the client source of the clientSocket to the destName in listofEverything
            for (HostWithSocketAndStream host: MP.listOfEverything)
            {
                //Checking for matches in the listOfeverything.
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
                    flag_clientIsInPeerlistWhenConnected = true;
                    break;
                }

                else
                {
                    flag_clientIsInPeerlistWhenConnected = false;


                }


            }

            if (flag_clientIsInPeerlistWhenConnected.equals(false))
            {
                //The socket is not in the list of everything, so we add it
                HostWithSocketAndStream newHost = new HostWithSocketAndStream(client.getInetAddress().getHostAddress().toString(),client.getInetAddress().getHostAddress().toString(),12000);
                newHost.setSocket(client);



                System.out.println("Did NOT find the host in listOfEverything so we are creating it");
                newHost.setSocket(client);
                hostTemp = newHost;
                //System.out.println("Creating an input stream for "+host.hostName+"@"+host.ipAddr+":"+host.port);
                if (newHost.IS == null)
                {
                    IS = new ObjectInputStream(client.getInputStream());
                    newHost.setIS(IS);
                    System.out.println("Input stream created for "+newHost.hostName+"@"+newHost.ipAddr+":"+newHost.port);
                }
                if (newHost.OS == null)
                {
                    newHost.setOS(new ObjectOutputStream(client.getOutputStream()));
                    System.out.println("Output stream created for "+newHost.hostName+"@"+newHost.ipAddr+":"+newHost.port);
                }

                MP.listOfEverything.add(newHost);



                //Create new output stream
                //host.setOS(new ObjectOutputStream(client.getOutputStream()));
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
//                System.out.println("|  content:            " + (String)msg.getPayload());
                System.out.println("|  timestamp:          " + msg.timestamp.toString());
                System.out.println("************************************");
                MP.receiveQueue.add(msg);
                /*if(msg.getMessageType().equals("Request")) {
                    List<StoredItem> results;
                    results = dbm.locateItem((String) msg.getPayload());
//                    StringBuffer response = new StringBuffer();
//                    for (int i = 0; i < results.size(); i++) {
//                        response.append("ItemName "+ results.get(i).getItemName() + ":" + "Price "+ results.get(i).getItemPrice() + ",");
//                    }
                    Message newMsg = new Message("192.168.2.3", "Response", results);
                    MP.send(newMsg);
                }*/

                if(msg.getMessageType().equals("Request")) {
                    List<StoredItem> results;
                    results = dbm.locateItem((String) msg.getPayload());
                    /*StringBuffer response = new StringBuffer();
                    for (int i = 0; i < results.size(); i++) {
                        response.append("ItemName "+ results.get(i).getItemName() + ":" + "Price "+ results.get(i).getItemPrice() + ",");
                    }*/
                    Message newMsg = new Message(msg.sourceNodeName, "Response", results);
                    MP.send(newMsg);
                }


                if(msg.getMessageType().equals("Response")) {
                    System.out.println("$$$ Handling a response message $$$");
                    List<StoredItem> adds = (List<StoredItem>)msg.getPayload();
                    adds = dbm.checkForDupes(adds);
                    dbm.insertList(adds);



                }

            }
            //doReceiveStuff();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}

