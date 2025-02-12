package com.example.mapsonapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    private TextView M_XOutput1;
    private TextView M_YOutput1;

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

    private LocationManager locationManager;
    private LocationListener locationListener;
    private int time =0;
    private boolean flag = false;
    private boolean stopRecordFlag = false;


    private Button M_StopRecording;
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
    public void recordTrail(View view){
       /* M_StopRecording = (Button) findViewById(R.id.recordTrail);
        if( stopRecordFlag){
            locationManager.removeUpdates(locationListener);
            M_StopRecording.setText("Record Trail");
        }

        M_StopRecording.setText("Stop Recording");
        stopRecordFlag =true;*/
        M_Name = (EditText) findViewById(R.id.enterName);
        M_Description = (EditText) findViewById(R.id.enterDescription);
        M_XOutput1 = (TextView) findViewById(R.id.XOutput1);
        M_YOutput1 = (TextView) findViewById(R.id.YOutput1);
        M_XCordinate1 = (EditText) findViewById(R.id.XInput1);
        M_YCordinate1 = (EditText) findViewById(R.id.YInput1);
        M_XCordinate2 = (EditText) findViewById(R.id.XInput2);
        M_YCordinate2 = (EditText) findViewById(R.id.YInput2);

        if(M_Name.getText().toString().equals("")||M_Description.getText().toString().equals("")){
            Toast.makeText(addTrail.this, "Name or Description not entered, Please enter", Toast.LENGTH_LONG).show();
            M_Name.setText("");
            M_Description.setText("");
            M_XCordinate1.setText("");
            M_YCordinate1.setText("");
            M_XCordinate2.setText("");
            M_YCordinate2.setText("");
        }
        else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    // This method is called when the location is updated
                    double xCoordinate = location.getLatitude();
                    double yCoordinate = location.getLongitude();
                    Toast.makeText(addTrail.this, "Location: " + xCoordinate + ", " + yCoordinate, Toast.LENGTH_LONG).show();
                    if (time == 0) {
                        M_XOutput1.setText("Start location X cordinate: ");
                        M_YOutput1.setText("Start location Y cordinate: ");
                        M_XCordinate1.setText("" + xCoordinate);
                        M_YCordinate1.setText("" + yCoordinate);
                    }
                    if (time == 1) {
                        M_XCordinate2.setText("" + xCoordinate);
                        M_YCordinate2.setText("" + yCoordinate);
                        xCoordinate1 = Double.parseDouble(M_XCordinate1.getText().toString());
                        yCoordinate1 = Double.parseDouble(M_YCordinate1.getText().toString());
                        xCoordinate2 = Double.parseDouble(M_XCordinate2.getText().toString());
                        yCoordinate2 = Double.parseDouble(M_YCordinate2.getText().toString());
                        // Initialize Firebase database reference
                        database = FirebaseDatabase.getInstance();
                        final DatabaseReference TrailRef = database.getReference("Trail");

                        final Trail trail = new Trail(M_Name.getText().toString(), M_Description.getText().toString(), xCoordinate1, yCoordinate1, xCoordinate2, yCoordinate2);
                        TrailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.child(trail.getName()).exists()) {     //check if a child with the username already exists
                                    Toast.makeText(addTrail.this, "the trail name is already exist, change it", Toast.LENGTH_SHORT).show();
                                    flag = true;
                                } else {
                                    TrailRef.child(trail.getName()).setValue(trail);
                                    //create a new child with name of username and set it value to user object
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        if (flag) return;
                    }
                    if (time > 1) {
                        M_XCordinate2.setText("" + xCoordinate);
                        M_YCordinate2.setText("" + yCoordinate);
                        database = FirebaseDatabase.getInstance();
                        final DatabaseReference TrailRef = database.getReference("Trail");
                        TrailRef.child(M_Name.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) { // Ensure the node exists
                                    Trail trail = snapshot.getValue(Trail.class);
                                    if (trail != null) {
                                        trail.addCord(xCoordinate, yCoordinate); // Modify the object
                                        TrailRef.child(trail.getName()).setValue(trail);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    time++;
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    // You can handle provider status changes here if needed
                }

                @Override
                public void onProviderEnabled(String provider) {
                    // This is called when the GPS provider is enabled
                    Toast.makeText(addTrail.this, "GPS provider is enabled", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onProviderDisabled(String provider) {
                    // This is called when the GPS provider is disabled
                    Toast.makeText(addTrail.this, "GPS provider is disabled", Toast.LENGTH_LONG).show();

                }
            };

            // Check if location permissions are granted, and request location updates
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1, locationListener);
                // For GPS provider, you could use LocationManager.GPS_PROVIDER instead
                // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
            } else {
                // Handle the case where permission is not granted
                // You need to request permission from the user (e.g., using ActivityCompat.requestPermissions)
            }

        }
    }
}