package edu.team7_18842cmu.Network;

/**
 * Created by Prabhanjan Batni on 05/04/2015.
 */

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


public class HostWithSocketAndStream
{
    String hostName;
    String ipAddr;
    int port;
    Socket socket;
    ObjectInputStream IS;
    ObjectOutputStream OS;
    BufferedReader br;
    PrintWriter pw;

    ArrayList<String> groupList;


    public BufferedReader getBr() {
        return br;
    }

    public void setBr(BufferedReader br) {
        this.br = br;
    }

    public PrintWriter getPw() {
        return pw;
    }

    public void setPw(PrintWriter pw) {
        this.pw = pw;
    }




    public String getHostName() {
        return hostName;
    }
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
    public String getIpAddr() {
        return ipAddr;
    }
    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public Socket getSocket() {
        return socket;
    }
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    public ObjectInputStream getIS() {
        return IS;
    }
    public void setIS(ObjectInputStream iS) {
        IS = iS;
    }
    public ObjectOutputStream getOS() {
        return OS;
    }
    public void setOS(ObjectOutputStream oS) {
        OS = oS;
    }
    public HostWithSocketAndStream(String hostName, String ipAddr, int port) {
        super();
        this.hostName = hostName;
        this.ipAddr = ipAddr;
        this.port = port;
        this.socket = null;
        this.IS = null;
        this.OS= null;
    }


}

