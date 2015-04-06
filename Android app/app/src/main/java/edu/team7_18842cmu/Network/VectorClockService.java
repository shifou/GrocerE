package edu.team7_18842cmu.Network;

/**
 * Created by Srinath on 05/04/2015.
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

    public VectorClockService(TimeStamp timeStamp){
        super(timeStamp);
    }
    //Check all the compare between Long before using. please use .compareTo
/*	public static boolean happenedBefore(VectorClockService v1, VectorClockService v2)
	{
		try
		{
			v1.getSemaphore().acquire();
			v2.getSemaphore().acquire();
			if(v1.getClockVector().size() != v2.getClockVector().size())
			{
				v1.getSemaphore().release();
				v2.getSemaphore().release();
				return false;
			}
			for(int i = 0; i < v1.getClockVector().size(); i++)
			{
				if(v1.lamportTimeStamp.get(i) > v2.lamportTimeStamp.get(i))
				{
					v1.getSemaphore().release();
					v2.getSemaphore().release();
					return false;
				}
			}

			v1.getSemaphore().release();
			v2.getSemaphore().release();

			return true;
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return false;
	}*/

    //Check all the compare between Long before using. please use .compareTo
/*	public boolean happenedBefore(TimeStamp timeStamp)
	{
		try
		{
			this.getSemaphore().acquire();
			if(this.getClockVector().size() != timeStamp.timeStamp.size())
			{
				this.getSemaphore().release();
				return false;
			}
			for(int i = 0; i < this.getClockVector().size(); i++)
			{
				if(this.lamportTimeStamp.get(i) > timeStamp.timeStamp.get(i))
				{
					this.getSemaphore().release();
					return false;
				}
			}

			this.getSemaphore().release();
			return true;
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return false;
	}*/


    public void sendAction()
    {
        try
        {
            semaphore.acquire();
            this.lamportTimeStamp.set(index, this.lamportTimeStamp.get(index)+1);
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
