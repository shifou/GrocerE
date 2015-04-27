package edu.team7_18842cmu.dbutil;

/**
 * Created by Michael-Gao on 2015/3/29.
 */
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import edu.team7_18842cmu.StoredItem;
import edu.team7_18842cmu.activities.R;
import edu.team7_18842cmu.model.ItemInfo;


public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;



    public DBManager(Context context) {
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }

    /**
     * add persons
     */
    public void insert(String tableName, Object[] objects) {
        db.beginTransaction();
        try {
            //should have 2 elements in the objects array
            if(tableName.equals("peerInfo"))
                db.execSQL("INSERT INTO peerInfo VALUES(?, ?)", objects);
            //should have 5 elements in the objects array
            else if(tableName.equals("priceInfo"))
                db.execSQL("INSERT INTO priceInfo VALUES(null, ?, ?, ?, ?, ?)", objects);
            //should have 5 elements in the objects array
            else if(tableName.equals("priceHistory"))
                db.execSQL("INSERT INTO priceHistory VALUES(null, ?, ?, ?, ?, ?)", objects);
            else
                System.out.println("No Such Table");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    // This method take a list of "StoredItems" and inserts it into the database. This is called
    // when the app receives a response to a price query. The payload of the response is a
    // list of StoredItems.
    public void insertList(List<StoredItem> response){
        System.out.println("# Prices returned: " + response.size());
        for(int i = 0; i < response.size(); i++) {
            StoredItem item = response.get(i);
            ItemInfo itemInfo = new ItemInfo(item.getItemName().toString(),
                    item.getItemPrice().toString(), item.getItemStore().toString(),
                    item.getPurchaseDate().toString(), item.getItemSize().toString());
            insert("priceInfo", itemInfo.getAttributes());
            System.out.println("Inserted: " + item.getItemName().toString());

        }
    }


    // Locate item names in the database and return all matches
    public List<StoredItem> locateItem(String query) {

        Cursor c = db.rawQuery("SELECT * FROM priceInfo", null);
        List<StoredItem> results = new ArrayList<StoredItem>();
        int answerCount = 0;

        while (c.moveToNext()){
            String name = c.getString(c.getColumnIndex("itemName")).toLowerCase();
            if(name.contains(query.toLowerCase())) {
                results.add(answerCount, rowToObject(c));
                answerCount++;
            }
        }
        return results;
    }


    // This converts a row in the database to a StoredItem object
    public StoredItem rowToObject(Cursor c) {

        StoredItem item = new StoredItem();
        item.itemName = c.getString(c.getColumnIndex("itemName"));
        item.itemSize = c.getString(c.getColumnIndex("itemQuantity"));
        item.itemStore = c.getString(c.getColumnIndex("store"));
        String price = c.getString(c.getColumnIndex("itemPrice"));
        item.itemPrice = new BigDecimal(price);
        String date = c.getString(c.getColumnIndex("purchaseDate"));
        item.purchaseDate = new Date(date);

        return item;

    }

    // This take a list of StoredItems as an input. The output is another list of StoredItems
    // where any StoredItems that already exist in the database have been removed.
    public List<StoredItem> checkForDupes(List<StoredItem> input){

        List<StoredItem> output = new ArrayList<StoredItem>();
        Boolean duplicate = false;

        for(int i=0; i < input.size(); i++) {

            StoredItem candidate = input.get(i);
            String itemName = candidate.getItemName();
            List<StoredItem> matches = locateItem(itemName);

            for(int j=0; j < matches.size(); j++) {
                StoredItem existing = matches.get(j);

                if(candidate.isEqual(existing)){
                    System.out.println("Found a duplicate item!!!");
                    duplicate = true;
                    break;
                }

            }

            if(duplicate){
                duplicate = false;
                continue;
            } else {
                output.add(candidate);
            }
        }
        return output;
    }

    // This takes a list of StoredItems as an input, along with a list of the user's
    // store preferences. It returns a filtered list of StoredItems based on these preferences.
    // If the item is from a store not in the user's preferences, then it is removed from
    // the list.
    public List<StoredItem> checkStorePrefs(List<StoredItem> input, List<String> list){

        List<StoredItem> output = new ArrayList<StoredItem>();

        for(int i=0; i < input.size(); i++){
            StoredItem item = input.get(i);
            if(list.contains(item.getItemStore())){
                output.add(item);
            }
        }

        return output;
    }


    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}


