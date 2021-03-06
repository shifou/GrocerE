package edu.team7_18842cmu.NetworkService;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.text.format.Formatter;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;

import edu.team7_18842cmu.Network.BMulticast;
import edu.team7_18842cmu.Network.HostWithSocketAndStream;
import edu.team7_18842cmu.Network.Message;
import edu.team7_18842cmu.Network.MessagePasserX;
import edu.team7_18842cmu.dbutil.DBManager;

//public class MessagePasserService extends IntentService {
public class MessagePasserService extends Service {

    private static String configFile;
    private static String clockOption;
    private static String nodeName;
    static MessagePasserX msgPasser;
    public  DBManager dbm;
    Calendar c= Calendar.getInstance();
    public long time = 0;

    @Override
    public void onCreate() {
//        The service is being created comment
        dbm = new DBManager(this);
        //TODO: Init message passer here
        // This pastebin file is how the node knows learns where the BootstrapNode is.
        configFile = "http://pastebin.com/raw.php?i=whdf6rBa";
        clockOption = "vector";
        nodeName = "nodeName";
        try {
            msgPasser = new MessagePasserX(configFile,nodeName,clockOption, dbm);
            System.out.println("Made a new message passer");

            WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            int ip = wifiInfo.getIpAddress();
            String ipAddress = Formatter.formatIpAddress(ip);
            String payload = "{\"Type\":0,\"Mid\":\""+ ipAddress+ "\",\"Ipaddr\":\""+ ipAddress + "\",\"Port\":12000,\"Peers\":\"0\"}";
            Message msg = new Message("BootstrapNode","server", payload);
            msgPasser.send(msg);
            time = c.getTimeInMillis();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()

        String function = intent.getStringExtra("functionName");
        String item = intent.getStringExtra("itemRequest");
        System.out.println("Started in message passer");

        if(function != null) {

            if (function.equals(new String("send")))
            {
                c= Calendar.getInstance();
                
                    //Refresh the peerlistjust in case
                    InetAddress address = null;
                    try {
                        address = InetAddress.getByName("");
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }

                WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                int ip = wifiInfo.getIpAddress();
                String ipAddress = Formatter.formatIpAddress(ip);

                    String payload = "{\"Type\":0,\"Mid\":\""+ ipAddress+ "\",\"Ipaddr\":\""+ ipAddress + "\",\"Port\":12000,\"Peers\":\"0\"}";
                    Message msg = new Message("BootstrapNode", "server", payload);
                    try {
                        msgPasser.send(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //Now we wait to mke sure the peerlist has been updated
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                /*
                BMulticast bm = new BMulticast(msgPasser);
                WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
                bm.multicast(item,wifiMgr);
                */

                //Get all the current connections from messagePasserX's masterList and send the request out
                for (HostWithSocketAndStream host : msgPasser.listOfEverything) {
                    if(host.getHostName().equals("BootstrapNode"))
                        continue;
                    String dest = host.getHostName();
                    Message msg2 = new Message(dest, "Request", item);
                    System.out.println("---------------------SourceNode name is:" + dest + "--------------------------------------------");
                    msg2.setSourceNodeName(ipAddress);

                    System.out.println("---------------------Node name is:" + dest + "--------------------------------------------");
                    if (!dest.equals("BootstrapNode")) {

                        System.out.println("Destination Node Name (should not be bootstrap)" + msg2.getDestinationNodeName());
                        System.out.println("Query: " + item);
                        System.out.println("Msg Payload: " + msg2.getPayload());
                        try {
                            msgPasser.send(msg2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            else if(function.equals(new String("teardown"))){

                System.out.println("Made it to teardown");
                WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                int ip = wifiInfo.getIpAddress();
                String ipAddress = Formatter.formatIpAddress(ip);
                String payload = "{\"Type\":3,\"Mid\":\""+ ipAddress+ "\",\"Ipaddr\":\""+ ipAddress + "\",\"Port\":12000,\"Peers\":\"0\"}";
                Message msg = new Message("BootstrapNode", "server", payload);
                try {
                    msgPasser.send(msg);
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                System.exit(0);
                stopSelf();
//                android.os.Process.killProcess(android.os.Process.myPid());



            }

//            else if (function.equals(new String("receive")))
//            {
//                Message recvMsg = msgPasser.receive();
//                if (recvMsg!=null)
//                {
//                    System.out.println("The next received message is: ("+ (String)recvMsg.getDestinationNodeName()+","+ (String) recvMsg.getMessageType());
//                    String callingActivity = intent.getStringExtra("callingActivity");
//
//                    //Get the class name of the sender
//                    Class callerClass = null;
//                    try {
//                        callerClass = Class.forName(callingActivity);
//                    } catch (ClassNotFoundException e) {
//                        e.printStackTrace();
//                    }
//
//                    //Send an intent back to caller
//                    //Note: this will fail if the above catch is triggered
//                    //TODO: have receivers listen to this
//                    //TODO: pretty sure this is already an explicit intent. but need to verify
//                    Intent replyIntent = new Intent(this,callerClass);
//                    startActivity(replyIntent);
//                }
//                else
//                {
//                    System.out.println("No message to receive...");
//                }
//            }
            return 0;
        }

      return -1;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        return null;
    }


    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}