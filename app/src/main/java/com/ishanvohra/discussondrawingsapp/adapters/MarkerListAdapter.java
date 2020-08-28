package com.ishanvohra.discussondrawingsapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.ishanvohra.discussondrawingsapp.R;
import com.ishanvohra.discussondrawingsapp.data.Marker;

import java.util.ArrayList;

public class MarkerListAdapter extends RecyclerView.Adapter<MarkerListAdapter.MyViewHolder> {

    private Context context;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private ArrayList<Marker> dataSet;
    private MarkerListAdapterListener listAdapterListener;

    public interface MarkerListAdapterListener{
        public void setMarker(float x, float y);
    }

    public void setMarkers(ArrayList<Marker> dataSet){
        this.dataSet = dataSet;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView userNameTv, labelTv;
        private RelativeTimeTextView timeTv;
        private ImageView imageView;

        public MyViewHolder(View itemView){
            super(itemView);
            userNameTv = itemView.findViewById(R.id.marker_list_item_username_tv);
            labelTv = itemView.findViewById(R.id.marker_list_item_title_tv);
            imageView = itemView.findViewById(R.id.marker_list_item_iv);
            timeTv = itemView.findViewById(R.id.marker_list_item_time_tv);
        }
    }

    public MarkerListAdapter(Context context, ArrayList<Marker> dataSet,MarkerListAdapterListener listAdapterListener){
        this.context = context;
        this.dataSet = dataSet;
        this.listAdapterListener = listAdapterListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.marker_list_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final Marker marker = dataSet.get(position);

        holder.labelTv.setText(marker.getContentEt());
        holder.timeTv.setReferenceTime(marker.getAdditionTime());

        storage.getReference().child("markers/" + marker.getMarkerId() + "/image.jpeg")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(holder.imageView);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listAdapterListener.setMarker(marker.getX(), marker.getY());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
