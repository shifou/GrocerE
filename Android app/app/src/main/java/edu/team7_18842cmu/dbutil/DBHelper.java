package edu.team7_18842cmu.dbutil;

/**
 * Created by Michael-Gao on 2015/3/29.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "androidNode.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        //Use default CursorFactory
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS peerInfo" +
                "(userID INTEGER PRIMARY KEY," +
                "ipAddress VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS priceInfo" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "itemName VARCHAR, itemPrice VARCHAR, itemQuantity VARCHAR," +
                "store VARCHAR, purchaseDate VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS priceHistory" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "itemName VARCHAR, itemPrice VARCHAR, itemQuantity VARCHAR," +
                "store VARCHAR, purchaseDate VARCHAR)");
    }

    //if DATABASE_VERSION changed to 2,system detect different version of db, onUpgrade will be called
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //To be added
    }
}
