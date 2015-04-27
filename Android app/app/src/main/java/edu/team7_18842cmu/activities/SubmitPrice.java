package edu.team7_18842cmu.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.team7_18842cmu.dbutil.DBManager;
import edu.team7_18842cmu.model.ItemInfo;


public class SubmitPrice extends ActionBarActivity {
    private DBManager dbm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbm = new DBManager(this);

        // Set up the buttons for this menu
        setContentView(R.layout.activity_submit_price);
        Button button1 = (Button)findViewById(R.id.submitbutton);

        button1.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        submitPrice(v);
                    }
                }
        );

        // Set up a spinner based on the user's Store Preferences
        // User should only be able to submit prices for a store they prefer
        // to shop at
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter;
        List<String> list;

        list = new ArrayList<String>();
        list.add("Choose a store");
        if(getFromSP("cb1"))
            list.add(getResources().getString(R.string.store1));
        if(getFromSP("cb2"))
            list.add(getResources().getString(R.string.store2));
        if(getFromSP("cb3"))
            list.add(getResources().getString(R.string.store3));
        if(getFromSP("cb4"))
            list.add(getResources().getString(R.string.store4));

        adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, list);
        spinner.setAdapter(adapter);

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

    // This is needed to display the calendar the user will choose the date from instead
    // of having them manually type the date manually. This way, the date will always
    // be displayed in a consistent format.
    private class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            month++;
            String date = month + "/" + day + "/" + year;
            TextView selection = (TextView) findViewById(R.id.editText6);
            selection.setText(date);

        }
    }

    // This displays the date picker
    public void showDatePicker(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }


    // Here's the code that executes when the user clicks the submit price button.
    public void submitPrice(View view) {

        // Check if the user forgot to submit any fields. If they did, display a notification
        // which tells them what they still need to fill out.
        String missing_fields = checkForm();
        if(!missing_fields.isEmpty()){
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("The following fields are required:\n" + missing_fields)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        // If every submission field has data, the information can be submitted to the database
        } else {
            // Get the information that was written into the form
            EditText item = (EditText) findViewById(R.id.editText2);
            EditText quantity = (EditText) findViewById(R.id.editText1);
            Spinner store = (Spinner) findViewById(R.id.spinner1);
            EditText price = (EditText) findViewById(R.id.editText4);
            TextView date = (TextView) findViewById(R.id.editText6);

            // Format the information properly so the database can use it
            final ItemInfo itemInfo = new ItemInfo(item.getText().toString(), price.getText().toString(),
                    store.getSelectedItem().toString(), date.getText().toString(), quantity.getText().toString());

            // Notify the user the price for the item was submitted successfully
            new AlertDialog.Builder(this)
                    .setTitle("Status Message")
                    .setMessage("Price submitted!\n\nItem: " + item.getText())
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write the information to the database
                            dbm.insert("priceInfo", itemInfo.getAttributes());
                            clearForm(); // Clear the form so the next price can be submitted
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    // This checks every field in the form to make sure some data has been submitted. A string
    // is returned which contains the name of every field that is empty.
    public String checkForm() {

        String response = "";
        Boolean missing_flag = false;

        // Get the values in each field in the form.
        EditText item = (EditText) findViewById(R.id.editText1);
        EditText quantity = (EditText) findViewById(R.id.editText2);
        Spinner store = (Spinner) findViewById(R.id.spinner1);
        EditText price = (EditText) findViewById(R.id.editText4);
        TextView date = (TextView) findViewById(R.id.editText6);

        // Build the response string if any fields are missing in the form
        if(store.getSelectedItem().toString() == "Choose a store") {
                response += "Store name";
                missing_flag = true;
        }

        if(item.getText().toString().trim().length() == 0){
            if(!missing_flag) {
                response += "Item name";
                missing_flag = true;
            } else
            response += ", Item name";
        }

        if(quantity.getText().toString().trim().length() == 0){
            if(!missing_flag) {
                response += "Quantity";
                missing_flag = true;
            } else
                response += ", Quantity";
        }

        if(price.getText().toString().trim().length() == 0) {
            if (!missing_flag) {
                response += "Price";
                missing_flag = true;

            } else
                response += ", Price";
        }

        if(date.getText().toString().trim().length() == 0) {
            if (!missing_flag) {
                response += "Purchase date";
            } else
                response += ", Purchase date";
        }

        // Return the response string
        return response;
    }

    // This clears all of the fields in the Price Submission form, except for the store spinner.
    // The reason that won't be cleared is in the most common use case, the user will be submitting
    // prices consecutively from the same store.
    public void clearForm() {
        EditText item = (EditText) findViewById(R.id.editText1);
        EditText quantity = (EditText) findViewById(R.id.editText2);
        EditText price = (EditText) findViewById(R.id.editText4);
        TextView date = (TextView) findViewById(R.id.editText6);
        item.setText("");
        quantity.setText("");
        price.setText("");
        date.setText("");
    }

    // This gets the value of the key in the Store Preferences
    private boolean getFromSP(String key){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("PROJECT_NAME", android.content.Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }
}
