package com.ishanvohra.discussondrawingsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.ishanvohra.discussondrawingsapp.adapters.DrawingListAdapter;
import com.ishanvohra.discussondrawingsapp.data.Drawing;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private DrawingListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        recyclerView = findViewById(R.id.main_activity_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DrawingListAdapter(this, new ArrayList<Drawing>());
        recyclerView.setAdapter(adapter);

        Drawing d1 = new Drawing();
        d1.setDrawingId("abcd");
        d1.setTitle("Title 1");

        Drawing d2 = new Drawing();
        d2.setDrawingId("abcd1");
        d2.setTitle("Title 2");

        Drawing d3 = new Drawing();
        d3.setDrawingId("abcd2");
        d3.setTitle("Title 3");

        ArrayList<Drawing> drawings = new ArrayList<>();
        drawings.add(d1);
        drawings.add(d2);
        drawings.add(d3);

        adapter.setDrawingList(drawings);
        adapter.notifyDataSetChanged();
    }
}