package com.example.mapsonapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public static EditText UserName;
    public static EditText Password;
    static User sUser;
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
    public void Login(View view) {
        UserName = findViewById(R.id.editTextUsername);
        Password = findViewById(R.id.editTextPassword);
        final String username = UserName.getText().toString();
        final String password = Password.getText().toString();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference usersRef = database.getReference("Users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if ((!username.isEmpty())&&dataSnapshot.child(username).exists()) { //check if a child with the username already exists
                    User login = dataSnapshot.child(username).getValue(User.class); //get value from
                    if (login.getPassword().equals(password)) {
                        sUser = new User(username,password);
                        Intent myIntent = new Intent(MainActivity.this, MapActivity.class);
                        startActivity(myIntent);
                    }
                    else
                       AlertDialogGeneral(MainActivity.this,"ok","Wrong password");
                } else
                    AlertDialogGeneral(MainActivity.this,"ok","Username not found");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void Register(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View diologView = inflater.inflate(R.layout.register_layout, null);
        builder.setView(diologView);
        EditText etUsername = diologView.findViewById(R.id.etUsername);
        EditText etPassword = diologView.findViewById(R.id.etPassword);


        builder.setTitle("Register").setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference usersRef =database.getReference("Users");
                final User user = new User(username,password);
                usersRef.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(user.getUsername()).exists())  //check if a child with the username already exists
                            AlertDialogGeneral(MainActivity.this,"ok","Username already exists");
                        else {
                            usersRef.child(user.getUsername()).setValue(user);
                            //create a new child with name of username and set it value to user object
                            AlertDialogGeneral(MainActivity.this,"ok","Success Register");
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

     public static void AlertDialogGeneral(Activity activity, String op1, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setPositiveButton(op1, (dialog, which) -> {
            dialog.dismiss();
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
     }
}