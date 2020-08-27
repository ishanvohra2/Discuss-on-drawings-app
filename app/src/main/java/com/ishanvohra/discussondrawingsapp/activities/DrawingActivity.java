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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    private ImageView imageView, markerImg, labelImg;
    private RecyclerView recyclerView;

    private static final int PICK_IMAGE = 100;
    private String drawingId;

    private MarkerListAdapter markerListAdapter = new MarkerListAdapter(this, new ArrayList<Marker>(), this);

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        drawingId = getIntent().getStringExtra("drawingId");

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

                Dialog dialog = onCreateDialog(motionEvent.getX(), motionEvent.getY());
                dialog.show();
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;
                dialog.getWindow().setLayout(width, height);
                dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                return false;
            }
        });

        recyclerView = findViewById(R.id.markers_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        markerListAdapter = new MarkerListAdapter(this, new ArrayList<Marker>(), this);
        recyclerView.setAdapter(markerListAdapter);

        databaseReference.child("markers").addValueEventListener(new ValueEventListener() {
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

    }

    private Dialog onCreateDialog(final float x, final float y){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_marker_dialog_layout);

        final EditText contentEt = dialog.findViewById(R.id.add_marker_dialog_content_et);
        labelImg = dialog.findViewById(R.id.add_marker_dialog_iv);
        Button addImgBtn = dialog.findViewById(R.id.add_marker_dialog_upload_img_btn);
        Button cancelBtn = dialog.findViewById(R.id.add_marker_dialog_cancel_btn);
        Button submitBtn = dialog.findViewById(R.id.add_marker_dialog_add_btn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Marker marker = new Marker();
                marker.setMarkerId(databaseReference.push().getKey());
                marker.setContentEt(contentEt.getText().toString());
                marker.setDrawingId(drawingId);
                marker.setType("Label");
                marker.setX(x);
                marker.setY(y);

                if(labelImg.getDrawable() != null){
                    uploadImage(marker.getMarkerId());
                }

                databaseReference.child("markers").child(marker.getMarkerId()).setValue(marker)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(DrawingActivity.this, "Label added", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        return dialog;
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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();

            String path = "markers/" + markerId + "/image.jpeg";
            StorageReference storageReference = storage.getReference(path);

            UploadTask uploadTask = storageReference.putBytes(bitmapdata);

            return path;
        }

        return "";
    }

    @Override
    public void setMarker(float x, float y) {
        markerImg.setVisibility(View.VISIBLE);
        markerImg.setPadding((int) x,(int) y, 0,0);
    }
}