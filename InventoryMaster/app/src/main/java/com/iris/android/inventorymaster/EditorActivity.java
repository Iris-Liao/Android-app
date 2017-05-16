package com.iris.android.inventorymaster;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iris.android.inventorymaster.data.DbBitmapUtility;
import com.iris.android.inventorymaster.data.ProductContract;

import static android.view.View.GONE;

/**
 * Created by iris on 2017-03-01.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = EditorActivity.class.getName();
    private static final int CAMERA_REQUEST = 1;
    private static final int PHOTOLIB_REQUEST = 2;
    public static final String[] mProjection = {
            ProductContract.ProductEntry._ID,
            ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
            ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER,
            ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
            ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
            ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE
    };
    private EditText mProductName;
    private EditText mProductSupplier;
    private EditText mProductPrice;
    private EditText mProductQuantity;
    private EditText mChangeQuantity;
    private TextView mDisplayQuantity;
    private ImageView mProductImage;
    private ImageView mProductReduceButton;
    private ImageView mProductIncreaseButton;
    private Button mAddMoreButton;
    private boolean mProductHasChanged = false;
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProductHasChanged = true;
            return false;
        }
    };
    Uri mUri = null;
    Intent mIntent;
    public static final int EDITOR_LOADER = 0;
    int currentQuantity;
    int changeQuantity;
    //boolean to check if any photo chosen
    public static boolean isPhotoChosen = false;

    String productName;
    String productSupplier;
    float productPrice = 0f;
    Integer productQuantity = 0;
    Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);
        //set isphotoChosen=false each time editoActivity running for it is a static var
        isPhotoChosen = false;
        //get references
        mProductName = (EditText) findViewById(R.id.edit_product_name);
        mProductSupplier = (EditText) findViewById(R.id.edit_product_supplier);
        mProductPrice = (EditText) findViewById(R.id.edit_product_price);
        mProductQuantity = (EditText) findViewById(R.id.edit_product_quantity);
        mChangeQuantity = (EditText) findViewById(R.id.edit_change_quantity);
        mChangeQuantity.setText("0");
        mDisplayQuantity = (TextView) findViewById(R.id.edit_display_quantity);
        mProductImage = (ImageView) findViewById(R.id.add_photo_button);
        mProductReduceButton = (ImageView) findViewById(R.id.reduce_button);
        mProductIncreaseButton = (ImageView) findViewById(R.id.increase_button);
        mAddMoreButton = (Button) findViewById(R.id.order_more_button);
        //set onTouchListener
        mProductName.setOnTouchListener(mOnTouchListener);
        mProductSupplier.setOnTouchListener(mOnTouchListener);
        mProductPrice.setOnTouchListener(mOnTouchListener);
        mProductQuantity.setOnTouchListener(mOnTouchListener);
        mProductImage.setOnTouchListener(mOnTouchListener);
        mProductReduceButton.setOnTouchListener(mOnTouchListener);
        mProductIncreaseButton.setOnTouchListener(mOnTouchListener);
        //1.check the intent is started from clicking on item or on fab
        mIntent = getIntent();
        mUri = mIntent.getData();
        if (mUri != null) {
            //if in edit mode, the product must have had an image
            Log.i(LOG_TAG, "test:mUri!=null" + mUri + isPhotoChosen);
            isPhotoChosen = true;
            setTitle(getString(R.string.edit_product));
            getSupportLoaderManager().initLoader(EDITOR_LOADER, null, this);

            //set onClickListener to reduce and increase quantity button
            mProductQuantity.setVisibility(GONE);
            mProductReduceButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    /*ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, currentQuantity - changeQuantity);*/
                    int quantity = Integer.parseInt(mChangeQuantity.getText().toString());
                    changeQuantity = quantity;
                    if (currentQuantity >= changeQuantity) {
                        //changeCurrentQuantity(mUri, values);
                        int cQuantity = currentQuantity - changeQuantity;
                        mDisplayQuantity.setText(Integer.toString(cQuantity));
                        currentQuantity = cQuantity;
                    }
                }
            });
            mProductIncreaseButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int quantity = Integer.parseInt(mChangeQuantity.getText().toString());
                    changeQuantity = quantity;
                    if (currentQuantity >= 0) {
                        //changeCurrentQuantity(mUri, values);
                        int cQuantity = currentQuantity + changeQuantity;
                        mDisplayQuantity.setText(Integer.toString(cQuantity));
                        currentQuantity = cQuantity;
                    }
                }
            });
            mAddMoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String emailAddress = getString(R.string.email_address);
                    String productName = mProductName.getText().toString();
                    String subject = getString(R.string.order_for) + productName;
                    String message = productName + "\n" + getString(R.string.needed_quantity);
                    composeEmail(emailAddress, subject, message);
                }
            });

        } else {
            mProductIncreaseButton.setVisibility(GONE);
            mProductReduceButton.setVisibility(GONE);
            mChangeQuantity.setVisibility(GONE);
            mDisplayQuantity.setVisibility(GONE);
            mAddMoreButton.setVisibility(GONE);
        }


        //add onClickListener to imageView to add photo from either photolibrary or camera
        mProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        //if intent is sent by clicking on unit item
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader
                (this,
                        mUri,
                        mProjection,
                        null,
                        null,
                        null
                );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //move cursor to first,or it will cause error???cuz cursor position=-1 by default

        if (cursor.moveToFirst()) {
            String productName = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME));
            String productSupplier = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER));
            Float productPrice = cursor.getFloat(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE));
            Integer productQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY));
            byte[] productImage = cursor.getBlob(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE));
            Bitmap image = DbBitmapUtility.getImage(productImage);
            mProductName.setText(productName);
            mProductSupplier.setText(productSupplier);
            mProductPrice.setText(productPrice.toString());
            mDisplayQuantity.setText(productQuantity.toString());
            mProductImage.setImageBitmap(image);
            currentQuantity = productQuantity;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductName.setText(null);
        mProductSupplier.setText(null);
        mProductPrice.setText(null);
        mDisplayQuantity.setText(null);
        mProductImage.setImageBitmap(null);
        mChangeQuantity.setText(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                try {
                    saveProduct();
                } catch (IllegalArgumentException e) {
                    Toast.makeText(this, "Please enter required value", Toast.LENGTH_SHORT).show();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteWarningMessage();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                //NavUtils.navigateUpFromSameTask(this);
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
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
                unsavedWarningMessage(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeCurrentQuantity(Uri _uri, ContentValues values) {
        getContentResolver().update(_uri, values, null, null);
    }

    //intent for sending an email
    private void composeEmail(String address, String subject, String productMessage) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, address);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, productMessage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void showDeleteWarningMessage() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setMessage(getString(R.string.delete_product_msg));
        mBuilder.setPositiveButton(R.string.delete_all_positive_button, null);
        mBuilder.setNegativeButton(R.string.editor_delete_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = mBuilder.create();
        alertDialog.show();
    }

    public void unsavedWarningMessage(DialogInterface.OnClickListener discardButtonClickListner) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setMessage(getString(R.string.unsaved_changes_dialog_msg));
        mBuilder.setPositiveButton(R.string.delete_all_positive_button, null);
        mBuilder.setNegativeButton(R.string.editor_discard_negative_button, discardButtonClickListner);
        // Create and show the AlertDialog
        AlertDialog alertDialog = mBuilder.create();
        alertDialog.show();
    }

    //warning for click on back button
    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        //if there is no input in "add a product" mode, when user clicks on "save"button, don't save the product
        if (mUri == null && TextUtils.isEmpty(productName) && TextUtils.isEmpty(productSupplier) && productPrice == 0f && productQuantity == 0 && isPhotoChosen == false) {
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
        unsavedWarningMessage(discardButtonClickListener);
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        int mDeletedRows = 0;
        mDeletedRows = getContentResolver().delete(mUri, null, null);
        if (mDeletedRows == 0) {
            Toast.makeText(this, getString(R.string.delete_all_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.editor_delete_succeed), Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void saveProduct() throws IllegalArgumentException {
        //1.product name
        productName = mProductName.getText().toString().trim();
        //2.product supplier
        productSupplier = mProductSupplier.getText().toString().trim();
        //3.product price
        String price = mProductPrice.getText().toString().trim();
        if (!TextUtils.isEmpty(price)) {
            productPrice = Float.parseFloat(mProductPrice.getText().toString());
        }
        //4.quantity
        String quantity = mProductQuantity.getText().toString();
        if (!TextUtils.isEmpty(quantity)) {
            productQuantity = Integer.parseInt(quantity);
        }
        //5.image byte[]
        bitmap = ((BitmapDrawable) mProductImage.getDrawable()).getBitmap();
        byte[] pImage = DbBitmapUtility.getBytes(bitmap);
        //set contentvalues for update and insert
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER, productSupplier);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE, pImage);
        if (mUri == null) {
            values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
            Uri uri = getContentResolver().insert(ProductContract.ProductEntry.PRODUCT_CONTENT_URI, values);
            long newRowId = ContentUris.parseId(uri);
            // Show a toast message depending on whether or not the insertion was successful
            if (newRowId == -1) {
                // If the row ID is -1, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.error_saving_product), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast with the row ID.
                Toast.makeText(this, getString(R.string.product_saved), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        if (mProductHasChanged == false && mUri != null) {
            finish();
        }
        if (mProductHasChanged == true && mUri != null) {
            values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, currentQuantity);
            int number = getContentResolver().update(mUri, values, null, null);
            if (number == 1) {
                Toast.makeText(this, getString(R.string.product_saved), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, getString(R.string.error_saving_product), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void choosePicture() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Option");
        builder.setPositiveButton(R.string.photo_positive_button, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                callCamera();
            }
        });
        builder.setNegativeButton(R.string.photo_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callPhotoLibrary();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void callCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 9);
        intent.putExtra("aspectY", 16);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 250);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_REQUEST);
        } else {
            Toast.makeText(this, "Not able to take picture from camera", Toast.LENGTH_SHORT);
        }
    }

    private void callPhotoLibrary() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTOLIB_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data == null) {
            Toast.makeText(this, getString(R.string.fail_get_photo), Toast.LENGTH_SHORT);
            return;
        } else {
            if ((requestCode == CAMERA_REQUEST || requestCode == PHOTOLIB_REQUEST) && resultCode == RESULT_OK) {
                Log.i(LOG_TAG, "test:onActivityRESULT");
                isPhotoChosen = true;
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                mProductImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            }

        }
    }
}

