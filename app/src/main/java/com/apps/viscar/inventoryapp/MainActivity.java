package com.apps.viscar.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.apps.viscar.inventoryapp.data.ShopContract.ShopEntry;
import com.apps.viscar.inventoryapp.data.ShopDbHelper;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int SHOP_LOADER = 0;
    private static final String[] PROJECTION = new String[]{ShopEntry._ID, ShopEntry.SHOP_COLUMN_NAME, ShopEntry.SHOP_COLUMN_PRICE,
            ShopEntry.SHOP_COLUMN_QUANTITY, ShopEntry.SHOP_COLUMN_IMAGE};

    private ShopCursorAdapter mAdapter;
    private ShopDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });
        mDbHelper = new ShopDbHelper(this);
        ListView productList = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        productList.setEmptyView(emptyView);
        mAdapter = new ShopCursorAdapter(this, null);
        productList.setAdapter(mAdapter);
        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                Uri uri = ContentUris.withAppendedId(ShopEntry.CONTENT_URI, id);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(SHOP_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, ShopEntry.CONTENT_URI, PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertDummyData();
                //  displayDatabaseInfo();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertDummyData() {
        ContentValues values = new ContentValues();
        values.put(ShopEntry.SHOP_COLUMN_NAME, "Pixel 2");
        values.put(ShopEntry.SHOP_COLUMN_QUANTITY, "4");
        values.put(ShopEntry.SHOP_COLUMN_PRICE, "50.00");
        values.put(ShopEntry.SHOP_COLUMN_IMAGE, toBlob());
        getContentResolver().insert(ShopEntry.CONTENT_URI, values);
    }

    private void deleteAllProducts() {
        getContentResolver().delete(ShopEntry.CONTENT_URI, null, null);
    }

    private byte[] toBlob() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Drawable vectorDrawable = ResourcesCompat.getDrawable(this.getResources(), R.drawable.pixeleight, null);
        Bitmap bitmap = ((BitmapDrawable) vectorDrawable).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        byte[] photo = stream.toByteArray();
        return photo;
    }

    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        // Create and/or open a database to read from it
        Cursor cursor = getContentResolver().query(ShopEntry.CONTENT_URI, PROJECTION, null, null, null);
        ListView productListView = (ListView) findViewById(R.id.list);
        View emtyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emtyView);
        ShopCursorAdapter adapter = new ShopCursorAdapter(this, cursor);
        productListView.setAdapter(adapter);

    }

}
