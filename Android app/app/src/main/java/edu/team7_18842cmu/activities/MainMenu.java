package edu.team7_18842cmu.activities;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import edu.team7_18842cmu.Network.Message;
import edu.team7_18842cmu.NetworkService.MessagePasserService;


public class MainMenu extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Set up the buttons for the Main Menu
        Button button1 = (Button)findViewById(R.id.button1);
        Button button2 = (Button)findViewById(R.id.button2);
        Button button3 = (Button)findViewById(R.id.button3);
        Button button4 = (Button)findViewById(R.id.button4);

        // Start the MessagePasser service in its own thread
        Intent msgPasserIntent = new Intent(this, MessagePasserService.class);
        Thread t = new Thread(new MsgPasserStarter(msgPasserIntent));
        t.start();


        // What will happen when the user click on a UI button
        button1.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        submitPrice(v);
                    }
                }
        );

        button2.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        requestPrice(v);
                    }
                }
        );

        button3.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        changeStores(v);
                    }
                }
        );

        button4.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        closeApplication(v);
                    }
                }
        );


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void submitPrice(View view) {
        Intent intent = new Intent(this, SubmitPrice.class);
        startActivity(intent);
    }

    public void requestPrice(View view) {
        Intent intent = new Intent(this, RequestPrice.class);
        startActivity(intent);
    }

    public void changeStores(View view) {
        Intent intent = new Intent(this, ChangeStores.class);
        startActivity(intent);
    }

    public void closeApplication(View view) {
        System.out.println("$$$$$$$$$$$$$$$");
        Intent newIntent = new Intent(this, MessagePasserService.class);
        Bundle extras = new Bundle();
        extras.putString("functionName", "teardown");
        newIntent.putExtras(extras);
        Button button = (Button)MainMenu.this.findViewById(R.id.button4);
        button.setText("Exiting...Please Wait");
        startService(newIntent);
        finish();
    }

    public class MsgPasserStarter implements Runnable
    {
        Intent intent;

        public MsgPasserStarter(Intent i)
        {
            intent = i;
        }
        @Override
        public void run() {
            startService(intent);
        }
    }

}
