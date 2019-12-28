package com.example.twitterclone;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import java.util.regex.Pattern;

public class RegisterStudentActivity extends AppCompatActivity {
    private EditText forenameField; // Forename field
    private EditText surnameField;

    private EditText emailField;
    private EditText degreeOfStudy; // Degree of study field

    private Button registerBtn; // Register Button
    private Button homeBtn; // Home Button
    private Button saveDataBtn;
    private Button getStudentDataBtn;

    private boolean hasUpperCaseCharacter = false; // Boolean flag to either hold true or false if the E-mail has an upper case character
    private boolean hasLetter = false;
    private boolean isValid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);

        // Initialise components

        this.forenameField = findViewById(R.id.forenameInputTextField);
        this.surnameField = findViewById(R.id.surnameLblText);
        this.emailField = findViewById(R.id.emailTextInputField);

        this.degreeOfStudy = findViewById(R.id.degreeOfStudyInputField);
        this.registerBtn = findViewById(R.id.registerBtn);

        this.homeBtn = findViewById(R.id.homeBtn);
        this.saveDataBtn = findViewById(R.id.saveDataBtn);
        this.getStudentDataBtn = findViewById(R.id.getStudentBtn);

        // Add action listeners for buttons

        this.registerBtn.setOnClickListener(new View.OnClickListener() { // Listener for the Register Button

            public void onClick(View view) {
                String foreNameText = forenameField.getText().toString(); // Get forename text
                String surnameText = surnameField.getText().toString();

                String emailText = emailField.getText().toString();
                String degreeText = degreeOfStudy.getText().toString();

                validateForename(foreNameText);
                validateSurname(surnameText);
                validateEmail(emailText);
                validateDegreeOfStudy(degreeText);

                createStudentStudyPlan(hasLetter, isValid);
            }
        });

        this.homeBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Create an Intent to go back to the home page
                Intent homeIntent = new Intent(RegisterStudentActivity.this, MainActivity.class);
                startActivity(homeIntent); // Start the activity
            }
        });

        this.saveDataBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String tempForenameText = forenameField.getText().toString();
                String tempSurnameText = surnameField.getText().toString();

                String tempEmailText = emailField.getText().toString();
                String tempDegreeText = degreeOfStudy.getText().toString();

                ParseObject studyStudentData = new ParseObject("StudyStudent"); // Create a Parse object to create the Student object
                studyStudentData.put("student_forename", tempForenameText);
                studyStudentData.put("student_surname", tempSurnameText);

                studyStudentData.put("student_email_address", tempEmailText);
                studyStudentData.put("student_degree_text", tempDegreeText);

                studyStudentData.saveInBackground(new SaveCallback() { // Method that will write the data to the external server

                    public void done(ParseException exc) {

                        if (exc == null) { // If there is no error
                            AlertDialog.Builder saveDataDialog = new AlertDialog.Builder(RegisterStudentActivity.this).setTitle("Data Write").setMessage("Student Data Written Successfully").setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivities(new Intent[]{intent});
                                }
                            })
                                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                            saveDataDialog.show(); // Show the dialogue
                            saveDataDialog.setCancelable(true);
                        }
                    }
                });
            }
        });

        this.getStudentDataBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final ParseQuery<ParseObject> getUsers = ParseQuery.getQuery("StudyStudent");

                getUsers.getInBackground("lUuT8mkotI", new GetCallback<ParseObject>() {

                    public void done(ParseObject object, ParseException e) {

                            if (object != null && e == null) {
                                AlertDialog.Builder showUser = new AlertDialog.Builder(RegisterStudentActivity.this).setTitle("User Data").setMessage("Below is the user data");
                                showUser.setCancelable(true);
                                showUser.show();

                                Toast.makeText(RegisterStudentActivity.this, object.get("student_forename") + " \n is the Forename " + object.get("student_surname") + " \n is the surname ", Toast.LENGTH_SHORT).show();
                            }
                    }
                });
            }
        });
    }

    public void validateForename(String foreNameText) {
        for (int index = 0; index < foreNameText.length(); index++) { // Loop over the forename input

            if (Character.isLetter(foreNameText.charAt(index)) && Character.isUpperCase(foreNameText.charAt(0))) {

                AlertDialog.Builder foreNameValidateMsg = new AlertDialog.Builder(RegisterStudentActivity.this).setTitle("Forename Validation").setMessage("Forename Validated Success");
                foreNameValidateMsg.setCancelable(true);
                foreNameValidateMsg.show();

                hasUpperCaseCharacter = true;
                hasLetter = true;
                forenameField.setText("");
                break;

            } else if (!Character.isUpperCase(foreNameText.charAt(0))) {
                Toast.makeText(RegisterStudentActivity.this, "Invalid Forename", Toast.LENGTH_SHORT).show();
                hasUpperCaseCharacter = false;
                isValid = false;
                forenameField.setText("");
                break;
            }

            if (isValid && hasLetter && hasUpperCaseCharacter) {

                Toast.makeText(RegisterStudentActivity.this, "Forename " + foreNameText + " is valid ", Toast.LENGTH_SHORT).show();
                break;

            } else {

                Toast.makeText(RegisterStudentActivity.this, "Forename " + foreNameText + " is not valid. Please re-enter ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void validateSurname(String surnameText) { // Validates the Surname
        for (int i = 0; i < surnameText.length(); i++) {

            if (Character.isLetter(surnameText.charAt(i)) && Character.isUpperCase(surnameText.charAt(0))) {
                AlertDialog.Builder surnameDialogue = new AlertDialog.Builder(RegisterStudentActivity.this).setTitle("Surname Validation").setMessage("Surname Validated");
                surnameDialogue.setCancelable(true);
                surnameDialogue.show();

                hasUpperCaseCharacter = true; // Contains an upper case character
                hasLetter = true;
                surnameField.setText("");
                break;

            } else if (!Character.isUpperCase(surnameText.charAt(0))) {
                Toast.makeText(RegisterStudentActivity.this, "Invalid Surname", Toast.LENGTH_SHORT).show();
                hasUpperCaseCharacter = false;
                isValid = false;
                surnameField.setText(""); // Empty the field
                break;
            }

            if (isValid && hasLetter && hasUpperCaseCharacter) {
                Toast.makeText(RegisterStudentActivity.this, "Surname : " + surnameText + " is valid ", Toast.LENGTH_SHORT).show();
                break;

            } else {

                Toast.makeText(RegisterStudentActivity.this, "Surname " + surnameText + " is invalid", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void validateEmail(final String emailText) {

        Pattern regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]"); // Regex patterns

        for (int i = 0; i < emailText.length(); i++) {

            if (Character.isLetter(emailText.charAt(i)) && !Character.isDigit(emailText.charAt(i)) && !Character.isLetter(emailText.charAt(0))) { // If the character
                AlertDialog.Builder emailAlert = new AlertDialog.Builder(RegisterStudentActivity.this).setTitle("E-mail Warning").setMessage("E-mail Invalid").setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        emailField.setText("");
                    }
                });

                emailAlert.setCancelable(true);
                emailAlert.show();
                break;
            }

           else if (!regex.matcher(emailText).find()) {
                AlertDialog.Builder regexMatcher = new AlertDialog.Builder(RegisterStudentActivity.this).setTitle("@ Symbol").setMessage("E-mail should contain @ Symbol and should start with an upper case letter").setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel(); // Cancel the dialog
                        emailField.setText(""); // Empty out the input field
                    }
                });

                regexMatcher.setCancelable(true);
                regexMatcher.show(); // Show the dialogue
                break;
            }

            else if (regex.matcher(emailText).find()) { // If the email does not contain any of the regex characters
                AlertDialog.Builder atSymbolDialogue = new AlertDialog.Builder(RegisterStudentActivity.this).setTitle("@ Symbol Validation").setMessage("E-mail contains @ symbol. Re-Enter");
                atSymbolDialogue.setCancelable(true);
                atSymbolDialogue.show();
                break;
            }

            else {
                Toast.makeText(RegisterStudentActivity.this, "E-mail Valid", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void validateDegreeOfStudy(String degreeText) { // Routine that validates the degree of study field
        degreeText = degreeOfStudy.getText().toString();
        String emptyString = "";

        for (int i = 0; i < degreeText.length(); i++) { // Loop over the text

            if (!Character.isLetter(degreeText.charAt(i)) && degreeText.matches(emptyString)) {
                AlertDialog.Builder degreeDialogue = new AlertDialog.Builder(RegisterStudentActivity.this).setTitle("Degree Validation").setMessage("Degree Input cannot be empty");
                degreeDialogue.setCancelable(true);

                degreeDialogue.show();
                hasLetter = true;
                isValid = false;
                degreeOfStudy.setText(emptyString);
                break;
            }

            if (Character.isLetter(degreeText.charAt(i))) {
                AlertDialog.Builder degreeSuccessDialogue = new AlertDialog.Builder(RegisterStudentActivity.this).setTitle("Degree Success Validation").setMessage("Degree Validated Success");
                degreeSuccessDialogue.setCancelable(true);
                degreeSuccessDialogue.show();

                hasLetter = true;
                isValid = true; // Is valid is now true
                degreeOfStudy.setText(emptyString); // Empty the field
                break;
            }

        }
    }

    public void createStudentStudyPlan(boolean hasLetter, boolean isValid) { // Routine to re-direct the flow to the create student study plan
        if (hasLetter && isValid) {
            Intent switchToStudyPlan = new Intent(RegisterStudentActivity.this, CreatePlanActivity.class);
            startActivity(switchToStudyPlan);
        }
      }
    }