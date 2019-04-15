package com.mms.recyc;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layManage;
    private ArrayList<String> dataset;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataset = new ArrayList<>();
        initDataset();

        rvView  = findViewById(R.id.rv_main);
        rvView.setHasFixedSize(true);

        layManage = new LinearLayoutManager(this);
        rvView.setLayoutManager(layManage);

        adapter = new RecyclerViewAdapter(dataset);
        rvView.setAdapter(adapter);
    }

    private void initDataset(){
        dataset.add("Karin");
        dataset.add("Ingrid");
        dataset.add("Helga");
        dataset.add("Ashley");
        dataset.add("Elke");
        dataset.add("Ursula");
        dataset.add("Erika");
        dataset.add("Christa");
        dataset.add("Gisela");
        dataset.add("Monika");
        dataset.add("Robert");
    }
}
