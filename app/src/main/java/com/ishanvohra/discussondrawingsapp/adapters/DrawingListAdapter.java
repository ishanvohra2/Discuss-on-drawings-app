package com.ishanvohra.discussondrawingsapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.ishanvohra.discussondrawingsapp.R;
import com.ishanvohra.discussondrawingsapp.activities.DrawingActivity;
import com.ishanvohra.discussondrawingsapp.data.Drawing;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DrawingListAdapter extends RecyclerView.Adapter<DrawingListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Drawing> dataSet;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public void setDrawingList(ArrayList<Drawing> dataSet){
        this.dataSet = dataSet;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView titleTv;
        private ImageView imageView;

        public MyViewHolder(View itemView){
            super(itemView);
            titleTv = itemView.findViewById(R.id.drawing_list_item_tv);
            imageView = itemView.findViewById(R.id.drawing_list_item_iv);
        }
    }

    public DrawingListAdapter(Context context, ArrayList<Drawing> dataSet){
        this.dataSet = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawings_list_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final @NonNull MyViewHolder holder, int position) {
        final Drawing drawing = dataSet.get(position);

        holder.titleTv.setText(drawing.getTitle());

        storage.getReference().child("drawings/" + drawing.getDrawingId() + "/drawing.jpeg")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(holder.imageView);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DrawingActivity.class);
                intent.putExtra("drawingId", drawing.getDrawingId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
