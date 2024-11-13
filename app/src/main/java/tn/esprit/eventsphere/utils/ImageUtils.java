package tn.esprit.eventsphere.utils;

import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;

public class ImageUtils {

    public static byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);  // Compression en PNG
        return byteArrayOutputStream.toByteArray();
    }
}
