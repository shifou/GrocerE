package edu.team7_18842cmu.Network;

/**
 * Created by Srinath on 05/04/2015.
 */
import java.io.Serializable;


public class Message implements Serializable
{

    String destinationNodeName;
    String sourceNodeName;
    int SeqNum;
    Boolean flag_duplicate;
    String MessageType;
    Object Payload;
    TimeStamp timestamp;
    Boolean isToBeLogged = true;
    String targetGroupName;

    public Message(Message m)
    {
        this.destinationNodeName = m.destinationNodeName;
        this.sourceNodeName = m.sourceNodeName;
        this.SeqNum = m.SeqNum;
        this.flag_duplicate = m.flag_duplicate;
        this.MessageType = m.MessageType;
        this.Payload = m.Payload;
        this.timestamp = m.timestamp;
        this.targetGroupName = m.targetGroupName;
        this.isToBeLogged = m.isToBeLogged;
    }

    public Message(String dest, String kind, Object data)
    {
        this.destinationNodeName = dest;
        this.MessageType = kind;
        this.Payload = data;
        this.flag_duplicate = false;
    }

    public Message(String src,String dest, String kind, Object data, TimeStamp t)
    {
        this.sourceNodeName = src;
        this.destinationNodeName = dest;
        this.MessageType = kind;
        this.Payload = data;
        this.flag_duplicate = false;
        this.timestamp = t;
    }

    public Message(String dest, String kind, Object data, Boolean bool)
    {
        this.destinationNodeName = dest;
        this.MessageType = kind;
        this.Payload = data;
        this.flag_duplicate = false;
        this.isToBeLogged = bool;
    }

    public void setTimestamp(TimeStamp timestamp) {
        this.timestamp = timestamp;
    }
    public TimeStamp getTimestamp() {
        return timestamp;
    }

    public String getDestinationNodeName() {
        return destinationNodeName;
    }

    public void setDestinationNodeName(String destinationNodeName) {
        this.destinationNodeName = destinationNodeName;
    }

    public String getSourceNodeName(){
        return sourceNodeName;
    }

    public void setSourceNodeName(String sourceNodeName){
        this.sourceNodeName = sourceNodeName;
    }

    public int getSeqNum() {
        return SeqNum;
    }

    public void setSeqNum(int seqNum) {
        SeqNum = seqNum;
    }

    public void incSeqNum()
    {
        SeqNum++;
    }

    public Boolean getFlag_duplicate() {
        return flag_duplicate;
    }

    public void setFlag_duplicate(Boolean flag_duplicate) {
        this.flag_duplicate = flag_duplicate;
    }

    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String messageType) {
        MessageType = messageType;
    }

    public Object getPayload() {
        return Payload;
    }

    public void setPayload(String payload) {
        Payload = payload;
    }
}


