package edu.team7_18842cmu.Network;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

/**
 * Created by Michael-Gao on 2015/4/24.
 */
public class BMulticast {
    MessagePasserX msgPasser = null;

    public BMulticast(MessagePasserX msgPasser){
        this.msgPasser = msgPasser;
    }

    public void multicast(String item,  WifiManager wifiMgr){
        for (HostWithSocketAndStream host : msgPasser.listOfEverything) {
            if(host.getHostName().equals("BootstrapNode"))
                continue;
            String dest = host.getHostName();
            Message msg2 = new Message(dest, "Request", item);
            //msg2.setSourceNodeName(msgPasser.serverName);

            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            int ip = wifiInfo.getIpAddress();
            String ipAddress = Formatter.formatIpAddress(ip);
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
}
