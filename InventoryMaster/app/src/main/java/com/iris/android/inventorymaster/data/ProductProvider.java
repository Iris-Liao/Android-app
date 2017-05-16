package com.iris.android.inventorymaster.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.iris.android.inventorymaster.EditorActivity;

/**
 * Created by iris on 2017-03-02.
 */

public class ProductProvider extends ContentProvider {
    private static final String LOG_TAG = ProductProvider.class.getName();
    private ProductFeedHelper productHelper;
    private static final int PRODUCT = 100;
    private static final int PRODUCT_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCT, PRODUCT);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCT + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        productHelper = new ProductFeedHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = productHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                cursor = db.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // Cursor containing that row of the table.
                cursor = db.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return ProductContract.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri mUri, ContentValues contentValues) {
        SanityCheck(contentValues);
        SQLiteDatabase db = productHelper.getWritableDatabase();
        long id = db.insert(ProductContract.ProductEntry.TABLE_NAME, null, contentValues);
        if (id > 0) {
            Uri _uri = ContentUris.withAppendedId(mUri, id);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        } else {
            Log.e(LOG_TAG, "Failed to insert row for " + mUri);
            return null;
        }
    }

    private void SanityCheck(ContentValues contentValues) {
        String productName = contentValues.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME).trim();
        if (TextUtils.isEmpty(productName)) {
            throw new IllegalArgumentException("Product requires a name");
        }
        String productSupplier = contentValues.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER).trim();
        if (TextUtils.isEmpty(productSupplier)) {
            throw new IllegalArgumentException("Product requires the supplier");
        }
        //be default;0
        Float productPrice = contentValues.getAsFloat(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        if ( productPrice <=0) {
            throw new IllegalArgumentException("Product requires valid price");
        }
        //by default:0
        Integer productQuantity = contentValues.getAsInteger(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (productQuantity <=0) {
            throw new IllegalArgumentException("Product requires valid quantity");
        }
        //by default: add_photo_image
        byte[] productImage = contentValues.getAsByteArray(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE);
        Log.i(LOG_TAG,"test:"+EditorActivity.isPhotoChosen);
        if (!EditorActivity.isPhotoChosen) {
            Log.i(LOG_TAG,"test:"+EditorActivity.isPhotoChosen);
            throw new IllegalArgumentException("Product requires valid image");
        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = productHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int nRows;
        switch (match) {
            case PRODUCT:
                if(selection==null){selection="1";}
                nRows = db.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                if (nRows != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return nRows;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                nRows = db.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                if (nRows != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return nRows;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri mUri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)) {
            String productName = contentValues.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            if (TextUtils.isEmpty(productName)) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }
        if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER)) {
            String productSupplier = contentValues.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            if (TextUtils.isEmpty(productSupplier) ) {
                throw new IllegalArgumentException("Product requires the supplier");
            }
        }
        if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE)) {
            Float productPrice = contentValues.getAsFloat(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            if (productPrice <= 0) {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }
        if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer productQuantity = contentValues.getAsInteger(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (productQuantity <= 0) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }
        if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE)) {
            byte[] productImage = contentValues.getAsByteArray(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE);
            if (productImage == null||EditorActivity.isPhotoChosen==false) {
                throw new IllegalArgumentException("Product requires valid image");
            }
        }
        SQLiteDatabase database = productHelper.getWritableDatabase();
        int nRows = database.update(ProductContract.ProductEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (nRows != 0) {
            getContext().getContentResolver().notifyChange(mUri, null);
        }
        return nRows;
    }
}
