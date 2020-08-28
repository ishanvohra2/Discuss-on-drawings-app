package com.ishanvohra.discussondrawingsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ishanvohra.discussondrawingsapp.R;
import com.ishanvohra.discussondrawingsapp.adapters.MarkerListAdapter;
import com.ishanvohra.discussondrawingsapp.data.Marker;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static java.security.AccessController.getContext;

public class DrawingActivity extends AppCompatActivity implements MarkerListAdapter.MarkerListAdapterListener {

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private ImageView imageView, markerImg;
    private RecyclerView recyclerView;

    private String drawingId;

    private MarkerListAdapter markerListAdapter = new MarkerListAdapter(this, new ArrayList<Marker>(), this);

    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        drawingId = getIntent().getStringExtra("drawingId");
        String title = getIntent().getStringExtra("title");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imageView = findViewById(R.id.drawing_activity_iv);
        markerImg = findViewById(R.id.drawing_activity_marker_iv);

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return scaleGestureDetector.onTouchEvent(motionEvent);
            }
        });

        //Load image from Firebase cloud storage using glide
        storage.getReference().child("drawings/" + drawingId + "/drawing.jpeg").getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(DrawingActivity.this).load(uri).into(imageView);
            }
        });

        recyclerView = findViewById(R.id.markers_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        markerListAdapter = new MarkerListAdapter(this, new ArrayList<Marker>(), this);
        recyclerView.setAdapter(markerListAdapter);

        //Loading the markers in the recycler view by querying our realtime database
        Query query = databaseReference.child("markers");
        query.orderByChild("drawingId").equalTo(drawingId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList<Marker> markers = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Marker marker = snapshot.getValue(Marker.class);
                        markers.add(marker);
                    }
                    markerListAdapter.setMarkers(markers);
                    markerListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Add New Marker
        FloatingActionButton addMarkerBtn = findViewById(R.id.drawing_activity_fab);
        addMarkerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrawingActivity.this, AddMarkerActivity.class);
                intent.putExtra("drawingId", drawingId);
                startActivity(intent);
            }
        });
    }

    //Change the position of the marker
    @Override
    public void setMarker(float x, float y) {
        markerImg.setVisibility(View.VISIBLE);
        markerImg.setPadding((int) x,(int) y, 0,0);
    }

    //Pinch to zoom functionality
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
        scaleFactor *= scaleGestureDetector.getScaleFactor();
        scaleFactor = Math.max(0.1f,Math.min(scaleFactor, 10.0f));
        imageView.setScaleX(scaleFactor);
        imageView.setScaleY(scaleFactor);
        return true;
        }
    }
}