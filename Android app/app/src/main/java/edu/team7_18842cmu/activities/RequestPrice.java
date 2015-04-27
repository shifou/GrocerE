package edu.team7_18842cmu.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.team7_18842cmu.NetworkService.MessagePasserService;
import edu.team7_18842cmu.StoredItem;
import edu.team7_18842cmu.dbutil.DBManager;
import edu.team7_18842cmu.model.AnswerAdapter;


public class RequestPrice extends ActionBarActivity {

    private DBManager dbm;
    MessagePasserService msgPasserService = null;
    public List<StoredItem> results = null;
    List<String> list = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbm = new DBManager(this);
        setContentView(R.layout.activity_request_price);
        Button button1 = (Button)findViewById(R.id.requestbutton);

        // Get the stored StorePreferences. This will be used
        // to filter what results are displayed for the user.
        list.clear();
        if(getFromSP("cb1"))
            list.add(getResources().getString(R.string.store1));
        if(getFromSP("cb2"))
            list.add(getResources().getString(R.string.store2));
        if(getFromSP("cb3"))
            list.add(getResources().getString(R.string.store3));
        if(getFromSP("cb4"))
            list.add(getResources().getString(R.string.store4));

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

        // Get the string submitted by the user
        final EditText item = (EditText) findViewById(R.id.editText5);

        // If the user made an empty request for an item, handle it
        // with a notification that they can't do that.
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

            // If we have an item name, make the request

            // What to do if the user checked the box to only query the local database
            if(((CheckBox) findViewById(R.id.offlineCheckbox)).isChecked()) {

                // Get the local results, filter them, sort them
                results = dbm.locateItem(item.getText().toString());
                results = dbm.checkStorePrefs(results,list);
                Collections.sort(results);

                // Display the results from the local database
                ListAdapter adapter = new AnswerAdapter(RequestPrice.this,results);
                ListView listView = (ListView) findViewById(R.id.answerList);
                EditText itemField = (EditText) findViewById(R.id.editText5);
                itemField.setText("");          // Clear the text field used to make the request

                // Use the adapter to display the properly formatted results
                listView.setAdapter(adapter);

            } else {    // This is the version of request that multicasts the request to the peers

                // Set up the Async task and pass it the request string
                getResponses task = new getResponses();
                task.execute(item.getText().toString());

                // Change the text field to display what we're searching for
                item.setText("Fetching prices for \"" + item.getText() + "\"");

                // Change how the request button is displayed and disable it until the
                // request is complete
                Button button = (Button)RequestPrice.this.findViewById(R.id.requestbutton);
                button.setEnabled(false);
                button.setBackgroundColor( -65536);
                button.setText("Waiting");

            }
        }
    }

    // When a price request is sent to the peer list, it's handled in the following Async task
    private class getResponses extends AsyncTask<String, Void, List<StoredItem>> {

        @Override
        protected List<StoredItem> doInBackground(final String... item) {

            // Send the message passer the item we're looking for via the following intent
            Intent newIntent = new Intent(RequestPrice.this, MessagePasserService.class);
            Bundle extras = new Bundle();
            extras.putString("itemRequest", item[0]);
            extras.putString("functionName", "send");
            newIntent.putExtras(extras);
            startService(newIntent);

            // Wait 15 seconds for messages to come in before updating the UI
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Submit a query to the local database. Since the data from any incoming
            // messages is replicated to the local database, this will display the results
            // so long as they arrive in time. Also need to sort and filter the results.
            results = dbm.locateItem(item[0]);
            results = dbm.checkStorePrefs(results,list);    // filter based on Store Preferences
            Collections.sort(results);                      // Sort based on lowest price
            return results;
        }

        // This is executed at the end of the Async task. Basically, it updates the UI with
        // the results and sets up the screen for the next request.
        protected void onPostExecute(List<StoredItem> results) {
            // Prep the adapter to properly display the results
            ListAdapter adapter = new AnswerAdapter(RequestPrice.this,results);
            ListView listView = (ListView) findViewById(R.id.answerList);
            EditText item = (EditText) findViewById(R.id.editText5);
            item.setText("");       // Clear the request input field

            // Re-enable the disabled request button and restore the display formatting
            Button button = (Button)RequestPrice.this.findViewById(R.id.requestbutton);
            button.setEnabled(true);
            button.setBackgroundColor(-16711936);
            button.setText("Request");

            // Launch the adapter to display the results
            listView.setAdapter(adapter);
        }
    }

    // This returns the key value for the Store Preference
    private boolean getFromSP(String key){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("PROJECT_NAME", android.content.Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }


}