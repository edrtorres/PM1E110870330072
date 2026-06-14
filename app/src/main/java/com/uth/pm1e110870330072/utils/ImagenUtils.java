package com.uth.pm1e110870330072.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImagenUtils {

    public static String bitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) {
            return "";
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);

        byte[] imagenBytes = outputStream.toByteArray();

        return Base64.encodeToString(imagenBytes, Base64.DEFAULT);
    }

    public static Bitmap base64ToBitmap(String imagenBase64) {
        if (imagenBase64 == null || imagenBase64.trim().isEmpty()) {
            return null;
        }

        try {
            byte[] imagenBytes = Base64.decode(imagenBase64, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
        } catch (Exception e) {
            return null;
        }
    }
}