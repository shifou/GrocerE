package edu.team7_18842cmu.Network;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import edu.team7_18842cmu.StoredItem;
import edu.team7_18842cmu.dbutil.DBManager;

/**
 * Created by Michael-Gao on 2015/4/18.
 */
public class ServerReceiveHandler implements Runnable{
    private Socket client;
    MessagePasserX MP;
    BufferedReader br;
    HostWithSocketAndStream hostTemp;
    DBManager dbm;

    public ServerReceiveHandler(Socket client, MessagePasserX MP, DBManager dbm)
    {
        this .client = client;
        this.MP = MP;
        this.dbm = dbm;
    }

    public void run()
    {
        try
        {
            System.out.println("Server ReceiveHandler started with name:"+Thread.currentThread().getName());

            //Here we need to match the client source of the clientSocket to the destName in listofEverything
            for (HostWithSocketAndStream host: MP.listOfEverything)
            {
                //client.
                if(host.ipAddr.equals(client.getInetAddress().getHostAddress().toString()))//and clientPorts are not equal
                {
                    //Here we have matched the host in listOfeverything to this client socket that has connected
                    System.out.println("Found the host in listOfEverything ");
                    host.setSocket(client);

                    //System.out.println("Creating an input stream for "+host.hostName+"@"+host.ipAddr+":"+host.port);
                    if (host.br == null)
                    {
                        InputStream is=client.getInputStream();
                        BufferedReader br=new BufferedReader(new InputStreamReader(is));
                        host.setBr(br);
                        System.out.println("Input stream created for "+host.hostName+"@"+host.ipAddr+":"+host.port);
                    }
                    if (host.pw == null)
                    {
                        OutputStream os=client.getOutputStream();
                        PrintWriter pw=new PrintWriter(os);
                        host.setPw(pw);
                        System.out.println("Output stream created for "+host.hostName+"@"+host.ipAddr+":"+host.port);
                    }
                    hostTemp = host;
                    //Create new output stream
                    //host.setOS(new ObjectOutputStream(client.getOutputStream()));
                    break;
                }
            }


            while (true)
            {
                //System.out.println("Just before receiving message ie about to call readObject() ");
                String msg = "";
                String line = "";
                while((line = hostTemp.br.readLine()) != null){
                    msg += line;
                    break;
                }


                //TODO: set receive timestamp for the message
                //System.out.println("Just after receiving message ");
                System.out.println("Message Received: ");
                System.out.println("************************************");

                System.out.println(msg);
                System.out.println("************************************");

                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(msg);

                String peerName = (String) jsonObject.get("Peers");
                String[] Addrs = peerName.split("\\+");
                for(int i=0; i < Addrs.length; i++) {
                    if(Addrs[i].trim().equals(""))
                        continue;
                    System.out.println("Addrs[" + i + "] =" + Addrs[i]);
                    HostWithSocketAndStream host = new HostWithSocketAndStream(Addrs[i],Addrs[i],12000);


                    //MP.listOfEverything.add(host);

                    //Here we check if the IP address already exists in the listOfEverything
                    boolean duplicate = false;
                    for (HostWithSocketAndStream hst: MP.listOfEverything)
                    {

                        if (hst.hostName.equals(host.hostName))
                        {
                            //We have a duplicate
                            duplicate = true;
                            break;
                        }
                    }

                    if (duplicate == false)
                    {
                        MP.listOfEverything.add(host);
                    }
                }
//                for(int i = 0 ; i < peerList.length; i++){
//                    Object[] objects = new Object[2];
//                    String[] info = peerList[i].split(",");
//                    objects[0] = info[0];
//                    objects[1] = info[1];
//                    dbm.insert("peerInfo",objects);
//                }
            }
            //doReceiveStuff();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
