package edu.team7_18842cmu.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import edu.team7_18842cmu.dbutil.DBManager;


public class RequestPrice extends ActionBarActivity {
    private DBManager dbm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbm = new DBManager(this);
        setContentView(R.layout.activity_request_price);

        Button button1 = (Button)findViewById(R.id.requestbutton);

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

    public void requestPrice(View view) {
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
            String[] results;
            results = dbm.locateItem(item.getText().toString());
            item.setText("");


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, results);
            ListView listView = (ListView) findViewById(R.id.answerList);
            listView.setAdapter(adapter);

//            new AlertDialog.Builder(this)
//                    .setTitle("Status Message")
//                    .setMessage("Price requested!\n\nItem: " + item.getText())
//                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // continue
//
//
////                            dbm.queryTest();
//                            String[] results;
//                            results = dbm.locateItem(item.getText().toString());
//                            item.setText("");
//                            for(int i = 0; i < results.length; i++)
//                                Log.d("GrocerE", results[i]);
//
//                        }
//                    })
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .show();


        }
    }


}

