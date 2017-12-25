package com.apps.viscar.inventoryapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.viscar.inventoryapp.data.ShopContract.ShopEntry;

/**
 * Created by Tantawy on 12/18/2017.
 */

public class ShopCursorAdapter extends CursorAdapter {
    private Context mContext;
    private int quantity;
    private Uri currentProductUri;

    public ShopCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mContext = context;
        TextView nameTextView = view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        final TextView quantityTextView = view.findViewById(R.id.quantity);
        ImageView productView = view.findViewById(R.id.thumbnail);
        FloatingActionButton fab = view.findViewById(R.id.fab_sale);

        int nameColumnIndex = cursor.getColumnIndex(ShopEntry.SHOP_COLUMN_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ShopEntry.SHOP_COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ShopEntry.SHOP_COLUMN_QUANTITY);
        int imageColumnIndex = cursor.getColumnIndex(ShopEntry.SHOP_COLUMN_IMAGE);

        int id = cursor.getInt(cursor.getColumnIndex(ShopEntry._ID));
        Toast.makeText(context, "Id " + id, Toast.LENGTH_SHORT).show();
        final String name = cursor.getString(nameColumnIndex);
        final String price = "Price $" + String.valueOf(cursor.getFloat(priceColumnIndex));
        quantity = cursor.getInt(quantityColumnIndex);
        final String quantityString = "Quantity " + String.valueOf(quantity);

        final byte[] image = cursor.getBlob(imageColumnIndex);
        Bitmap thumbnail = getImage(image);
        nameTextView.setText(name);
        priceTextView.setText(price);
        quantityTextView.setText(quantityString);
        productView.setImageBitmap(thumbnail);
        currentProductUri = ContentUris.withAppendedId(ShopEntry.CONTENT_URI, id);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentResolver contentResolver = view.getContext().getContentResolver();
                ContentValues values = new ContentValues();
                if (quantity > 0) {
                    int currentQuantity = quantity;
                    values.put(ShopEntry.SHOP_COLUMN_QUANTITY, --currentQuantity);
                    int rowsAffected = contentResolver.update(currentProductUri, values, null, null);
                    mContext.getContentResolver().notifyChange(currentProductUri, null);
                    if (rowsAffected == 0) {
                        Toast.makeText(mContext, mContext.getString(R.string.sell_product_failed), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "Item out of stock", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

}
