package com.ishanvohra.discussondrawingsapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.ishanvohra.discussondrawingsapp.R;

public class DrawingActivity extends AppCompatActivity {

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private ImageView imageView, markerImg;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        String drawingId = getIntent().getStringExtra("drawingId");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imageView = findViewById(R.id.drawing_activity_iv);
        markerImg = findViewById(R.id.drawing_activity_marker_iv);

        storage.getReference().child("drawings/" + drawingId + "/drawing.jpeg").getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(DrawingActivity.this).load(uri).into(imageView);
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Toast.makeText(DrawingActivity.this, "X = " + motionEvent.getX() + " , Y = "
                        + motionEvent.getY(), Toast.LENGTH_SHORT).show();
                markerImg.setVisibility(View.VISIBLE);
                markerImg.setPadding((int) motionEvent.getX(),(int) motionEvent.getY(), 0,0);

                return false;
            }
        });
    }

    private Dialog onCreateDialog(float x, float y){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_marker_dialog_layout);

        return dialog;
    }
}