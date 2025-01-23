package com.example.mapsonapp;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
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

    private MapView mapView1;
    FirebaseDatabase database;
    DatabaseReference SharePointRef;
    DatabaseReference TrailRef;
    LocationManager locationManager;
    private MyLocationNewOverlay mLocationOverlay;


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


        database = FirebaseDatabase.getInstance();
        SharePointRef = database.getReference("SharePoint");
        TrailRef = database.getReference("Trail");
        buildMap();



      /*  if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.
                        listener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {}*/
/*
                if(marker != null)
                {
                    marker.remove();
                }
*/
/*    }
}
        */


            //marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
        //    @Override
        //    public boolean onMarkerClick(Marker marker, MapView mapView) {
        //        Intent myIntent = new Intent(MapActivity.this, FeedActivity.class);
        //       startActivity(myIntent);
        //        return true;
        //    }
        //});

        //mapView1.getOverlays().add(marker);



        Location userLoction = new Location(LocationManager.GPS_PROVIDER);
        GeoPoint userCord = new GeoPoint(userLoction.getLatitude(),userLoction.getLongitude());
        Marker marker = new Marker(mapView1);
        marker.setPosition(userCord);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("your location");
        mapView1.getOverlays().add(marker);

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
    private void buildMap() {
        SharePointRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap :dataSnapshot.getChildren()) {
                    SharePoint point = snap.getValue(SharePoint.class);

                    //set on map
                    Marker marker = new Marker(mapView1);
                    GeoPoint startPoint = new GeoPoint(point.getPlacement().getLatitude(), point.getPlacement().getLongitude()); // Example: Park Hamoshava
                    marker.setPosition(startPoint);
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    marker.setTitle(point.getName());
                    mapView1.getOverlays().add(marker);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        TrailRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
        });
    }
    public void returnToMain(View view) {
        Intent myIntent = new Intent(MapActivity.this, MainActivity.class);
        startActivity(myIntent);
    }


}

