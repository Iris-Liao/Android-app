package com.iris.android.inventorymaster.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by iris on 2017-03-02.
 */

public class ProductFeedHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Inventory.db";

    public ProductFeedHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES = "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + " ( " +
                ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL ," +
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER + " TEXT NOT NULL ," +
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL ," +
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL ," +
                ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE + " BLOB NOT NULL );";
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    //only when newVersion >oldVersion, this method will be executed
    //and if you only want to add a new column to the table, you don't need to drop table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + ProductContract.ProductEntry.TABLE_NAME);
        onCreate(db);
    }
}
