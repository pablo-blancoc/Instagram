package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.common.util.concurrent.ListenableFuture;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PostActivity extends AppCompatActivity {

    // Constants
    private static final String TAG = "PostActivity";
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;

    // Attributes
    EditText etDescription;
    PreviewView pvImage;
    Button btnPost;
    Button btnAddImage;
    ProgressBar loading;
    ImageView ivImage;
    ImageCapture imageCapture;
    File outputDirectory;
    ExecutorService cameraExecutor;
    TextView Description;
    File image;
    int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // Find attributes on View
        this.etDescription = findViewById(R.id.etDescription);
        this.btnAddImage = findViewById(R.id.btnAddImage);
        this.btnPost = findViewById(R.id.btnPost);
        this.pvImage = findViewById(R.id.pvImage);
        this.ivImage = findViewById(R.id.ivImage);
        this.loading = findViewById(R.id.loading);
        this.Description = findViewById(R.id.Description);

        // Get intent if we want to update ProfilePicture
        Intent intent = getIntent();
        this.code = intent.getIntExtra("code", 0);
        if( this.code != 0 ) {
            this.etDescription.setVisibility(View.GONE);
            this.Description.setText("New profile picture");
        }

        // Listener to submit the post
        this.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnPost.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);

                if( code == 0 ) {
                    String description = etDescription.getText().toString();
                    if(description.isEmpty()) {
                        Toast.makeText(PostActivity.this, "Description can't be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(image == null || ivImage.getDrawable() == null) {
                        Toast.makeText(PostActivity.this, "You need to take an image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ParseUser user = ParseUser.getCurrentUser();
                    post(user, description, image);
                } else {
                    if(image == null || ivImage.getDrawable() == null) {
                        Toast.makeText(PostActivity.this, "You need to take an image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ParseUser user = ParseUser.getCurrentUser();
                    user.put("picture", new ParseFile(image));
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            btnPost.setVisibility(View.VISIBLE);
                            loading.setVisibility(View.GONE);

                            if( e != null ) {
                                Log.e(TAG, "Error while saving", e);
                                Toast.makeText(PostActivity.this, "Could not save picture", Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(PostActivity.this, "Nice picture!", Toast.LENGTH_SHORT).show();
                                etDescription.setText("");
                                ivImage.setImageResource(0);
                                ivImage.setVisibility(View.GONE);
                                btnAddImage.setVisibility(View.VISIBLE);
                                pvImage.setVisibility(View.VISIBLE);
                                finish();
                            }
                        }
                    });
                }

            }
        });

        // Listener to take picture and submit
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        // setting up the output directory and creating new thread for taking pictures
        outputDirectory = getOutputDirectory();
        cameraExecutor = Executors.newSingleThreadExecutor();

        // Start camera
        if(hasCameraPermission()) {
            enableCamera();
        } else {
            requestCameraPermission();
        }
    }

    /**
     * Returns whether the user has granted access to use the camera
     * @return boolean
     */
    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests the user the permission to use the camera
     */
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, CAMERA_PERMISSION, CAMERA_REQUEST_CODE);
    }

    /**
     * Takes a picture using CameraX
     */
    private void takePhoto() {
        if(imageCapture == null){
            return;
        }

        // set up the photo file for storing the photo
        File photoFile = new File(outputDirectory, new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg");

        // save the image and wait for callback.
        ImageCapture.OutputFileOptions fileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(fileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Uri savedUri = Uri.fromFile(photoFile);
                String msg = "Photo Capture succeeded " + savedUri.toString();
                Log.d(TAG, msg);

                // View image on page and take our button to take picture
                btnAddImage.setVisibility(View.GONE);
                pvImage.setVisibility(View.GONE);
                ivImage.setVisibility(View.VISIBLE);

                // Save image into File variable
                image = new File(savedUri.getPath());

                // Load image with Glide
                Glide.with(PostActivity.this)
                        .load(new File(savedUri.getPath()))
                        .into(ivImage);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.i("Image Capture", exception.toString());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                enableCamera();
            } else {
                Toast.makeText(this, "Permissions not grated by the user", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /**
     * Enables the camera in order to take a picture
     */
    private void enableCamera() {
        ListenableFuture<ProcessCameraProvider> processCameraProvider = ProcessCameraProvider.getInstance(this);

        // the main logic is in a listener.
        processCameraProvider.addListener(new Runnable() {
            @Override
            public void run() {
                ProcessCameraProvider cameraProvider = null;
                try {
                    cameraProvider = processCameraProvider.get();
                } catch (Exception e) {
                    Log.e(TAG, "Exception on enableCamera", e);
                }

                // set up preview window
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(pvImage.createSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                // tie the preview, camera selector and imageCapture together via the cameraProvider
                try{
                    cameraProvider.unbindAll();
                    cameraProvider.bindToLifecycle(PostActivity.this, cameraSelector, preview, imageCapture);
                }catch (Exception e){
                    Log.e(TAG, "Exception on enableCamera", e);
                }
            }


        }, ContextCompat.getMainExecutor(this));
    }

    /**
     * Gets the output directory of the picture if it is to be taken
     * @return File
     */
    private File getOutputDirectory() {

        File mediaDirs = getExternalMediaDirs()[0];
        File newFiles = null;

        if (mediaDirs != null) {
            newFiles = new File(mediaDirs, getResources().getString(R.string.app_name));
            newFiles.mkdirs();
        }
        if (newFiles != null && mediaDirs.exists()) {
            return mediaDirs;
        } else
            return getFilesDir();
    }

    /**
     * Creates a new Post and posts it to Parse on background thread
     * @param user: ParseUser that makes the post
     * @param description: The description that must be included in the post
     */
    private void post(ParseUser user, String description, File image) {
        Post post = new Post();
        post.setDescription(description);
        post.setUser(user);
        post.setImage(new ParseFile(image));
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                btnPost.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);

                if( e != null ) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(PostActivity.this, "Could not save the post", Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(PostActivity.this, "Nice post!", Toast.LENGTH_SHORT).show();
                    etDescription.setText("");
                    ivImage.setImageResource(0);
                    ivImage.setVisibility(View.GONE);
                    btnAddImage.setVisibility(View.VISIBLE);
                    pvImage.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(PostActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}