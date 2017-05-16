package com.iris.android.inventorymaster.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by iris on 2017-03-02.
 * Note:

 1.Before inserting into database,
 you need to convert your Bitmap image into byte array first then apply it using database query.
 2.When retrieving from database,
 you certainly have a byte array of image,
 what you need to do is to convert byte array back to original image.
 So, you have to make use of BitmapFactory to decode.
 */

public class DbBitmapUtility {
    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
