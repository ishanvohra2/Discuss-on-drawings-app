package com.ishanvohra.discussondrawingsapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ishanvohra.discussondrawingsapp.R;
import com.ishanvohra.discussondrawingsapp.data.Drawing;

import java.io.ByteArrayOutputStream;

public class AddDrawingActivity extends AppCompatActivity {

    private EditText titleEt;
    private ImageView imageView;
    private Button addImgBtn, postBtn;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private static final int PICK_IMAGE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drawing);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        titleEt = findViewById(R.id.add_drawing_content_et);
        imageView = findViewById(R.id.add_drawing_iv);
        addImgBtn = findViewById(R.id.add_drawing_upload_img_btn);
        postBtn = findViewById(R.id.add_drawing_upload_add_btn);

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
                    titleEt.setError("Please enter title");
                    return;
                }

                if(imageView.getDrawable() == null){
                    Toast.makeText(AddDrawingActivity.this, "Add an image", Toast.LENGTH_SHORT).show();
                    return;
                }

                Drawing drawing = new Drawing();
                drawing.setDrawingId(databaseReference.push().getKey());
                drawing.setTitle(titleEt.getText().toString());
                drawing.setImgPath(uploadImage(drawing.getDrawingId()));

                databaseReference.child("drawings").child(drawing.getDrawingId()).setValue(drawing);
                finish();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                imageView.setImageURI(data.getData());
            }
        }
    }

    public void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }


    public String uploadImage(String drawingId) {

        if (imageView.getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            byte[] bitmapdata = stream.toByteArray();

            String path = "drawings/" + drawingId + "/drawing.jpeg";
            StorageReference storageReference = storage.getReference(path);

            UploadTask uploadTask = storageReference.putBytes(bitmapdata);

            return path;
        }

        return "";
    }

}