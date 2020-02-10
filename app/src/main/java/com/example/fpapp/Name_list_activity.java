package com.example.fpapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Name_list_activity extends MainActivity{

    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_list);
        int currentScreenOrientation = this.getResources().getConfiguration().orientation;
        recyclerView = findViewById(R.id.names_list);
        recyclerView.setHasFixedSize(true);

        // portrait mode vertical list
        if (currentScreenOrientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        // landscape mode horizontal list
        if (currentScreenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        }



    }
}