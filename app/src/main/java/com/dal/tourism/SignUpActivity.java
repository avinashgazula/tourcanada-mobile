package com.dal.tourism;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    EditText name;
    EditText username;
    EditText email;
    EditText mobile_number;
    EditText password1;
    EditText password2;
    Button btn_signUp;
    TextView txt_login;

    private ProgressDialog waitDialog;
    private AlertDialog userDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);



        name = findViewById(R.id.input_name);
        username = findViewById(R.id.input_username);
        email = findViewById(R.id.input_email);
        mobile_number = findViewById(R.id.input_mobile);
        password1 = findViewById(R.id.input_password);
        password2 = findViewById(R.id.input_reEnterPassword);
        txt_login = findViewById(R.id.txt_login);
        btn_signUp = findViewById(R.id.btn_signup);


        final CognitoUserAttributes userAttributes = new CognitoUserAttributes();
        final SignUpHandler signUpHandler = new SignUpHandler() {

            @Override
            public void onSuccess(CognitoUser user, SignUpResult signUpResult) {
                showWaitDialog("Sending verification code to "+signUpResult.getCodeDeliveryDetails().getDestination());
                Log.d(TAG, "onSuccess: Sign Up successful "+signUpResult.getUserConfirmed());
                if(!signUpResult.getUserConfirmed()){
                    Log.d(TAG, "onSuccess: verification code sent to "+signUpResult.getCodeDeliveryDetails().getDestination());
                    Toast.makeText(getApplicationContext(), "verification code sent to "+signUpResult.getCodeDeliveryDetails().getDestination(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), VerifySignUpActivity.class);
                    intent.putExtra("userId", user.getUserId());
                    startActivity(intent);
                }else{
                    Log.d(TAG, "onSuccess: Account verified");
                }
            }

            @Override
            public void onFailure(Exception exception) {
                Log.d(TAG, "onFailure: sign up failed"+exception);

                String message = exception.getMessage();
                int index = message.indexOf('(');
                message = message.substring(0, index);

                showDialogMessage("Error", message);
            }
        };


        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().isEmpty()){
                    name.requestFocus();
                    name.setError("Enter name");
                    return;
                }
                if(username.getText().toString().isEmpty()){
                    username.requestFocus();
                    username.setError("Enter username");
                    return;
                }
                if(email.getText().toString().isEmpty()){
                    email.requestFocus();
                    email.setError("Enter email");
                    return;
                }
                if(mobile_number.getText().toString().isEmpty()){
                    mobile_number.requestFocus();
                    mobile_number.setError("Enter mobile number");
                    return;
                }


                if (password1.getText().toString().equals(password2.getText().toString())){
                    final String username_str = username.getText().toString();
                    final String name_str = name.getText().toString();
                    final String email_str = email.getText().toString();
                    String password_str = password1.getText().toString();
                    final String mobile_number_str = "+1" + mobile_number.getText().toString();

                    if (email_str.isEmpty()){
                        email.setError("Enter your email");
                        email.requestFocus();
                    }
                    if (password_str.isEmpty()){
                        password1.setError("Enter your password");
                        password1.requestFocus();
                    }

                    if (!(email_str.isEmpty() || password_str.isEmpty())){

                        userAttributes.addAttribute("name", name_str);
                        userAttributes.addAttribute("phone_number", mobile_number_str);
//                        userAttributes.addAttribute("preferred_username", username_str);
                        userAttributes.addAttribute("email", email_str);

                        CognitoSettings cognitoSettings = new CognitoSettings(SignUpActivity.this);
                        cognitoSettings.getUserPool().signUpInBackground(username_str, password_str, userAttributes, null, signUpHandler);


                    }

                }
                else{
                    showDialogMessage("Error", "Password Mismatch");
                }
            }
        });
    }

    private void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                } catch (Exception e) {
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void showWaitDialog(String message) {
        closeWaitDialog();
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle(message);
        waitDialog.show();
    }

    private void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        }
        catch (Exception e) {
            //
        }
    }
}



//public class SignUpActivity extends AppCompatActivity {
//
//    private FirebaseAuth mAuth;
//    private DatabaseReference mDatabase;
//
//    EditText name;
//    EditText email;
//    EditText mobile_number;
//    EditText password1;
//    EditText password2;
//    Button btn_signUp;
//    TextView txt_login;
//
//
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_signup);
//
//
//        mAuth = FirebaseAuth.getInstance();
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//
//        name = findViewById(R.id.input_name);
//        email = findViewById(R.id.input_email);
//        mobile_number = findViewById(R.id.input_mobile);
//        password1 = findViewById(R.id.input_password);
//        password2 = findViewById(R.id.input_reEnterPassword);
//        txt_login = findViewById(R.id.txt_login);
//
//        btn_signUp = findViewById(R.id.btn_signup);
//
//        txt_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
//                finish();
//            }
//        });
//
//        btn_signUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (password1.getText().toString().equals(password2.getText().toString())){
//                    final String name_str = name.getText().toString();
//                    final String email_str = email.getText().toString();
//                    String password_str = password1.getText().toString();
//                    final String mobile_number_str = mobile_number.getText().toString();
//
//                    if (email_str.isEmpty()){
//                        email.setError("Enter your email");
//                        email.requestFocus();
//                    }
//                    if (password_str.isEmpty()){
//                        password1.setError("Enter your password");
//                        password1.requestFocus();
//                    }
//
//                    if (!(email_str.isEmpty() || password_str.isEmpty())){
//
//                        mAuth.createUserWithEmailAndPassword(email_str, password_str).
//                                addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (!task.isSuccessful()) {
//                                    Toast.makeText(SignUpActivity.this.getApplicationContext(),
//                                            "SignUp unsuccessful: " + task.getException().getMessage(),
//                                            Toast.LENGTH_SHORT).show();
//                                } else {
//                                    System.out.println("User created");
//                                    String userId = mAuth.getCurrentUser().getUid();
//                                    System.out.println("userId: " + userId);
//                                    User user = new User(name_str, mobile_number_str, email_str);
//
//                                    mDatabase.child("tourism-cloud5409").child(userId).setValue(user);
//
//                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
//                                    firebaseUser.sendEmailVerification();
//
//
//
//                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                                            .setDisplayName(name_str)
//                                            .build();
//
//                                    firebaseUser.updateProfile(profileUpdates)
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (task.isSuccessful()) {
//                                                        System.out.println("User profile updated.");
//                                                    }
//                                                }
//                                            });
//
//                                    Toast user_created = Toast.makeText(getApplicationContext(), "User Created", Toast.LENGTH_LONG);
//                                    user_created.show();
//                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
//                                }
//                            }
//                        });
//
//
//                    }
//
//                }
//                else{
//                    Toast password_mismatch = Toast.makeText(getApplicationContext(), "Password mismatch", Toast.LENGTH_LONG);
//                    password_mismatch.show();
//                }
//            }
//        });
//    }
//}
