package com.ishanvohra.discussondrawingsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ishanvohra.discussondrawingsapp.R;
import com.ishanvohra.discussondrawingsapp.data.Marker;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class AddMarkerActivity extends AppCompatActivity {

    private EditText titleEt;
    private ImageView imageView, labelImg, markerImg;
    private Button addImgBtn, postBtn;

    private static final int PICK_IMAGE = 100;
    private String drawingId;
    private float xVal, yVal;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);

        drawingId = getIntent().getStringExtra("drawingId");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        titleEt = findViewById(R.id.add_marker_content_et);
        imageView = findViewById(R.id.add_marker_drawing_iv);
        labelImg = findViewById(R.id.add_marker_iv);
        markerImg = findViewById(R.id.add_marker_marker_iv);
        addImgBtn = findViewById(R.id.add_marker_upload_img_btn);
        postBtn = findViewById(R.id.add_marker_add_btn);

        storage.getReference().child("drawings/" + drawingId + "/drawing.jpeg").getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(AddMarkerActivity.this).load(uri).into(imageView);
                    }
                });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                markerImg.setVisibility(View.VISIBLE);
                markerImg.setPadding((int) motionEvent.getX(),(int) motionEvent.getY(), 0,0);
                xVal = motionEvent.getX();
                yVal = motionEvent.getY();
                return false;
            }
        });

        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(titleEt.getText().toString())){
                    titleEt.setError("Please enter some text");
                    return;
                }

                Marker marker = new Marker();
                marker.setMarkerId(databaseReference.push().getKey());
                marker.setContentEt(titleEt.getText().toString());
                marker.setDrawingId(drawingId);
                marker.setType("Label");
                marker.setX(xVal);
                marker.setY(yVal);
                marker.setAdditionTime(new Date().getTime());

                if(labelImg.getDrawable() != null){
                    uploadImage(marker.getMarkerId());
                }

                databaseReference.child("markers").child(marker.getMarkerId()).setValue(marker)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(AddMarkerActivity.this, "Label added", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                labelImg.setImageURI(data.getData());
            }
        }
    }

    public void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }


    public String uploadImage(String markerId) {

        if (labelImg.getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) labelImg.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            byte[] bitmapdata = stream.toByteArray();

            String path = "markers/" + markerId + "/image.jpeg";
            StorageReference storageReference = storage.getReference(path);

            UploadTask uploadTask = storageReference.putBytes(bitmapdata);

            return path;
        }

        return "";
    }
}