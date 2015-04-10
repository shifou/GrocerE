package edu.team7_18842cmu.NetworkService;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import edu.team7_18842cmu.Network.Message;
import edu.team7_18842cmu.Network.MessagePasserX;

public class MessagePasserService extends IntentService {



    static MessagePasserX msgPasser = null;

    private static String configFile;
    private static String clockOption;
    private static String nodeName;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MessagePasserService(String name) {
        super(name);
    }


    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onCreate()
    {
        super.onCreate();

        //TODO: Init message passer here
        configFile = "http://pastebin.com/raw.php?i=9tBQGEpT";
        clockOption = "vector";
        nodeName = "nodeName";
        try {
            msgPasser = new MessagePasserX(configFile,nodeName,clockOption);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void onDestroy()
    {
        super.onDestroy();
        //TODO: Exit all connections
    }

    public int onStartCommand(Intent intent, int flags, int startID)
    {
       String function = intent.getStringExtra("functionName");

       if (function.equals(new String("send")))
       {
           //Get send params from intent
            Message msg = (Message) intent.getSerializableExtra("messageObject");
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
    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
