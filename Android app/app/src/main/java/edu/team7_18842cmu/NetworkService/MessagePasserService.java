package edu.team7_18842cmu.NetworkService;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.Serializable;

import edu.team7_18842cmu.Network.Message;
import edu.team7_18842cmu.Network.MessagePasserX;
import edu.team7_18842cmu.dbutil.DBManager;

//public class MessagePasserService extends IntentService {
public class MessagePasserService extends Service {

    private static String configFile;
    private static String clockOption;
    private static String nodeName;
    static MessagePasserX msgPasser;
    public  DBManager dbm;

    @Override
    public void onCreate() {
        // The service is being created
        dbm = new DBManager(this);
        System.out.println("DODODODODO");
                //TODO: Init message passer here
        configFile = "http://pastebin.com/raw.php?i=whdf6rBa";
        clockOption = "vector";
        nodeName = "nodeName";
        try {
            msgPasser = new MessagePasserX(configFile,nodeName,clockOption, dbm);
            System.out.println("Made a new message passer");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()

               String function = intent.getStringExtra("functionName");
       String item = intent.getStringExtra("itemRequest");

        if(function != null) {
            if (function.equals(new String("send")))
            {
                //Get send params from intent
                Message msg = new Message ("192.168.2.3", "Request", item);
//                Message msg = (Message) intent.getSerializableExtra("messageObject");
                System.out.println("Destination Node Name" + msg.getDestinationNodeName());
                System.out.println("Query: " + item);
                System.out.println("Msg Payload: " + msg.getPayload());
                try {
                    msgPasser.send(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (function.equals(new String("receive")))
            {
                Message recvMsg = msgPasser.receive();
                if (recvMsg!=null)
                {
                    System.out.println("The next received message is: ("+ (String)recvMsg.getDestinationNodeName()+","+ (String) recvMsg.getMessageType());
                    String callingActivity = intent.getStringExtra("callingActivity");

                    //Get the class name of the sender
                    Class callerClass = null;
                    try {
                        callerClass = Class.forName(callingActivity);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    //Send an intent back to caller
                    //Note: this will fail if the above catch is triggered
                    //TODO: have receivers listen to this
                    //TODO: pretty sure this is already an explicit intent. but need to verify
                    Intent replyIntent = new Intent(this,callerClass);
                    startActivity(replyIntent);
                }
                else
                {
                    System.out.println("No message to receive...");
                }
            }
            return 0;
        }

      return -1;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        return null;
    }


    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
    }
}