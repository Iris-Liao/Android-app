package com.iris.android.inventorymaster;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.iris.android.inventorymaster.data.ProductContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int PRODUCT_LOADER = 0;
    private ProductCursorAdapter productCursorAdapter;
    private static final String[] projection = {
            ProductContract.ProductEntry._ID,
            ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
            ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
            ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView mListView = (ListView) findViewById(R.id.text_view_product);
        productCursorAdapter = new ProductCursorAdapter(this, null);
        //set adapter and empty view
        mListView.setAdapter(productCursorAdapter);
        View mEmptyView = findViewById(R.id.empty_view);
        mListView.setEmptyView(mEmptyView);
        //init loader
        getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        //set fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_new_product_button);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        //set onItemClick
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri startUri = ContentUris.withAppendedId(ProductContract.ProductEntry.PRODUCT_CONTENT_URI, id);
                intent.setData(startUri);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all_entries:
                showDeleteWarningMessage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case PRODUCT_LOADER:
                return new CursorLoader(
                        this,
                        ProductContract.ProductEntry.PRODUCT_CONTENT_URI,
                        projection,
                        null,
                        null,
                        null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        productCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productCursorAdapter.changeCursor(null);
    }

    public void showDeleteWarningMessage() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setMessage(getString(R.string.delete_all_products_msg));
        mBuilder.setPositiveButton(R.string.delete_all_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        mBuilder.setNegativeButton(R.string.delete_all_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAllProducts();
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = mBuilder.create();
        alertDialog.show();
    }

    public void deleteAllProducts() {
        int nRows = 0;
        nRows = getContentResolver().delete(ProductContract.ProductEntry.PRODUCT_CONTENT_URI, null, null);
        if (nRows != 0) {
            Toast.makeText(this, getString(R.string.delete_all_successful)+nRows, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.delete_all_failed), Toast.LENGTH_SHORT).show();
        }
    }

}
