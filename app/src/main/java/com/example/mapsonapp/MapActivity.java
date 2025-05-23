package com.example.mapsonapp;

import static com.example.mapsonapp.MainActivity.AlertDialogGeneral;
import static com.example.mapsonapp.MainActivity.sUser;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.DrawableRes;
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

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferencesFactory;


public class MapActivity extends AppCompatActivity {

    ArrayList<String> UserComm = new ArrayList<>();
    ArrayList<String> users = new ArrayList<>();

    private Marker myLocation;

    private TextView NameRef;
    private TextView testRef;
    private EditText M_Community_Name;
    private TextView M_CommunityPass;

    private MapView mapView1;
    FirebaseDatabase database;
    DatabaseReference SharePointRef;
    DatabaseReference TrailRef;


    private LocationManager locationManager;
    private LocationListener locationListener;

    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




        context = this;

        Context ctx = this.getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_map);

        mapView1 = findViewById(R.id.mapView);
        mapView1.setMultiTouchControls(true);
        mapView1.setTileSource(TileSourceFactory.MAPNIK);
        mapView1.getController().setZoom(9.0);


        // Set initial map settings
        GeoPoint startPoint = new GeoPoint(32.097, 34.812); // Example: Tel Aviv
        mapView1.getController().setCenter(startPoint);


        NameRef = findViewById(R.id.showUser);
        NameRef.setText("User: " + MainActivity.sUser.getUsername());

        database = FirebaseDatabase.getInstance();
        SharePointRef = database.getReference("SharePoint");
        TrailRef = database.getReference("Trail");
        buildMap();









    }



    @Override
    protected void onResume() {
        super.onResume();
        mapView1.onResume(); // Needed for OSMDroid
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView1.onPause(); // Needed for OSMDroid
    }
    public void findMyLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // This method is called when the location is updated
                double xCoordinate = location.getLatitude();
                double yCoordinate = location.getLongitude();
                if(myLocation!=null)
                    mapView1.getOverlays().remove(myLocation);
                myLocation = new Marker(mapView1);
                GeoPoint startPoint = new GeoPoint(xCoordinate, yCoordinate);
                myLocation.setPosition(startPoint);
                myLocation.setTitle("My Location");
                myLocation.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.my);
                myLocation.setIcon(drawable);
                mapView1.getController().setCenter(startPoint);
                mapView1.getOverlays().add(myLocation);
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // You can handle provider status changes here if needed
            }

            @Override
            public void onProviderEnabled(String provider) {
                // This is called when the GPS provider is enabled
                AlertDialogGeneral(MapActivity.this, "ok", "GPS provider is enabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                // This is called when the GPS provider is disabled
                AlertDialogGeneral(MapActivity.this, "ok", "GPS provider is disabled");
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

    private void buildMap() {
        CreateCommList();
        //Show user location
        findMyLocation();

        //Show User own points(Working)
        final DatabaseReference UserPointsRef = database.getReference("Users").child(MainActivity.sUser.getUsername()).child("UserPoints");
        UserPointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap :snapshot.getChildren()) {
                    SharePoint point = snap.getValue(SharePoint.class);

                    //set on map
                    Marker marker = new Marker(mapView1);
                    GeoPoint startPoint = new GeoPoint(point.getPlacement().getLatitude(), point.getPlacement().getLongitude());
                    marker.setPosition(startPoint);
                    marker.setTitle(point.getName());
                    marker.setSubDescription(point.getText() + "\t\t\t  Source: " + point.getCreatorName());
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    Drawable drawable = ContextCompat.getDrawable(context, R.drawable.dote4);
                    marker.setIcon(drawable);
                    if(!(point.getPhoto().equals("none"))){
                        Drawable drawable2 = base64ToDrawable(context ,point.getPhoto());
                        marker.setImage(drawable2);
                    }
                    mapView1.getOverlays().add(marker);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Show User own Trails(Working)
        final DatabaseReference UserTrailsRef = database.getReference("Users").child(MainActivity.sUser.getUsername()).child("UserTrails");
        UserTrailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap :snapshot.getChildren()) {
                    Trail trail = snap.getValue(Trail.class);

                    //set on map
                    Marker marker = new Marker(mapView1);
                    GeoPoint startPoint = new GeoPoint(trail.getPlacement().getLatitude(), trail.getPlacement().getLongitude());
                    marker.setPosition(startPoint);
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    marker.setTitle(trail.getName() + ".trail");
                    marker.setSubDescription(trail.getText() + "\t\t\t Source: "+ trail.getCreatorName());
                    Drawable drawable = ContextCompat.getDrawable(context, R.drawable.dote1);
                    marker.setIcon(drawable);
                    if(!(trail.getPhoto().equals("none"))){
                        Drawable drawable2 = base64ToDrawable(context ,trail.getPhoto());
                        marker.setImage(drawable2);
                    }
                    mapView1.getOverlays().add(marker);
                    // Create the Polyline
                    List<GeoPoint> points = new ArrayList<>();
                    for (MyGeoPoint P: trail.getTrail() ) {
                        points.add(new GeoPoint(P.getLatitude(), P.getLongitude()));
                    }
                    Polyline polyline = new Polyline();
                    polyline.setPoints(points);
                    polyline.setColor(0xFF800080); // purple color
                    polyline.setWidth(10.0f); // Line width
                    // Add the Polyline to the map
                    mapView1.getOverlayManager().add(polyline);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });








        //Show User communities points




        /*TrailRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap :snapshot.getChildren()) {
                    Trail trail = snap.getValue(Trail.class);

                    //set on map
                    Marker marker = new Marker(mapView1);
                    GeoPoint startPoint = new GeoPoint(trail.getPlacement().getLatitude(), trail.getPlacement().getLongitude());
                    marker.setPosition(startPoint);
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    marker.setTitle(trail.getName() + ".trail");
                    marker.setSubDescription(trail.getText() + "\t\t\t Source: "+ trail.getText());
                    Drawable drawable = ContextCompat.getDrawable(context, R.drawable.dote4);
                    marker.setIcon(drawable);
                    if(!(trail.getPhoto().equals("none"))){
                        Drawable drawable2 = base64ToDrawable(context ,trail.getPhoto());
                        marker.setImage(drawable2);
                    }
                    mapView1.getOverlays().add(marker);
                    // Create the Polyline
                   List<GeoPoint> points = new ArrayList<>();
                    for (MyGeoPoint P: trail.getTrail() ) {
                        points.add(new GeoPoint(P.getLatitude(), P.getLongitude()));
                    }
                    Polyline polyline = new Polyline();
                    polyline.setPoints(points);
                    polyline.setColor(0xFF0000FF); // Blue color
                    polyline.setWidth(10.0f); // Line width
                    // Add the Polyline to the map
                    mapView1.getOverlayManager().add(polyline);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }
    public static Drawable base64ToDrawable(Context context, String base64String) {
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            return new BitmapDrawable(context.getResources(), bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if decoding fails
        }
    }
    public void CreateCommList()
    {
        final DatabaseReference UserCommRef = database.getReference("Users").child(MainActivity.sUser.getUsername()).child("CommNames");
        UserCommRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren())
                {
                    String name = snap.getValue(String.class);
                    UserComm.add(name);
                    testRef = findViewById(R.id.textView);
                    String s = testRef.getText().toString();
                    testRef.setText(s + "\t" + name);
                }
                CreateUserList();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void CreateUserList() {
        testRef = findViewById(R.id.textView);
        testRef.setText("");
        for(String s: UserComm)
        {
            final DatabaseReference CommUserRef = database.getReference("Communities").child(s).child("Users");
            CommUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot snap : snapshot.getChildren())
                    {
                        String name = snap.getValue(String.class);
                        if(!isExist(users, name))
                        {
                            if(!name.equals(sUser.getUsername()))
                            {
                                users.add(name);
                                ShowUserPointsAndTrails(name);
                                /*testRef = findViewById(R.id.textView);
                                String st = testRef.getText().toString();
                                testRef.setText(st + "\t" + name);*/
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }


    }
    public void ShowUserPointsAndTrails(String user) {

            final DatabaseReference CommUserPointsRef = database.getReference("Users").child(user).child("UserPoints");
            CommUserPointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap :snapshot.getChildren()) {
                        SharePoint point = snap.getValue(SharePoint.class);

                        //set on map
                        Marker marker = new Marker(mapView1);
                        GeoPoint startPoint = new GeoPoint(point.getPlacement().getLatitude(), point.getPlacement().getLongitude());
                        marker.setPosition(startPoint);
                        marker.setTitle(point.getName());
                        marker.setSubDescription(point.getText() + "\t\t\t  Source: " + point.getCreatorName());
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.dote2);
                        marker.setIcon(drawable);
                        if(!(point.getPhoto().equals("none"))){
                            Drawable drawable2 = base64ToDrawable(context ,point.getPhoto());
                            marker.setImage(drawable2);
                        }
                        mapView1.getOverlays().add(marker);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            final DatabaseReference CommUserTrailsRef = database.getReference("Users").child(user).child("UserTrails");
            CommUserTrailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap :snapshot.getChildren()) {
                        Trail trail = snap.getValue(Trail.class);

                        //set on map
                        Marker marker = new Marker(mapView1);
                        GeoPoint startPoint = new GeoPoint(trail.getPlacement().getLatitude(), trail.getPlacement().getLongitude());
                        marker.setPosition(startPoint);
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        marker.setTitle(trail.getName() + ".trail");
                        marker.setSubDescription(trail.getText() + "\t\t\t Source: "+ trail.getCreatorName());
                        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.dote1);
                        marker.setIcon(drawable);
                        if(!(trail.getPhoto().equals("none"))){
                            Drawable drawable2 = base64ToDrawable(context ,trail.getPhoto());
                            marker.setImage(drawable2);
                        }
                        mapView1.getOverlays().add(marker);
                        // Create the Polyline
                        List<GeoPoint> points = new ArrayList<>();
                        for (MyGeoPoint P: trail.getTrail() ) {
                            points.add(new GeoPoint(P.getLatitude(), P.getLongitude()));
                        }
                        Polyline polyline = new Polyline();
                        polyline.setPoints(points);
                        polyline.setColor(0xFFFFA500); // Orange color
                        polyline.setWidth(10.0f); // Line width
                        // Add the Polyline to the map
                        mapView1.getOverlayManager().add(polyline);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }



    //public void onClickContinue(View view){   ShowCommPointsAndTrails();  }
    public void returnToMain(View view) {
        Intent myIntent = new Intent(MapActivity.this, MainActivity.class);
        startActivity(myIntent);
    }

    public void moveToAddPoint(View view) {
        CreateUserList();
       /* Intent myIntent = new Intent(this, addpoint.class);
        startActivity(myIntent);*/
    }
    public void moveToAddTrail(View view) {
        Intent myIntent = new Intent(this, addTrail.class);
        startActivity(myIntent);
    }
    public void onClickCreateComm(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View diologView = inflater.inflate(R.layout.create_community, null);
        builder.setView(diologView);
        M_Community_Name = diologView.findViewById(R.id.communityName);
        EditText M_CommPass =  diologView.findViewById(R.id.password);
        Switch switchQuestion = diologView.findViewById(R.id.switchQuestion);
        builder.setTitle("Create Community").setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = M_Community_Name.getText().toString();
                String password = M_CommPass.getText().toString();
                Boolean isPublic = switchQuestion.isChecked();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference communityRef =database.getReference("Communities");
                final DatabaseReference commUsersRef = database.getReference("Communities").child(name).child("Users");
                final Community comm = new Community(name, password, MainActivity.sUser.getUsername() ,isPublic);
                communityRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(comm.getName()).exists())  //check if a child with the username already exists
                            AlertDialogGeneral(MapActivity.this,"ok","Community name already exists");
                        else {
                            communityRef.child(comm.getName()).setValue(comm);
                            commUsersRef.child(MainActivity.sUser.getUsername()).setValue(sUser.getUsername());
                            DatabaseReference userCommNames = database.getReference("Users").child(MainActivity.sUser.getUsername()).child("CommNames");
                            userCommNames.child(comm.getName()).setValue(comm.getName());




                            //create a new child with name of username and set it value to user object
                            AlertDialogGeneral(MapActivity.this,"ok",comm.getName() + " has created");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        builder.show();

    }

    public void onClickJoinCommunity(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View diologView = inflater.inflate(R.layout.join_community, null);
        builder.setView(diologView);
        M_CommunityPass = diologView.findViewById(R.id.CommPassword);
        Switch switchQuestion = diologView.findViewById(R.id.switch1);
        switchQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switchQuestion.isChecked()){
                    M_CommunityPass.setVisibility(view.INVISIBLE);
                }
            }
        });

        M_Community_Name = diologView.findViewById(R.id.communityName);
        builder.setTitle("Join Community").setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String communityName = M_Community_Name.getText().toString();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference communityRef =database.getReference("Communities");
                communityRef.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!communityName.isEmpty()&& dataSnapshot.child(communityName).exists())  //check if a child with the name already exists
                        {
                            boolean isIn = false;
                            Community comm = dataSnapshot.child(communityName).getValue(Community.class);
                            DatabaseReference commUsersRef = database.getReference("Communities").child(communityName).child("Users");
                            for(DataSnapshot snap : dataSnapshot.child("Users").getChildren()){
                                User user = snap.getValue(User.class);
                                if(user.getUsername().equals(MainActivity.sUser.getUsername())){
                                    isIn = true;
                                    AlertDialogGeneral(MapActivity.this,"ok","User in " + comm.getName() + " Community");
                                }
                            }
                            if(!isIn) {
                                if (comm.isOpen()) {
                                    commUsersRef.child(MainActivity.sUser.getUsername()).setValue(sUser.getUsername());
                                    DatabaseReference userCommNames = database.getReference("Users").child(MainActivity.sUser.getUsername()).child("CommNames");
                                    userCommNames.child(comm.getName()).setValue(comm.getName());
                                    AlertDialogGeneral(MapActivity.this, "ok", "You joined successfully, refresh map!");
                                } else {
                                    String pass = M_CommunityPass.getText().toString();
                                    if (comm.getPassword().equals(pass)) {
                                        commUsersRef.child(MainActivity.sUser.getUsername()).setValue(sUser.getUsername());
                                        DatabaseReference userCommNames = database.getReference("Users").child(MainActivity.sUser.getUsername()).child("CommNames");
                                        userCommNames.child(comm.getName()).setValue(comm.getName());
                                        AlertDialogGeneral(MapActivity.this, "ok", "You joined successfully, refresh map!");

                                    } else {
                                        AlertDialogGeneral(MapActivity.this, "ok", "Wrong password");
                                    }
                                }
                            }
                        }
                        else {
                            //create a new child with name of username and set it value to user object
                            AlertDialogGeneral(MapActivity.this,"ok","Community doesn't exists");
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

        builder.show();
    }


    public static boolean isExist(ArrayList<String> Names, String name)
    {
        if(Names.isEmpty())
            return false;
        for (String N : Names)
            if(N.equals(name))
                return true;
        return false;

    }
}

