package com.pawlak.krzysiek.hotnail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.kosalgeek.android.photoutil.PhotoLoader;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.pawlak.krzysiek.hotnail.API_URL.SERVER;

public class PhotoActivity extends AppCompatActivity {

    final int GALLERY_REQUEST = 1200;
    final int CAMERA_REQUEST = 13323;
    final int CAMERA_REQUEST_OLD = 2146;
    ImageView ivGallery, ivUpload, ivImage, ivCamera, ivCameraOld;
    EditText etImage;
    GalleryPhoto galleryPhoto;
    CameraPhoto cameraPhoto;
    byte[] BYTE; // for compress
    ByteArrayOutputStream bytearrayoutputstream; // for compress
    ArrayList<String> imageList = new ArrayList<>();

    private static final String URL = SERVER + "/upload.php";

    String selectedPhoto;

    public static int byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        final CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorLayout);

        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String email = getPrefs.getString("email", "empty");

        galleryPhoto = new GalleryPhoto(getApplicationContext());
        cameraPhoto = new CameraPhoto(getApplicationContext());

        ivGallery = (ImageView) findViewById(R.id.ivGallery);
        ivUpload = (ImageView) findViewById(R.id.ivUpload);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        ivCameraOld = (ImageView) findViewById(R.id.ivCameraOld);
        etImage = (EditText) findViewById(R.id.etImage);

        bytearrayoutputstream = new ByteArrayOutputStream();

        ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = galleryPhoto.openGalleryIntent();
                startActivityForResult(in, GALLERY_REQUEST);
            }
        });

        ivCameraOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_OLD);
            }
        });

        final MyCommand myCommand = new MyCommand(getApplicationContext());

        ivUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check images selected, return
                if (selectedPhoto == null || selectedPhoto.equals("")) {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "No image selected", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }
                if (etImage.getText().toString().trim().length() == 0) {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "fill title", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }

                try {
                    Bitmap bitmap = PhotoLoader.init().from(selectedPhoto).requestSize(220, 220).getBitmap();

                    // compress
                    bitmap.compress(Bitmap.CompressFormat.JPEG,40,bytearrayoutputstream);
                    BYTE = bytearrayoutputstream.toByteArray();
                    Bitmap bitmap2 = BitmapFactory.decodeByteArray(BYTE, 0, BYTE.length);
                    bitmap = bitmap2;

                    final String encodedString = ImageBase64.encode(bitmap);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            finish();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "error", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("image", encodedString);
                            params.put("image_name", etImage.getText().toString());
                            params.put("email", email);
                            return params;
                        }
                    };
                    myCommand.add(stringRequest);

                } catch (FileNotFoundException e) {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Error while loading image", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                myCommand.execute();
            }
        });

        getSupportActionBar().setTitle("Back to Menu");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        if (resultCode == RESULT_OK) {

            // working on real device(my device)
            if (requestCode == CAMERA_REQUEST_OLD) {
                Uri selectedImage = data.getData();
                String photoPath = getRealPathFromURI(getApplicationContext(), selectedImage);
                selectedPhoto = photoPath;
                try {
                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(220, 220).getBitmap();
                    ivImage.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Something Wrong while loading photos", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

            } // end old version camera

            if (requestCode == GALLERY_REQUEST) { // read from gallery
                Uri uri = data.getData();
                galleryPhoto.setPhotoUri(uri);
                String photoPath = galleryPhoto.getPath();
                selectedPhoto = photoPath;
                try {
                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(220, 220).getBitmap();
                    ivImage.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {

                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Something Wrong while choosing photos", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            } // end read
        } // end checking result code
    } // end for result

    // get path from Uri
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // rotate images
    private Bitmap getRotatedBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap bitmap1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        return bitmap1;
    }
}
