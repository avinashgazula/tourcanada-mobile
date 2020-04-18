package com.dal.tourism;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    static MultiFactorAuthenticationContinuation mfac;
    static ForgotPasswordContinuation fpc;

    EditText input_email;
    EditText input_password;
    TextView txt_createAccount;
    TextView txt_forgotPassword;
    Button btn_login;

    private ProgressDialog waitDialog;
    private AlertDialog userDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG);
        }
        else{
            connected = false;
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG);
        }

        Log.d(TAG, "onCreate: connection status: "+ connected);


        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        txt_createAccount = findViewById(R.id.txt_createAccount);
        txt_forgotPassword = findViewById(R.id.txt_forgotPassword);
        btn_login = findViewById(R.id.btn_login);

        txt_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        txt_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input_email.getText().toString().isEmpty()){
                    input_email.setError("Enter email");
                    input_email.requestFocus();
                }else{
                    resetPassword(input_email.getText().toString());
                    Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                    intent.putExtra("userId", input_email.getText().toString());
                    startActivity(intent);
                }
            }
        });


        final AuthenticationHandler authenticationHandler = new AuthenticationHandler() {


            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                closeWaitDialog();
                Log.d(TAG, "onSuccess: Login Successful");

                Intent intent = new Intent(LoginActivity.this, ViewLocationsActivity.class);
                startActivity(intent);
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                // The API needs user sign-in credentials to continue
                AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId,
                        input_password.getText().toString(), null);

                // Pass the user sign-in credentials to the continuation
                authenticationContinuation.setAuthenticationDetails(authenticationDetails);

                // Allow the sign-in to continue
                authenticationContinuation.continueTask();
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
                mfac = multiFactorAuthenticationContinuation;
                showWaitDialog("Sending 2FA code to "+mfac.getParameters().getDestination());

                Log.d(TAG, "getMFACode: mfac params "+mfac.getParameters().getDestination());
                new ProgressDialog(getApplicationContext()).dismiss();

                Log.d(TAG, "getMFACode: MFA Required");
                Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
                startActivity(intent);
//                MFA code is verified in AuthenticationActivity
            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
                Log.d(TAG, "authenticationChallenge: "+continuation);
            }

            @Override
            public void onFailure(Exception exception) {
                // Sign-in failed, check exception for the cause
                Log.d(TAG, "onFailure: sign-in failed "+exception);

                String message = exception.getMessage();
                int index = message.indexOf('(');
                message = message.substring(0, index);

                showDialogMessage("Error", message);
//                Toast.makeText(getApplicationContext(), exception.toString(), Toast.LENGTH_LONG).show();
            }
        };





        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(input_email.getText().toString().isEmpty()){
                    input_email.requestFocus();
                    input_email.setError("Enter email");
                    return;
                }
                if(input_password.getText().toString().isEmpty()){
                    input_password.requestFocus();
                    input_password.setError("Enter password");
                    return;
                }

                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }

                String email_str = input_email.getText().toString();

                CognitoSettings cognitoSettings = new CognitoSettings(LoginActivity.this);
                CognitoUser user = cognitoSettings.getUserPool().getUser(email_str);

                user.getSessionInBackground(authenticationHandler);



            }
        });
    }

    private void resetPassword(String userId) {

        CognitoSettings cognitoSettings = new CognitoSettings(getApplicationContext());
        CognitoUser user = cognitoSettings.getUserPool().getUser(userId);

        ForgotPasswordHandler forgotPasswordHandler = new ForgotPasswordHandler() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: Password Reset Success");
                Toast.makeText(getApplicationContext(), "Password Reset Successful. Login to continue", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));

            }

            @Override
            public void getResetCode(ForgotPasswordContinuation continuation) {
                Log.d(TAG, "getResetCode: Password Reset code sent");
                fpc = continuation;
//                continuation.setVerificationCode(input_code.getText().toString());
//                continuation.setPassword(input_password.getText().toString());
//                continuation.continueTask();

            }

            @Override
            public void onFailure(Exception exception) {
                Log.d(TAG, "onFailure: password reset failed "+ exception);
            }
        };


        user.forgotPassword(forgotPasswordHandler);


    }


    public static MultiFactorAuthenticationContinuation getMFAC(){
        return mfac;
    }

    public static ForgotPasswordContinuation getFPC(){
        return fpc;
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



//public class LoginActivity extends AppCompatActivity {
//
//    private FirebaseAuth mAuth;
//
//    EditText input_email;
//    EditText input_password;
//    TextView txt_createAccount;
//    TextView txt_forgotPassword;
//    Button btn_login;
//
//
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null) {
//            // User is signed in
//            startActivity(new Intent(LoginActivity.this, ViewLocationsActivity.class));
//            finish();
//        }
//
//        input_email = findViewById(R.id.input_email);
//        input_password = findViewById(R.id.input_password);
//        txt_createAccount = findViewById(R.id.txt_createAccount);
//        txt_forgotPassword = findViewById(R.id.txt_forgotPassword);
//        btn_login = findViewById(R.id.btn_login);
//
//        txt_createAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
//            }
//        });
//
//        txt_forgotPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (input_email.getText().toString().isEmpty()){
//                    input_email.setError("Enter email");
//                    input_email.requestFocus();
//                }else{
//                    mAuth.sendPasswordResetEmail(input_email.getText().toString())
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        System.out.println("Email sent.");
//                                        Toast email_sent = Toast.makeText(getApplicationContext(), "Password reset email sent", Toast.LENGTH_LONG);
//                                        email_sent.show();
//                                    }
//                                }
//                            });
//                }
//            }
//        });
//
//        btn_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String email_str = input_email.getText().toString();
//                String password_str = input_password.getText().toString();
//                mAuth.signInWithEmailAndPassword(email_str, password_str)
//                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()){
//                                    Toast login_successful = Toast.makeText(getApplicationContext(), "Login Successful: " , Toast.LENGTH_LONG);
//                                    login_successful.show();
//                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                                    if (user != null) {
//                                        // Name, email address, and profile photo Url
//                                        String name = user.getDisplayName();
//                                        String email = user.getEmail();
//                                        Uri photoUrl = user.getPhotoUrl();
//
//                                        // Check if user's email is verified
//                                        boolean emailVerified = user.isEmailVerified();
//
//
//                                        System.out.println(name + email + String.valueOf(photoUrl) + String.valueOf(emailVerified));
//
//                                        startActivity(new Intent(LoginActivity.this, AuthenticationActivity.class));
//                                        finish();
//                                    }
//
//                                }
//                                else{
//                                    Toast login_unsuccessful = Toast.makeText(getApplicationContext(), "Login Unsuccessful: " + task.getException(), Toast.LENGTH_LONG);
//                                    login_unsuccessful.show();
//                                }
//                            }
//                        });
//            }
//        });
//    }
//
//}
