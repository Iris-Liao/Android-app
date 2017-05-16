package com.iris.android.inventorymaster;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iris.android.inventorymaster.data.ProductContract;
import com.iris.android.inventorymaster.data.ProductFeedHelper;

/**
 * Created by iris on 2017-03-02.
 */

public class ProductCursorAdapter extends CursorAdapter {
    public static final String LOG_TAG = ProductCursorAdapter.class.getName();

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    Context mContext;
    ProductFeedHelper mProductFeedHelper;


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        mProductFeedHelper = new ProductFeedHelper(context);
        ViewHolder mViewHolder;
        View convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        mViewHolder = new ViewHolder(convertView);
        convertView.setTag(mViewHolder);
        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final Uri mUri;
        final int currentQuantity;
        ViewHolder viewHolder;
        viewHolder = (ViewHolder) view.getTag();
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry._ID));
        mUri = ContentUris.withAppendedId(ProductContract.ProductEntry.PRODUCT_CONTENT_URI, id);
        //Extract properties from cursor
        //1.name
        String productName = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME));
        //2.price
        Float price = cursor.getFloat(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE));
        String productPrice = "Product Price: " + price;
        //3.quantity
        currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY));
        String productQuantity = "Current Quantity: " + currentQuantity;
        viewHolder.nameView.setText(productName);
        viewHolder.priceView.setText(productPrice);
        viewHolder.quantityView.setText(productQuantity);
        //set onclickListener to increase and reduce by 1 button
        viewHolder.mProductReduceButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                int mQuantity;
                mQuantity = currentQuantity;
                mContext = v.getContext();
                if (mQuantity >= 1) {
                    mQuantity -= 1;
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, mQuantity);
                    changeCurrentQuantity(values, mUri);
                }
            }
        });
    }

    public void changeCurrentQuantity(ContentValues values, Uri mUri) {
        mContext.getContentResolver().update(mUri, values, null, null);
    }

    //ViewHolder for better performance
    class ViewHolder {
        private TextView nameView;
        private TextView priceView;
        private TextView quantityView;
        private ImageView mProductReduceButton;

        public ViewHolder(View view) {
            this.nameView = (TextView) view.findViewById(R.id.name);
            this.priceView = (TextView) view.findViewById(R.id.price);
            this.quantityView = (TextView) view.findViewById(R.id.quantity);
            this.mProductReduceButton = (ImageView) view.findViewById(R.id.reduce_button);
        }

    }
}
