package com.apps.viscar.inventoryapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Tantawy on 12/16/2017.
 */

public final class ShopContract {
    public static final String CONTENT_AUTHORITY = "com.apps.viscar.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SHOP = "shop";

    public ShopContract() {
    }

    public static abstract class ShopEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SHOP);

        public static final String TABLE_NAME = "shop";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String SHOP_COLUMN_NAME = "name";
        public static final String SHOP_COLUMN_PRICE = "price";
        public static final String SHOP_COLUMN_QUANTITY = "quantity";
        public static final String SHOP_COLUMN_IMAGE = "image";


    }
}
