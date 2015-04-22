package edu.team7_18842cmu.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.Collections;
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
    boolean boxChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbm = new DBManager(this);
        setContentView(R.layout.activity_request_price);
        CheckBox cb1;

        Button button1 = (Button)findViewById(R.id.requestbutton);



        msgPasserService = (MessagePasserService)this.getIntent().getSerializableExtra("messagePasser");


        button1.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            requestPrice(v);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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


            if(((CheckBox) findViewById(R.id.offlineCheckbox)).isChecked()) {

                results = dbm.locateItem(item.getText().toString());
                ListAdapter adapter = new AnswerAdapter(RequestPrice.this,results);
                ListView listView = (ListView) findViewById(R.id.answerList);
                EditText itemField = (EditText) findViewById(R.id.editText5);
                itemField.setText("");
                listView.setAdapter(adapter);

            } else {

                getResponses task = new getResponses();
                task.execute(item.getText().toString());
                item.setText("Fetching prices for \"" + item.getText() + "\"");
                Button button = (Button)RequestPrice.this.findViewById(R.id.requestbutton);
                button.setEnabled(false);
                button.setBackgroundColor( -65536);
                button.setText("Waiting");

            }
        }
    }

    private class getResponses extends AsyncTask<String, Void, List<StoredItem>> {
        @Override
        protected List<StoredItem> doInBackground(String... item) {
            Intent newIntent = new Intent(RequestPrice.this, MessagePasserService.class);
            List<StoredItem> results = null;
            Bundle extras = new Bundle();
            extras.putString("itemRequest", item[0]);
            extras.putString("functionName", "send");
            newIntent.putExtras(extras);
            startService(newIntent);
            SystemClock.sleep(15000);
            results = dbm.locateItem(item[0]);
            Collections.sort(results);


            return results;
        }

        protected void onPostExecute(List<StoredItem> results) {
            ListAdapter adapter = new AnswerAdapter(RequestPrice.this,results);
            ListView listView = (ListView) findViewById(R.id.answerList);
            EditText item = (EditText) findViewById(R.id.editText5);
            item.setText("");
            Button button = (Button)RequestPrice.this.findViewById(R.id.requestbutton);
            button.setEnabled(true);
            button.setBackgroundColor(-16711936);
            button.setText("Request");
            listView.setAdapter(adapter);
        }
    }


}