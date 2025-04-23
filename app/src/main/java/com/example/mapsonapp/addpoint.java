package com.example.mapsonapp;

import static com.example.mapsonapp.MainActivity.AlertDialogGeneral;
import static com.example.mapsonapp.MainActivity.sUser;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;

public class addpoint extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference myRef;

    private TextView NameRef;
    private TextView M_CameraInfo;

    private EditText M_Name;
    private EditText M_Description;
    private EditText M_XCoordinate;
    private EditText M_YCoordinate;
    private double  xCoordinate;
    private double  yCoordinate;

    private LocationManager locationManager;
    private LocationListener locationListener;

    Button btnpicture;
    Bitmap cameraCapture;
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bitmap imageBitmap = (Bitmap) result.getData().getExtras().get("data");
                    cameraCapture = imageBitmap;
                    M_CameraInfo.setVisibility(View.VISIBLE);
                }
            });


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

        M_CameraInfo = findViewById(R.id.cameraInput);
        M_CameraInfo.setVisibility(View.INVISIBLE);

        NameRef = findViewById(R.id.showUser);
        NameRef.setText("User: " + MainActivity.sUser.getUsername());

        if(ContextCompat.checkSelfPermission(addpoint.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(addpoint.this, new String[]{Manifest.permission.CAMERA}, 100);
        }
        btnpicture = findViewById(R.id.openCamera);
        btnpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch(cameraIntent);

            }
        });
    }




    public void addData(View view)    {
        // Initialize EditText fields
        M_Name = (EditText) findViewById(R.id.enterName);
        M_Description = (EditText) findViewById(R.id.enterDescription);
        M_XCoordinate = (EditText) findViewById(R.id.XInput);
        M_YCoordinate = (EditText) findViewById(R.id.YInput);

        xCoordinate = Double.parseDouble(M_XCoordinate.getText().toString());
        yCoordinate = Double.parseDouble(M_YCoordinate.getText().toString());


        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance();
        final DatabaseReference SharePointRef = database.getReference("SharePoint");
        final DatabaseReference UserPointsRef = database.getReference("Users").child(MainActivity.sUser.getUsername()).child("UserPoints");

        String photo;
        if(cameraCapture == null)
            photo = "none";
        else photo = bitmapToBase64(cameraCapture);


        final SharePoint point = new SharePoint(M_Name.getText().toString(),M_Description.getText().toString(),xCoordinate , yCoordinate, photo, MainActivity.sUser.getUsername());
        SharePointRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(point.getName()).exists())     //check if a child with the username already exists
                    AlertDialogGeneral(addpoint.this, "ok", "The point name is already exist");
                else {
                    SharePointRef.child(point.getName()).setValue(point);
                    UserPointsRef.child(point.getName()).setValue(point);

                    //create a new child with name of username and set it value to user object
                    AlertDialogGeneral(addpoint.this, "ok", "Point added successfully");
                    cameraCapture = null;
                    M_Name.setText("");
                    M_Description.setText("");
                    M_XCoordinate.setText("");
                    M_YCoordinate.setText("");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void returnToMap(View view) {
        Intent myIntent = new Intent(addpoint.this, MapActivity.class);
        startActivity(myIntent);
    }
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    public void addMyLocation(View view) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                M_XCoordinate = (EditText) findViewById(R.id.XInput);
                M_YCoordinate = (EditText) findViewById(R.id.YInput);
                // This method is called when the location is updated
                xCoordinate =location.getLatitude();
                yCoordinate =location.getLongitude();
                M_XCoordinate.setText( "" + xCoordinate);
                M_YCoordinate.setText( "" + yCoordinate);
                Toast.makeText(addpoint.this, "Location: " + xCoordinate + ", " + yCoordinate, Toast.LENGTH_LONG).show();

            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // You can handle provider status changes here if needed
            }

            @Override
            public void onProviderEnabled(String provider) {
                // This is called when the GPS provider is enabled
                Toast.makeText(addpoint.this,"GPS provider is enabled" , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                // This is called when the GPS provider is disabled
                Toast.makeText(addpoint.this,"GPS provider is disabled" , Toast.LENGTH_LONG).show();

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