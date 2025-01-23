package com.example.mapsonapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class addpoint extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference myRef;
    private EditText M_Name;
    private EditText M_Description;
    private EditText M_XCordinate;
    private EditText M_YCordinate;
    private double  xCoordinate;
    private double  yCoordinate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_addpoint);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void addData(View view)    {
        // Initialize EditText fields
        M_Name = (EditText) findViewById(R.id.enterName);
        M_Description = (EditText) findViewById(R.id.enterDescription);
        M_XCordinate = (EditText) findViewById(R.id.XInput);
        M_YCordinate = (EditText) findViewById(R.id.YInput);

        xCoordinate = Double.parseDouble(M_XCordinate.getText().toString());
        yCoordinate = Double.parseDouble(M_YCordinate.getText().toString());


        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance();
        final DatabaseReference SharePointRef = database.getReference("SharePoint");

        final SharePoint point = new SharePoint(M_Name.getText().toString(),M_Description.getText().toString(),xCoordinate , yCoordinate);
        SharePointRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(point.getName()).exists())     //check if a child with the username already exists
                    Toast.makeText(addpoint.this, "the point name is already exist", Toast.LENGTH_SHORT).show();
                else {
                    SharePointRef.child(point.getName()).setValue(point);
                    //create a new child with nam  e of username and set it value to user object
                    Toast.makeText(addpoint.this,"Point added successfuly",Toast.LENGTH_SHORT).show();
                    M_Name.setText("");
                    M_Description.setText("");
                    M_XCordinate.setText("");
                    M_YCordinate.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void returnToMain(View view) {
        Intent myIntent = new Intent(addpoint.this, MainActivity.class);
        startActivity(myIntent);
    }

}