package edu.team7_18842cmu.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import android.content.SharedPreferences;
import android.widget.CompoundButton;


public class ChangeStores extends ActionBarActivity implements CompoundButton.OnCheckedChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_stores);

        CheckBox cb1,cb2,cb3,cb4;

        cb1 = (CheckBox)findViewById(R.id.store1checkbox);
        cb1.setChecked(getFromSP("cb1"));
        cb1.setOnCheckedChangeListener(this);
        cb2 = (CheckBox)findViewById(R.id.store2checkbox);
        cb2.setChecked(getFromSP("cb2"));
        cb2.setOnCheckedChangeListener(this);
        cb3 = (CheckBox)findViewById(R.id.store3checkbox);
        cb3.setChecked(getFromSP("cb3"));
        cb3.setOnCheckedChangeListener(this);
        cb4 = (CheckBox)findViewById(R.id.store4checkbox);
        cb4.setChecked(getFromSP("cb4"));
        cb4.setOnCheckedChangeListener(this);
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

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // Is the view now checked?
        boolean checked = ((CheckBox) buttonView).isChecked();

        // Check which checkbox was clicked
        switch(buttonView.getId()) {
            case R.id.store1checkbox:
                if (checked) {
                    CheckBox cb1 = (CheckBox) findViewById(R.id.store1checkbox);
                    saveStore("cb1", true);
                }
                else
                // Other stuff
                    saveStore("cb1", false);
                break;
            case R.id.store2checkbox:
                if (checked) {
                    CheckBox cb2 = (CheckBox) findViewById(R.id.store2checkbox);
                    saveStore("cb2", true);
                }
                else
                // Other stuff
                    saveStore("cb2", false);
                break;
            case R.id.store3checkbox:
                if (checked) {
                    CheckBox cb3 = (CheckBox) findViewById(R.id.store3checkbox);
                    saveStore("cb3", true);
                }
                else
                // Other stuff
                    saveStore("cb3", false);
                break;
            case R.id.store4checkbox:
                if (checked) {
                    CheckBox cb4 = (CheckBox) findViewById(R.id.store4checkbox);
                    saveStore("cb4", true);
                }
                else
                // Other stuff
                    saveStore("cb4", false);
                break;
        }
    }

    public void savePrefs(View view) {
            new AlertDialog.Builder(this)
                    .setTitle("Status Message")
                    .setMessage("Store preferences saved!")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();



    }

    private void saveStore(String key,boolean value){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("PROJECT_NAME", android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private boolean getFromSP(String key){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("PROJECT_NAME", android.content.Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

}
