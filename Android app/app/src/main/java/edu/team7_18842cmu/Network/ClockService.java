package edu.team7_18842cmu.Network;

/**
 * Created by Prabhanjan on 05/04/2015.
 */


import java.util.ArrayList;
import java.util.concurrent.Semaphore;


public abstract class ClockService
{
    //MessagePasser msgPasser;
    Semaphore semaphore;
    ArrayList<Long> lamportTimeStamp;

    public ClockService(){
        this.semaphore = new Semaphore(1);
        lamportTimeStamp = new ArrayList<Long>();
    }

    public ClockService(TimeStamp timeStamp){
        this.lamportTimeStamp = timeStamp.timeStamp;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }
    public void setSemaphore(Semaphore semaphore) {
        this.semaphore = semaphore;
    }
    public abstract ArrayList<Long> getTimeStamp();
    public abstract void sendAction() throws InterruptedException;
    public abstract void receiveAction(TimeStamp someoneElsesTimeStamp) throws InterruptedException;
    //public abstract boolean happenedBefore(TimeStamp timeStamp);

}
