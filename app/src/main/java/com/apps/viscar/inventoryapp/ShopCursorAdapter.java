package com.apps.viscar.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
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
        ImageView imageView = view.findViewById(R.id.thumbnail);
        FloatingActionButton fabSell = view.findViewById(R.id.fab_sale);

        int name_index = cursor.getColumnIndex(ShopEntry.SHOP_COLUMN_NAME);
        int price_index = cursor.getColumnIndex(ShopEntry.SHOP_COLUMN_PRICE);
        int quantity_index = cursor.getColumnIndex(ShopEntry.SHOP_COLUMN_QUANTITY);
        byte[] mImageByteArray;
        mImageByteArray = cursor.getBlob(cursor.getColumnIndex(ShopEntry.SHOP_COLUMN_IMAGE));
        Bitmap productImage = ImageHelper.convertBlobToBitmap(mImageByteArray);
        final String productName = cursor.getString(name_index);
        final String productQuantity = cursor.getString(quantity_index);
        final String productPrice = cursor.getString(price_index);

        if (productImage != null) {
            imageView.setImageBitmap(productImage);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }
        nameTextView.setText(productName);
        priceTextView.setText(productPrice);
        quantityTextView.setText(productQuantity);
        fabSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
                    Object obj = v.getTag();
                    String st = obj.toString();
                    ContentValues values = new ContentValues();
                    int quantity = Integer.parseInt(productQuantity);

                    values.put(ShopEntry.SHOP_COLUMN_NAME, productName);
                    values.put(ShopEntry.SHOP_COLUMN_PRICE, productPrice);
                    values.put(ShopEntry.SHOP_COLUMN_QUANTITY, quantity >= 1 ? quantity - 1 : 0);
                    Uri currentProductUri = ContentUris.withAppendedId(ShopEntry.CONTENT_URI, Integer.parseInt(st));

                    int rowsAffected = mContext.getContentResolver().update(currentProductUri, values, null, null);
                    if (rowsAffected == 0 || quantity == 0) {
                        Toast.makeText(mContext, "you haven't any quantity for this product", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        Object obj = cursor.getInt(cursor.getColumnIndex(ShopEntry._ID));
        fabSell.setTag(obj);
    }

}
