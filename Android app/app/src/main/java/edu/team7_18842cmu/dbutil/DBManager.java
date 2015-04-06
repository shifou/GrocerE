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
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import edu.team7_18842cmu.StoredItem;


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
        db.beginTransaction();  //开始事务
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
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    //return a which contains <Store : Price> for the given item
    //the given items might have different types. etc. milk(brand1) price:20 milk(brand2) price:30
    //return a list of the price, can be used to calculate average or median (TBD)
    public HashMap<String, List<Double>> getItemsInStores(List<String> stores, String tableName, String itemName){
        HashMap<String, List<Double>> result = new HashMap<String, List<Double>>();
        for(int i = 0 ; i < stores.size(); i++){
            Cursor c = db.rawQuery("SELECT * FROM " + tableName + " where store = "+ stores.get(i) + " and itemName = " + itemName, null);
            while (c.moveToNext()) {
                String store = c.getString(c.getColumnIndex("store"));
                Double price = Double.parseDouble(c.getString(c.getColumnIndex("itemPrice")));
                if(result.containsKey(store)) {
                    List<Double> ls = result.get(store);
                    ls.add(price);
                    result.put(store, ls);
                }
                else{
                    List<Double> ls = new ArrayList<Double>();
                    ls.add(price);
                    result.put(store, ls);
                }
            }
            c.close();
        }
        return result;

    }

    /**
     * query all items
     * @return List<String>
     */
//    public void queryTest() {
//        Cursor c = db.rawQuery("SELECT * FROM priceInfo", null);
//
//        while (c.moveToNext()) {
//           String s = rowToString(c);
//            Log.d("GrocerE", s );
//        }
//        c.close();
//    }

    // Locate item names in the database and return all matches
    public List<StoredItem> locateItem(String query) {
        Cursor c = db.rawQuery("SELECT * FROM priceInfo", null);
        int numRows = (int) DatabaseUtils.queryNumEntries(db, "priceInfo");
        List<StoredItem> results = new ArrayList<StoredItem>();
        int answerCount = 0;

        while (c.moveToNext()){
            String name = c.getString(c.getColumnIndex("itemName")).toLowerCase();
            if (name.contains(query.toLowerCase())) {
                results.add(answerCount, rowToObject(c));
                answerCount++;
            }
        }
        return results;
    }

    // Convert a row in the item database to a string
//    public String rowToString(Cursor c) {
//        String s = "";
//        s += "No."+ Integer.toString(c.getInt(c.getColumnIndex("_id")));
//        s += " Item Name:" + c.getString(c.getColumnIndex("itemName"));
//        s += " Item Price:" + c.getString(c.getColumnIndex("itemPrice"));
//        s += " Item Quantity:" + c.getString(c.getColumnIndex("itemQuantity"));
//        s += " Store:" + c.getString(c.getColumnIndex("store"));
//        s += " Purchase Date:" + c.getString(c.getColumnIndex("purchaseDate"));
//        return s;
//    }

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

    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}

