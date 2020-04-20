package com.collegehackz.addemoji;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE=1;
    private static final int REQUEST_STORAGE_PERMISSION=1;
    private static final String FILE_PROVIDER_AUTHORITY = "com.collegehackz.addemoji.provider";
    private ImageView imageView;
    private Button mainAppButton,mainAddEmojiButton;
    private FloatingActionButton shareFab, saveFab, clearFab;
    private TextView titleTextView;
    private String mTempPhotoPath;
    private Bitmap mResultsBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);
        mainAppButton = findViewById(R.id.main_go_butt);
        shareFab = findViewById(R.id.share_butt);
        saveFab = findViewById(R.id.save_butt);
        clearFab = findViewById(R.id.clear_butt);
        titleTextView = findViewById(R.id.title_textView);
        mainAddEmojiButton = findViewById(R.id.main_addemoji_butt);

        mainAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojifyMe();
            }
        });
        
        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareMe();
            }
        });
        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMe();
            }
        });
        clearFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMe();
            }
        });

        mainAddEmojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmoji();
            }
        });
    }

    private void addEmoji() {
        mResultsBitmap = Detector.detectFaces(getApplicationContext(),mResultsBitmap);
        imageView.setImageBitmap(mResultsBitmap);
    }

    private void clearMe() {
        mainAppButton.setVisibility(View.VISIBLE);
        titleTextView.setVisibility(View.VISIBLE);
        saveFab.setVisibility(View.GONE);
        shareFab.setVisibility(View.GONE);
        clearFab.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        mainAddEmojiButton.setVisibility(View.GONE);
        BitMapUtils.deleteImageFile(this,mTempPhotoPath);
    }

    private void saveMe() {
        BitMapUtils.deleteImageFile(this,mTempPhotoPath);
        BitMapUtils.saveImage(this,mResultsBitmap);
    }

    private void shareMe() {
        BitMapUtils.deleteImageFile(this,mTempPhotoPath);
        BitMapUtils.saveImage(this,mResultsBitmap);
        BitMapUtils.shareImage(this,mTempPhotoPath);
    }

    private void emojifyMe() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_STORAGE_PERMISSION);
        }else{
            launchCamera();
        }
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePictureIntent.resolveActivity(getPackageManager())!=null){
            File photoFile =null;
            try {
                photoFile = BitMapUtils.createTempImageFile(this);
            }catch (IOException ex){
                ex.printStackTrace();
            }
            if(photoFile!=null){
                mTempPhotoPath = photoFile.getAbsolutePath();

                Uri photoUri = FileProvider.getUriForFile(this,FILE_PROVIDER_AUTHORITY,photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);

                startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            processAndSetImage();
        } else {
            BitMapUtils.deleteImageFile(this, mTempPhotoPath);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void processAndSetImage() {
        mainAppButton.setVisibility(View.GONE);
        titleTextView.setVisibility(View.GONE);
        saveFab.setVisibility(View.VISIBLE);
        shareFab.setVisibility(View.VISIBLE);
        clearFab.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        mainAddEmojiButton.setVisibility(View.VISIBLE);
        mResultsBitmap = BitMapUtils.resamplePic(this,mTempPhotoPath);
        mResultsBitmap=getResizedBitmap(mResultsBitmap,512,512);
        imageView.setImageBitmap(mResultsBitmap);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
