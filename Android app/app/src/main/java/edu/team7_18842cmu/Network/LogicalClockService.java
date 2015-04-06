package edu.team7_18842cmu.Network;

/**
 * Created by Prabhanjan Batni on 05/04/2015.
 */


import java.util.ArrayList;

//TODO:
public class LogicalClockService extends ClockService
{
    //ArrayList<Long> lamportTimeStamp;

    public LogicalClockService()
    {
        super();
        lamportTimeStamp.add(new Long(1));
    }

    public LogicalClockService(TimeStamp timeStamp){
        super(timeStamp);
    }

    public ArrayList<Long> getTimeStamp()
    {
        return lamportTimeStamp;
    }

    public void clockTick() throws InterruptedException
    {
        this.getSemaphore().acquire();
        lamportTimeStamp.set(0, lamportTimeStamp.get(0)+1);
        this.getSemaphore().release();
    }

    //Call this just before sending message
    public void sendAction() throws InterruptedException
    {
        //Should be same as clockTick for now
        //Call this just before sending message
        this.getSemaphore().acquire();
        lamportTimeStamp.set(0, lamportTimeStamp.get(0)+1);
        this.getSemaphore().release();
    }

    //call this at inside receive()
    public void receiveAction(TimeStamp someoneElsesTimeStamp) throws InterruptedException
    {
        this.getSemaphore().acquire();
        if(lamportTimeStamp.get(0).compareTo(someoneElsesTimeStamp.timeStamp.get(0)) > 0)
        {
            lamportTimeStamp.set(0,lamportTimeStamp.get(0) +1);
        }
        else
        {
            lamportTimeStamp.set(0,someoneElsesTimeStamp.timeStamp.get(0) +1);
        }
        //lamportTimeStamp.set(0, Math.max(lamportTimeStamp.get(0), someoneElsesTimeStamp.timeStamp.get(0)) + 1);
        this.getSemaphore().release();
    }
    //Check all the compare between Long before using. please use .compareTo
	/*public static boolean happenedBefore(LogicalClockService l1, LogicalClockService l2)
	{
		try
		{
			l1.getSemaphore().acquire();
			l2.getSemaphore().acquire();
			if(l1.getTimeStamp().get(0) < l2.getTimeStamp().get(0))
			{
				l1.getSemaphore().release();
				l2.getSemaphore().release();
				return true;
			}
			else
			{
				l1.getSemaphore().release();
				l2.getSemaphore().release();
				return false;
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return false;
	}*/

    //Check all the compare between Long before using. please use .compareTo
	/*public boolean happenedBefore(TimeStamp timeStamp)
	{
		try
		{
			this.getSemaphore().acquire();
			if(this.getTimeStamp().get(0) < timeStamp.timeStamp.get(0))
			{
				this.getSemaphore().release();
				return true;
			}
			else
			{
				this.getSemaphore().release();
				return false;
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return false;
	}*/
}

