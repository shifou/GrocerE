package edu.team7_18842cmu.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

import edu.team7_18842cmu.Network.Message;
import edu.team7_18842cmu.NetworkService.MessagePasserService;
import edu.team7_18842cmu.StoredItem;
import edu.team7_18842cmu.dbutil.DBManager;
import edu.team7_18842cmu.model.AnswerAdapter;


public class RequestPrice extends ActionBarActivity {
    private DBManager dbm;
    MessagePasserService msgPasserService = null;
    public List<StoredItem> results = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbm = new DBManager(this);
        setContentView(R.layout.activity_request_price);

        Button button1 = (Button)findViewById(R.id.requestbutton);

        msgPasserService = (MessagePasserService)this.getIntent().getSerializableExtra("messagePasser");


        button1.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        requestPrice(v);
                    }
                }
        );
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

    public void requestPrice(View view) throws InterruptedException {
        final EditText item = (EditText) findViewById(R.id.editText5);

        if(item.getText().toString().trim().length() == 0){

            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("You must submit an item name.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {

//            List<StoredItem> results;
//            results = dbm.locateItem(item.getText().toString());
//
//
//            ListAdapter adapter = new AnswerAdapter(this,results);
//            ListView listView = (ListView) findViewById(R.id.answerList);
//            listView.setAdapter(adapter);


            Intent newIntent = new Intent(RequestPrice.this, MessagePasserService.class);
//            newIntent.putExtra("functionName","send");
//            Message newMessage = new Message ("N2", "Request", item.getText().toString());
            Bundle extras = new Bundle();
            extras.putString("itemRequest", item.getText().toString());
            extras.putString("functionName", "send");
            newIntent.putExtras(extras);
            startService(newIntent);
            wait(15000);
            
            results = dbm.locateItem(item.getText().toString());


            ListAdapter adapter = new AnswerAdapter(this,results);
            ListView listView = (ListView) findViewById(R.id.answerList);
            listView.setAdapter(adapter);


            item.setText("");

        }
    }


}

