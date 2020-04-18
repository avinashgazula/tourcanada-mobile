package com.dal.tourism;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;

public class ResetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ResetPasswordActivity";

    EditText input_code;
    EditText input_password;
    Button btn_resetPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        input_code = findViewById(R.id.input_code);
        input_password = findViewById(R.id.input_password);
        btn_resetPassword = findViewById(R.id.btn_resetPassword);





        btn_resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = getIntent().getStringExtra("userId");
                ForgotPasswordContinuation fpc = LoginActivity.getFPC();

                fpc.setVerificationCode(input_code.getText().toString());
                fpc.setPassword(input_password.getText().toString());
                fpc.continueTask();
            }
        });
    }



}
