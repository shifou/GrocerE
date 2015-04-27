package edu.team7_18842cmu.Network;

/**
 * Created by Prabhanjan Batni on 05/04/2015.
 */


import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class VectorClockService extends ClockService
{
    int index;
    //ArrayList<Long> lamportTimeStamp;
    //Semaphore  semaphore;

    public int getIndex()
    {
        return index;
    }
    public void setIndex(int index)
    {
        this.index = index;
    }
    public ArrayList<Long> getClockVector()
    {
        return lamportTimeStamp;
    }
    public void setClockVector(ArrayList<Long> clockVector)
    {
        this.lamportTimeStamp = clockVector;
    }
    public Semaphore getSemaphore()
    {
        return semaphore;
    }

    public VectorClockService(int length, int index)
    {
        super();
        for(int i = 0; i < length; i ++){
            this.lamportTimeStamp.add((long) 0);
        }
        this.index = index;
    }


    public void sendAction()
    {
        try
        {
            semaphore.acquire();
//            this.lamportTimeStamp.set(index, this.lamportTimeStamp.get(index)+1);
            semaphore.release();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    public void clockMerge(VectorClockService vc)
    {
        try
        {
            semaphore.acquire();
            vc.getSemaphore().acquire();
            for(int i = 0; i < this.lamportTimeStamp.size(); i ++){
                if(vc.lamportTimeStamp.get(i) > this.lamportTimeStamp.get(i))
                    this.lamportTimeStamp.set(i, vc.lamportTimeStamp.get(i));
            }
            vc.getSemaphore().release();
            semaphore.release();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void receiveAction(TimeStamp someoneElsesTimeStamp) throws InterruptedException
    {
        //increase the timeStamp first
        this.sendAction();
        //then update the timeStamp with someone else's timeStamp
        this.getSemaphore().acquire();
        for(int i = 0; i < this.lamportTimeStamp.size(); i ++){
            if(someoneElsesTimeStamp.timeStamp.get(i).compareTo(this.lamportTimeStamp.get(i)) > 0)
                this.lamportTimeStamp.set(i, someoneElsesTimeStamp.timeStamp.get(i));
        }
        this.getSemaphore().release();
    }

    public ArrayList<Long> getTimeStamp()
    {
        return this.lamportTimeStamp;
    }

}
