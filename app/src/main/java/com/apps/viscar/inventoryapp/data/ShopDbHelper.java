package com.apps.viscar.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tantawy on 12/16/2017.
 */

public class ShopDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shop.db";
    private static final int DATABASE_VERSION = 1;

    public ShopDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE_SHOP = "CREATE TABLE " +
                ShopContract.ShopEntry.TABLE_NAME + "(" +
                ShopContract.ShopEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ShopContract.ShopEntry.SHOP_COLUMN_NAME + " TEXT NOT NULL," +
                ShopContract.ShopEntry.SHOP_COLUMN_PRICE + " TEXT NOT NULL," +
                ShopContract.ShopEntry.SHOP_COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0," +
                ShopContract.ShopEntry.SHOP_COLUMN_IMAGE + " BLOB NOT NULL" + ");";
        sqLiteDatabase.execSQL(CREATE_TABLE_SHOP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
