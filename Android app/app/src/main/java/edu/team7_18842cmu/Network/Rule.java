package edu.team7_18842cmu.Network;

/**
 * Created by Srinath on 05/04/2015.
 */
public class Rule
{
    //Flag to say if the file rule is for send or receive
    String sendOrReceive;
    String action,src,dest,kind;
    String duplicate;
    int seqNum;


    public Rule(String sendOrReceive, String action, String src, String dest,
                String kind, int seqNum, String duplicate) {
        super();
        this.sendOrReceive = sendOrReceive;
        this.action = action;
        this.src = src;
        this.dest = dest;
        this.kind = kind;
        this.seqNum = seqNum;
        this.duplicate = duplicate;
    }

    public Rule(String action, String src, String dest, String kind, int seqNum, String duplicate) {
        super();
        this.action = action;
        this.src = src;
        this.dest = dest;
        this.kind = kind;
        this.seqNum = seqNum;
        this.duplicate = duplicate;
    }

    public String getDuplicate(){
        return this.duplicate;
    }

    public void setDuplicate(String duplicate){
        this.duplicate = duplicate;
    }

    public String getSendOrReceive() {
        return sendOrReceive;
    }

    public void setSendOrReceive(String sendOrReceive) {
        this.sendOrReceive = sendOrReceive;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }



}


