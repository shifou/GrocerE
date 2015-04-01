package edu.team7_18842cmu.dbutil;

/**
 * Created by Michael-Gao on 2015/3/29.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


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
    public void queryTest() {
        Cursor c = db.rawQuery("SELECT * FROM priceInfo", null);

        while (c.moveToNext()) {
            String s = "";
            s = s + "No."+ Integer.toString(c.getInt(c.getColumnIndex("_id"))) + " ";
            s = s + "Item Name:" + c.getString(c.getColumnIndex("itemName"))+ " ";
            s = s + "Item Price:" + c.getString(c.getColumnIndex("itemPrice"))+ " ";
            s = s + "Item Quantity" + c.getString(c.getColumnIndex("itemQuantity"))+ " ";
            s = s + "Store" + c.getString(c.getColumnIndex("store"))+ " ";
            s = s + "Purchase Date" + c.getString(c.getColumnIndex("purchaseDate"))+ " ";
            System.out.println(s);
        }
        c.close();
    }

    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}
