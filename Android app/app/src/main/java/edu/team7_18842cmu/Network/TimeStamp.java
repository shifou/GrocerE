package edu.team7_18842cmu.Network;

/**
 * Created by Prabhanjan Batni on 05/04/2015.
 */
import java.io.Serializable;
import java.util.ArrayList;


public class TimeStamp implements Serializable
{
    ArrayList<Long> timeStamp;
    public TimeStamp(ArrayList<Long> timeStamp)
    {
        this.timeStamp = timeStamp;
    }
    public String toString()
    {
        String tsString = "";
        tsString += "<" ;
        for(int i = 0; i < this.timeStamp.size(); i++)
        {
            if(i != this.timeStamp.size() - 1)
                tsString += this.timeStamp.get(i).toString() + ",";
            else
                tsString += this.timeStamp.get(i).toString();
        }
        tsString += ">";
        return tsString;
    }
    public static boolean equal(TimeStamp ts1, TimeStamp ts2)
    {
        if(ts1.timeStamp.size() != ts2.timeStamp.size())
            return false;
        else{
            for(int i = 0; i < ts1.timeStamp.size(); i ++)
            {
                if(!ts1.timeStamp.get(i).equals(ts2.timeStamp.get(i)))
                {
                    return false;
                }
            }
            return true;
        }
    }
    public static boolean happenedBefore(TimeStamp ts1, TimeStamp ts2)
    {
        if(equal(ts1,ts2))
            return false;
        if(ts1.timeStamp.size() != ts2.timeStamp.size())
            return false;
        else{
            for(int i = 0; i < ts1.timeStamp.size(); i ++)
            {
                if(ts1.timeStamp.get(i).compareTo(ts2.timeStamp.get(i)) > 0)
                    return false;
            }
            return true;
        }

    }

}

