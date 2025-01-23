package com.example.mapsonapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }
    public void returnToMap(View view) {
        Intent myIntent = new Intent(this, MapActivity.class);
        startActivity(myIntent);
    }
    public void moveToAddPoint(View view) {
        Intent myIntent = new Intent(this, addpoint.class);
        startActivity(myIntent);
    }
    public void moveToAddTrail(View view) {
        Intent myIntent = new Intent(this, addTrail.class);
        startActivity(myIntent);
    }
}