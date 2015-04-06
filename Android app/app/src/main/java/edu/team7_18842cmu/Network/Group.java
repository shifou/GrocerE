package edu.team7_18842cmu.Network;

/**
 * Created by Srinath on 05/04/2015.
 */


import java.util.ArrayList;

public class Group
{

    String groupName;
    ArrayList<String> groupMembers;
    public Group()
    {
        groupName = null;
        groupMembers = new ArrayList<String>();
    }
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public ArrayList<String> getGroupMembers() {
        return groupMembers;
    }
    public void setGroupMembers(ArrayList<String> groupMembers) {
        this.groupMembers = groupMembers;
    }
    public void addMember(String member)
    {
        this.groupMembers.add(member);
    }

}

