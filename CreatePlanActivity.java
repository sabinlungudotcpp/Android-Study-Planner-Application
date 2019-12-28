package com.example.twitterclone;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Pattern;

// Purpose of Application: To create the Study Plan for the student
// Author: Sabin Constantin Lungu
// Project Start Date: 20/12/2019
// Date of Completion: 27/12/2019
// Any Errors? None (pending testing..)

public class CreatePlanActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText moduleInputField; // The module name input field
    private EditText dayInputField; // The day input field
    private EditText startTimeInputField;
    private EditText endTimeInputField;

    private Button addPlanButton; // Button to add the planner
    private Button deletePlanButton; // Button to delete the planner
    private ListView plannersListView; // The list view

    private ArrayList<StudyPlanner> listOfPlanners; // An Array list of planners created
    private ArrayList<String> plannerNames; // An Array list of planner names
    private StudyPlannerDB plannerDatabase; // Database Instance
    private ArrayAdapter plannerAdapter; // An Array Adapter instance

    private boolean startsWithUpperCase = false; // Condition that holds if the fields start with an upper case character
    private boolean isValid = true;
    private boolean hasLetters = false;

    private boolean hasRegex = false; // Flag to indicate if it has regex
    private boolean isEmpty = false;

    private Pattern regexPatterns = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]"); // Regex patterns

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);

        this.moduleInputField = findViewById(R.id.moduleInputTxt);
        this.dayInputField = findViewById(R.id.dayInputTxt);

        this.startTimeInputField = findViewById(R.id.startTimeInputTxt);
        this.endTimeInputField = findViewById(R.id.endTimeTxtInput);

        this.addPlanButton = findViewById(R.id.addPlanBtn);
        this.deletePlanButton = findViewById(R.id.deletePlanBtn);

        this.addPlanButton.setOnClickListener(this);
        this.deletePlanButton.setOnClickListener(this);

        this.plannersListView = findViewById(R.id.studyListPlanList);

        plannerDatabase = new StudyPlannerDB(CreatePlanActivity.this);
        this.plannerNames = new ArrayList<>(); // A new array list of planner names to store in the list view
        this.listOfPlanners = plannerDatabase.getAllPlanners();

        addToPlannerNames(); // Routine to add to an array list of strings, the planner names
    }

    public void addToPlannerNames() {
        if (listOfPlanners.size() > 0) { // If the list size is > 0
            for (int i = 0; i < listOfPlanners.size(); i++) { // Loop over the array list
                StudyPlanner planner = listOfPlanners.get(i); // Get the values from the array list and store them in the study planner instance

                plannerNames.add(planner.getModule() + " " + planner.getDay() + " " + planner.getStartTime() + " " + planner.getEndTime() + " ");
            }
        }

        this.plannerAdapter = new ArrayAdapter(CreatePlanActivity.this, android.R.layout.simple_list_item_1, plannerNames);
        plannersListView.setAdapter(plannerAdapter);
    }

    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.addPlanBtn: // If the add button is clicked
                // Step 1. Validate the entries for invalid data

                StudyPlanner newPlanner = new StudyPlanner(moduleInputField.getText().toString(), dayInputField.getText().toString(), startTimeInputField.getText().toString(), endTimeInputField.getText().toString());
                listOfPlanners.add(newPlanner); // Add the instance to the array list
                plannerDatabase.addStudyPlanner(newPlanner);
                plannerNames.add(newPlanner.getModule() + " " + newPlanner.getDay() + " " + newPlanner.getStartTime() + " " + newPlanner.getEndTime());

                validateModuleEntry();
                validateDayEntry();
                validateStartTimeEntry();
                validateEndTimeEntry();

                // Clears the fields
                moduleInputField.setText("");
                dayInputField.setText("");
                startTimeInputField.setText("");
                endTimeInputField.setText("");

                checkTimeClash();


                // Send E-mail to the student of planner
                Intent emailPlanner = new Intent(Intent.ACTION_SEND); // Set the action send to send e-mail
                emailPlanner.setType("message/rfc822");
                emailPlanner.putExtra(Intent.EXTRA_EMAIL, new String[]{"sabinlungu293@gmail.com"}); // Send to these e-mail address
                emailPlanner.putExtra(Intent.EXTRA_SUBJECT, "Your Study Planner");
                emailPlanner.putExtra(Intent.EXTRA_TEXT, moduleInputField.getText().toString());

                emailPlanner.putExtra(Intent.EXTRA_TEXT, dayInputField.getText().toString());
                emailPlanner.putExtra(Intent.EXTRA_TEXT, startTimeInputField.getText().toString());
                emailPlanner.putExtra(Intent.EXTRA_TEXT, endTimeInputField.getText().toString());

                try {
                    startActivity(Intent.createChooser(emailPlanner, "Sending E-mail..."));

                } catch (android.content.ActivityNotFoundException notFound) { // Catch exception

                    Log.i("Cause : ", notFound.getMessage()); // Get the cause
                }

                break;


            case R.id.deletePlanBtn: // If the delete button is clicked
                if (listOfPlanners.size() > 0) {

                    plannerNames.remove(0); // Remove the first planner
                    plannerDatabase.deletePlanner(listOfPlanners.get(0));
                    listOfPlanners.remove(0); // Remove it from the list

                } else {
                    return; // Otherwise return
                }

                break; // Break out of the case statement
        }

        plannerAdapter.notifyDataSetChanged(); // Notify that the data has been changed
    }

    public void validateModuleEntry() { // Validates the module field
        final String moduleText = moduleInputField.getText().toString();

        for(int i = 0; i < moduleText.length(); i++) { // Loop over the module entry

            if(Character.isLetter(moduleText.charAt(i)) && Character.isUpperCase(moduleText.charAt(0))) {
                AlertDialog.Builder moduleDialogue = new AlertDialog.Builder(CreatePlanActivity.this).setTitle("Valid Module Entry").setMessage("Valid Module");
                moduleDialogue.setCancelable(true);
                moduleDialogue.show();

                hasLetters = true;
                startsWithUpperCase = true;
                moduleInputField.setText("");
                break;
            }

            // If the module entry has a regex character present
            else if(regexPatterns.matcher(moduleText).find()) {
                AlertDialog.Builder regexDialogue = new AlertDialog.Builder(CreatePlanActivity.this).setTitle("Regex Warning").setMessage("Regex Character Found. Please Re-Enter").setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        plannersListView.setAdapter(null); // Clear out the list view
                        moduleInputField.setText("");
                    }
                });

                regexDialogue.setCancelable(true);
                regexDialogue.show();
                hasRegex = true;
                isValid = false;
                break;
            }

            // Check to see if the Module Field Entry does not contain letters and it doesn't start with an upper case character
            else if(!Character.isLetter(moduleText.charAt(i)) || !Character.isUpperCase(moduleText.charAt(0))) {
                AlertDialog.Builder notValidDialogue = new AlertDialog.Builder(CreatePlanActivity.this).setTitle("Not Valid Module").setMessage("Module entry not valid").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                notValidDialogue.setCancelable(true);
                notValidDialogue.show();

                hasLetters = false;
                startsWithUpperCase = false;
                moduleInputField.setText("");
                break;
            }

            else if(moduleText.matches("")) {
                AlertDialog.Builder emptyStringMsg = new AlertDialog.Builder(CreatePlanActivity.this).setTitle("Empty String Error").setMessage("Field cannot be empty");
                emptyStringMsg.setCancelable(true);
                emptyStringMsg.show(); // Show the dialogue

                moduleInputField.setText("");
                isEmpty = true;
                break;
            }
        }
    }

    public void validateDayEntry() { // Routine to validate the day input field
        String day = dayInputField.getText().toString();


        for(int i = 0; i < day.length(); i++) { // Loop over the module entry

            if(Character.isLetter(day.charAt(i)) && Character.isUpperCase(day.charAt(0))) {
                AlertDialog.Builder moduleDialogue = new AlertDialog.Builder(CreatePlanActivity.this).setTitle("Valid Day Entry").setMessage("Valid Day Entry");
                moduleDialogue.setCancelable(true);
                moduleDialogue.show();

                hasLetters = true;
                startsWithUpperCase = true;
                moduleInputField.setText("");
                break;
            }

            // If the module entry has a regex character present
            else if(regexPatterns.matcher(day).find()) {
                AlertDialog.Builder regexDialogue = new AlertDialog.Builder(CreatePlanActivity.this).setTitle("Regex Warning").setMessage("Regex Character Found. Please Re-Enter").setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        plannersListView.setAdapter(null); // Clear out the list view
                        moduleInputField.setText("");
                    }
                });

                regexDialogue.setCancelable(true);
                regexDialogue.show();
                hasRegex = true;
                isValid = false;
                break;
            }

            // Check to see if the Module Field Entry does not contain letters and it doesn't start with an upper case character
            else if(!Character.isLetter(day.charAt(i)) || !Character.isUpperCase(day.charAt(0))) {
                AlertDialog.Builder notValidDialogue = new AlertDialog.Builder(CreatePlanActivity.this).setTitle("Not Valid Module").setMessage("Day entry not valid").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                notValidDialogue.setCancelable(true);
                notValidDialogue.show();

                hasLetters = false;
                startsWithUpperCase = false;
                moduleInputField.setText("");
                break;
            }

            else if(day.matches("")) {
                AlertDialog.Builder emptyStringMsg = new AlertDialog.Builder(CreatePlanActivity.this).setTitle("Empty String Error").setMessage("Field cannot be empty");
                emptyStringMsg.setCancelable(true);
                emptyStringMsg.show();

                moduleInputField.setText("");
                isEmpty = true;
                break;
            }

            if(hasLetters && startsWithUpperCase && isValid && !isEmpty) {
                Toast.makeText(CreatePlanActivity.this, "Day Successfully VALIDATED", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void validateStartTimeEntry() { // Validates the start time entry
        String startTimeText = startTimeInputField.getText().toString();

        for(int i = 0; i < startTimeText.length(); i++) { // Loop over the module entry

            if(!Character.isLetter(startTimeText.charAt(i)) && Character.isDigit(startTimeText.charAt(i))) {
                AlertDialog.Builder moduleDialogue = new AlertDialog.Builder(CreatePlanActivity.this).setTitle("Valid Day Entry").setMessage("Valid Start Time Entry");
                moduleDialogue.setCancelable(true);
                moduleDialogue.show();

                moduleInputField.setText("");
                break;
            }

            // If the module entry has a regex character present
            else if(!regexPatterns.matcher(startTimeText).find()) {
                AlertDialog.Builder regexDialogue = new AlertDialog.Builder(CreatePlanActivity.this).setTitle("Regex Warning").setMessage("Regex Character Found. Please Re-Enter").setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        plannersListView.setAdapter(null); // Clear out the list view
                        moduleInputField.setText("");
                    }
                });

                regexDialogue.setCancelable(true);
                regexDialogue.show();
                hasRegex = false;
                isValid = true;
                break;
              }
            }

             if(startTimeText.matches("")) {
                AlertDialog.Builder emptyStringMsg = new AlertDialog.Builder(CreatePlanActivity.this).setTitle("Empty String Error").setMessage("Field cannot be empty");
                emptyStringMsg.setCancelable(true);
                emptyStringMsg.show();

                moduleInputField.setText("");
                isEmpty = true;
            }
        }

        public void validateEndTimeEntry() { // Validates the end time
        String endTimeEntry = endTimeInputField.getText().toString(); // Get the text

            // Loop over the text

            for(int i = 0; i < endTimeEntry.length(); i++) {

                if(!Character.isLetter(endTimeEntry.charAt(i)) && Character.isDigit(endTimeEntry.charAt(i))) {
                   AlertDialog.Builder message = new AlertDialog.Builder(CreatePlanActivity.this).setTitle("Message").setMessage("End Time Valid");
                   hasLetters = false;
                   isValid = true;
                   break;
                }

               else if(!regexPatterns.matcher(endTimeEntry).find()) {
                    Toast.makeText(CreatePlanActivity.this, "Regex character found", Toast.LENGTH_LONG).show();
                    hasRegex = false;
                    break;
                }

               else if(endTimeEntry.matches("")) {
                   isEmpty = true;
                   break;
                }

            }

        }

         // Function in maintenance
        public void checkTimeClash() { // Routine that checks the time clash between the start time and end time
         String startTime = startTimeInputField.getText().toString();
         String endTime = endTimeInputField.getText().toString();

         for(int i = 0; i < startTime.length(); i++) { // Loop over the start time
             for(int j = 0; j < endTime.length(); j++) { // Loop over the end time

                 if(Character.isDigit(startTime.charAt(i)) == Character.isDigit(endTime.charAt(j))) {
                    final AlertDialog.Builder timeClash = new AlertDialog.Builder(CreatePlanActivity.this).setTitle("Time Clash").setMessage("Study Time Clashes. Re-Enter").setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    timeClash.setCancelable(true);
                    timeClash.show();
                    plannersListView.setAdapter(null);
                }
             }
         }
        }
}