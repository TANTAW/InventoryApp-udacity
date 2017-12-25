package com.apps.viscar.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.viscar.inventoryapp.data.ShopContract.ShopEntry;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] PROJECTION = new String[]{ShopEntry._ID, ShopEntry.SHOP_COLUMN_NAME, ShopEntry.SHOP_COLUMN_PRICE,
            ShopEntry.SHOP_COLUMN_QUANTITY, ShopEntry.SHOP_COLUMN_IMAGE};
    private static final int EXISTING_PET_LOADER = 0;
    public static TextView productQuantity;
    private static int RESULT_LOAD_IMAGE = 7;
    private EditText productName;
    private EditText productPrice;
    private ImageView productImage;
    private Uri currentProductUri;
    private byte[] mImageByteArray;
    private boolean mViewHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mViewHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        final Intent intent = getIntent();
        currentProductUri = intent.getData();

        getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);

        productName = (EditText) findViewById(R.id.edit_text_name);
        productPrice = (EditText) findViewById(R.id.edit_text_price);
        productQuantity = (TextView) findViewById(R.id.text_view_quantity);
        productImage = (ImageView) findViewById(R.id.image_view);
        productName.setOnTouchListener(mTouchListener);
        productPrice.setOnTouchListener(mTouchListener);
        productQuantity.setOnTouchListener(mTouchListener);
        productImage.setOnTouchListener(mTouchListener);

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                Intent chooserIntent = Intent.createChooser(intent, "Select an Image");
                startActivityForResult(chooserIntent, RESULT_LOAD_IMAGE);
            }
        });
        Button modifyQuantity = (Button) findViewById(R.id.btn_quantity);
        modifyQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditorActivity.this, AddQuantityActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (currentProductUri == null) {
            MenuItem deleteMenuItem = menu.findItem(R.id.action_delete);
            MenuItem orderMenuItem = menu.findItem(R.id.action_order);
            deleteMenuItem.setVisible(false);
            orderMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mViewHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveProduct();
                Intent intent = new Intent(EditorActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(EditorActivity.this, getString(R.string.details_update_product_successful), Toast.LENGTH_SHORT).show();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                // Do nothing for now
                return true;
            case R.id.action_order:
                orderMessage();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mViewHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                currentProductUri,
                PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ShopEntry.SHOP_COLUMN_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ShopEntry.SHOP_COLUMN_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ShopEntry.SHOP_COLUMN_PRICE);

            String name = cursor.getString(nameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);

            mImageByteArray = cursor.getBlob(cursor.getColumnIndex(ShopEntry.SHOP_COLUMN_IMAGE));
            Bitmap productImageBitmap = ImageHelper.convertBlobToBitmap(mImageByteArray);
            productImage.setImageBitmap(productImageBitmap);
            productName.setText(name);
            productPrice.setText(String.valueOf(price));
            productQuantity.setText(String.valueOf(quantity));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productName.setText("");
        productPrice.setText("");
        productQuantity.setText("");


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            if (data == null) {
                return;
            }
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                productImage.setImageBitmap(selectedImage);
                mImageByteArray = ImageHelper.convertBitmapToBlob(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(EditorActivity.this, getString(R.string.file_not_found), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == RESULT_LOAD_IMAGE) {
            Toast.makeText(EditorActivity.this, getString(R.string.image_failed), Toast.LENGTH_LONG).show();
        }
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveProduct() {
        String name = productName.getText().toString().trim();
        String quantity = productQuantity.getText().toString().trim();
        String price = productPrice.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(ShopEntry.SHOP_COLUMN_NAME, name);
        values.put(ShopEntry.SHOP_COLUMN_PRICE, price);
        values.put(ShopEntry.SHOP_COLUMN_QUANTITY, quantity);

        if (mImageByteArray != null) {
            values.put(ShopEntry.SHOP_COLUMN_IMAGE, mImageByteArray);
        }

        int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);
        if (rowsAffected == 0) {
            Toast.makeText(this, getString(R.string.details_update_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.details_update_product_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProduct() {
        if (currentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(currentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.details_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.details_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this product ?");
        builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
                finish();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void orderMessage() {
        try {
            Intent mEmail = new Intent(Intent.ACTION_SEND);
            mEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"ahmedadelhabib@outlook.com"});
            mEmail.putExtra(Intent.EXTRA_SUBJECT, "Order");
            mEmail.putExtra(Intent.EXTRA_TEXT, "I want this product   " + productName.getText().toString().trim() + "   With quantity   "
                    + productQuantity.getText().toString().trim());
            mEmail.setType("message/rfc822");
            startActivity(mEmail);
        } catch (Exception e) {
            Toast.makeText(this, "You don't have email app", Toast.LENGTH_LONG).show();
        }
    }
}
