package com.example.mapsonapp;

import android.content.Intent;
import android.os.Bundle;
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

public class addTrail extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    private EditText M_Name;
    private EditText M_Description;
    private EditText M_XCordinate1;
    private EditText M_YCordinate1;
    private EditText M_XCordinate2;
    private EditText M_YCordinate2;
    private double  xCoordinate1;
    private double  yCoordinate1;
    private double  xCoordinate2;
    private double  yCoordinate2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_trail);
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
        M_XCordinate1 = (EditText) findViewById(R.id.XInput1);
        M_YCordinate1 = (EditText) findViewById(R.id.YInput1);
        M_XCordinate2 = (EditText) findViewById(R.id.XInput2);
        M_YCordinate2 = (EditText) findViewById(R.id.YInput2);

        xCoordinate1 = Double.parseDouble(M_XCordinate1.getText().toString());
        yCoordinate1 = Double.parseDouble(M_YCordinate1.getText().toString());
        xCoordinate2 = Double.parseDouble(M_XCordinate2.getText().toString());
        yCoordinate2 = Double.parseDouble(M_YCordinate2.getText().toString());


        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance();
        final DatabaseReference TrailRef = database.getReference("Trail");

        final Trail trail = new Trail(M_Name.getText().toString(),M_Description.getText().toString(),xCoordinate1 , yCoordinate1,xCoordinate2 , yCoordinate2);
        TrailRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(trail.getName()).exists())     //check if a child with the username already exists
                    Toast.makeText(addTrail.this, "the Trail name is already exist", Toast.LENGTH_SHORT).show();
                else {
                    TrailRef.child(trail.getName()).setValue(trail);
                    //create a new child with name of username and set it value to user object
                    Toast.makeText(addTrail.this,"Trail added successfuly",Toast.LENGTH_SHORT).show();
                    M_Name.setText("");
                    M_Description.setText("");
                    M_XCordinate1.setText("");
                    M_YCordinate1.setText("");
                    M_XCordinate2.setText("");
                    M_YCordinate2.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void returnToMain(View view) {
        Intent myIntent = new Intent(addTrail.this, MainActivity.class);
        startActivity(myIntent);
    }
}