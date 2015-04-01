package edu.team7_18842cmu.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;


import java.util.Calendar;

import edu.team7_18842cmu.dbutil.DBManager;
import edu.team7_18842cmu.model.ItemInfo;


public class SubmitPrice extends ActionBarActivity {
    private DBManager dbm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbm = new DBManager(this);
        setContentView(R.layout.activity_submit_price);
        Button button1 = (Button)findViewById(R.id.submitbutton);

        button1.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        submitPrice(v);
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
            TextView selection = (TextView) findViewById(R.id.textView6);
            selection.setText(date);

        }
    }

    public void showDatePicker(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");

    }


    public void submitPrice(View view) {
        EditText item = (EditText) findViewById(R.id.editText1);
        EditText quantity = (EditText) findViewById(R.id.editText2);
        EditText store = (EditText) findViewById(R.id.editText3);
        EditText price = (EditText) findViewById(R.id.editText4);
        TextView date = (TextView) findViewById(R.id.textView6);
        final ItemInfo itemInfo = new ItemInfo(item.getText().toString(), price.getText().toString(),
                                    store.getText().toString(), date.getText().toString(), quantity.getText().toString());
        if(itemInfo.checkForm()){
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Missing a required field")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {

            new AlertDialog.Builder(this)
                    .setTitle("Status Message")
                    .setMessage("Price submitted!\n\nItem: " + item.getText())
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dbm.insert("priceInfo", itemInfo.getAttributes());
                            clearForm();
                            dbm.queryTest();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }


    }



    public void clearForm() {
        EditText item = (EditText) findViewById(R.id.editText1);
        EditText quantity = (EditText) findViewById(R.id.editText2);
        EditText store = (EditText) findViewById(R.id.editText3);
        EditText price = (EditText) findViewById(R.id.editText4);
        TextView date = (TextView) findViewById(R.id.textView6);
        item.setText("");
        quantity.setText("");
        store.setText("");
        price.setText("");
        date.setText("");
    }
}
