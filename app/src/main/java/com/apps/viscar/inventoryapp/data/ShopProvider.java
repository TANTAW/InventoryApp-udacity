package com.apps.viscar.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.apps.viscar.inventoryapp.data.ShopContract.ShopEntry;

import static android.content.ContentValues.TAG;

/**
 * Created by Tantawy on 12/17/2017.
 */

public class ShopProvider extends ContentProvider {

    private static final int SHOP = 100;
    private static final int SHOP_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ShopContract.CONTENT_AUTHORITY, ShopContract.PATH_SHOP, SHOP);
        sUriMatcher.addURI(ShopContract.CONTENT_AUTHORITY, ShopContract.PATH_SHOP + "/#", SHOP_ID);
    }

    private ShopDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ShopDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case SHOP:
                cursor = database.query(ShopEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SHOP_ID:
                selection = ShopEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ShopEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown Uri" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case SHOP:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }

    private Uri insertProduct(Uri uri, ContentValues contentValues) {
        String name = contentValues.getAsString(ShopEntry.SHOP_COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name ");
        }
        Integer quantity = contentValues.getAsInteger(ShopEntry.SHOP_COLUMN_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Product requires a valid quantity ");
        }
        Float price = contentValues.getAsFloat(ShopEntry.SHOP_COLUMN_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Product requires valid price ");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(ShopEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Toast.makeText(getContext(), "Failed to insert row for " + uri, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case SHOP:
                rowsDeleted = database.delete(ShopEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SHOP_ID:
                selection = ShopEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ShopEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsUpdated;
        if (contentValues == null) {
            throw new IllegalArgumentException("cannot update empty values ");
        }
        switch (match) {
            case SHOP:
                rowsUpdated = database.update(ShopEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case SHOP_ID:
                selection = ShopEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = database.update(ShopEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
        return rowsUpdated;
    }
}
