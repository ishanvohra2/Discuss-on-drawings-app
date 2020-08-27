package com.ishanvohra.discussondrawingsapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ishanvohra.discussondrawingsapp.R;
import com.ishanvohra.discussondrawingsapp.data.Drawing;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DrawingListAdapter extends RecyclerView.Adapter<DrawingListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Drawing> dataSet;

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
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Drawing drawing = dataSet.get(position);

        holder.titleTv.setText(drawing.getTitle());

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
