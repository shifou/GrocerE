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


public class ChangeStores extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_stores);

        Button savebutton = (Button)findViewById(R.id.savebutton);

        savebutton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        savePrefs(v);
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

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.store1checkbox:
                if (checked) {
                    CheckBox cb = (CheckBox) findViewById(R.id.store1checkbox);
                    saveStore(cb.getText().toString(), true);
                }
                else
                // Other stuff
                break;
            case R.id.store2checkbox:
                if (checked) {
                    CheckBox cb = (CheckBox) findViewById(R.id.store2checkbox);
                    saveStore(cb.getText().toString(), true);
                }
                else
                // Other stuff
                break;
            case R.id.store3checkbox:
                if (checked) {
                    CheckBox cb = (CheckBox) findViewById(R.id.store3checkbox);
                    saveStore(cb.getText().toString(), true);
                }
                else
                // Other stuff
                break;
            case R.id.store4checkbox:
                if (checked) {
                    CheckBox cb = (CheckBox) findViewById(R.id.store4checkbox);
                    saveStore(cb.getText().toString(), true);
                }
                else
                // Other stuff
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
}
