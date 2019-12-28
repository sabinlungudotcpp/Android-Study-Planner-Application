package com.example.twitterclone;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

// Purpose of Class: Main Activity that loads the main screen of the Android Application
// Author of Application: Sabin Constantin Lungu (All Rights Reserved)
// Date of creation: 24th December 2019
// Any bugs?: null


public class MainActivity extends AppCompatActivity { // Main activity class
    private Button registerStudentPlanButton; // Button for registering the student
    private Button createStudyPlanButton; // Button for creating study plan.

    private String registerText = "Register Student Please";
    private String createPlanText = "Create Study Plan";

    private boolean hasSwitched = false; // Boolean variable to determine whether the user switched
    private boolean isRegistered = true;

    protected void onCreate(Bundle savedInstanceState) { // Method invoked to run Android Application. 1st Stage in Android Cycle
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.registerStudentPlanButton = findViewById(R.id.registerStudentPlanBtn); // Initialise component
        this.createStudyPlanButton =  findViewById(R.id.createStudentPlanBtn); // Initialise create study plan component

        this.registerStudentPlanButton.setOnClickListener(new View.OnClickListener() { // Listener for the register student plan button

            public void onClick(View view) {

                Toast.makeText(MainActivity.this, registerText, Toast.LENGTH_SHORT).show();
                hasSwitched = true; // User switched

                Intent switchToRegisterStudent = new Intent(MainActivity.this, RegisterStudentActivity.class); // Switch over to the register student activity class
                startActivity(switchToRegisterStudent); // Start the activity

            }
        });

        this.createStudyPlanButton.setOnClickListener(new View.OnClickListener() { // Create a listener for the create study plan button

            public void onClick(View view) {
             AlertDialog.Builder msgUser = new AlertDialog.Builder(MainActivity.this).setTitle("WARNING").setMessage("You must register before doing this").setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     Intent intent = new Intent(MainActivity.this, RegisterStudentActivity.class);
                     startActivity(intent);
                 }
             });

             msgUser.show();
             msgUser.setOnCancelListener(new DialogInterface.OnCancelListener() {
                 @Override
                 public void onCancel(DialogInterface dialog) {
                     dialog.cancel();
                 }
             });
            }
        });
    }
}