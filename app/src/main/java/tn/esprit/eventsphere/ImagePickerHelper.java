package tn.esprit.eventsphere;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImagePickerHelper {
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int MAX_IMAGE_SIZE = 800;
    private final Activity activity;
    private final ImageView imageView;
    private final DatabaseHelper databaseHelper;
    private final int userId;

    public ImagePickerHelper(Activity activity, ImageView imageView, DatabaseHelper databaseHelper, int userId) {
        this.activity = activity;
        this.imageView = imageView;
        this.databaseHelper = databaseHelper;
        this.userId = userId;
    }

    public void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                try {
                    Uri imageUri = data.getData();
                    if (imageUri != null) {
                        // Get input stream from Uri
                        InputStream inputStream = activity.getContentResolver().openInputStream(imageUri);

                        // Decode image size first
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(inputStream, null, options);
                        inputStream.close();

                        // Calculate scale
                        int scale = 1;
                        while (options.outWidth / scale > MAX_IMAGE_SIZE ||
                                options.outHeight / scale > MAX_IMAGE_SIZE) {
                            scale *= 2;
                        }

                        // Decode with scale
                        options = new BitmapFactory.Options();
                        options.inSampleSize = scale;
                        inputStream = activity.getContentResolver().openInputStream(imageUri);
                        Bitmap selectedImage = BitmapFactory.decodeStream(inputStream, null, options);
                        inputStream.close();

                        if (selectedImage != null) {
                            // Convert bitmap to byte array
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            selectedImage.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                            byte[] imageData = stream.toByteArray();

                            // Save to database
                            boolean saved = databaseHelper.saveProfileImage(userId, imageData);
                            if (saved) {
                                imageView.setImageBitmap(selectedImage);
                                Toast.makeText(activity, "Profile picture updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, "Failed to save image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(activity, "Error processing image", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return false;
    }
}