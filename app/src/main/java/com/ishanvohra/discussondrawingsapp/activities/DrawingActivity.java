package com.ishanvohra.discussondrawingsapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
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

    private ImageView imageView;

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
                Toast.makeText(DrawingActivity.this, "X = " + motionEvent.getX() + " , Y = " + motionEvent.getY(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
}