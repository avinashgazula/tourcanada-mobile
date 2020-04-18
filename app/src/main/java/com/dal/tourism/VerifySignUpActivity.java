package com.dal.tourism;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoMfaSettings;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;

public class VerifySignUpActivity extends AppCompatActivity {

    private static final String TAG = "VerifySignUpActivity";

    EditText input_code;
    Button btn_submitCode;

    GenericHandler confirmationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_sign_up);

        input_code = findViewById(R.id.input_code);
        btn_submitCode = findViewById(R.id.btn_submitCode);

        confirmationCallback = new GenericHandler() {

            @Override
            public void onSuccess() {
                // User was successfully confirmed
                Log.d(TAG, "onSuccess: User was successfully confirmed");
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Exception exception) {
                // User confirmation failed. Check exception for the cause.
                Log.d(TAG, "onFailure: User confirmation failed."+exception);
            }
        };

        btn_submitCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(input_code.getText().toString().isEmpty()){
                    input_code.requestFocus();
                    input_code.setError("Enter verification code");
                }


                String code = input_code.getText().toString();
                if (code == ""){
                    input_code.setError("Enter Authentication Code");
                }else{
                    System.out.println("input code: "+code);
                    verifyCode(code);
                }
            }
        });
    }

    private void verifyCode(String code) {
        // This will cause confirmation to fail if the user attribute has been verified for another user in the same pool
        boolean forcedAliasCreation = false;

        CognitoSettings cognitoSettings = new CognitoSettings(VerifySignUpActivity.this);

        // Call API to confirm this user
        String userId = getIntent().getStringExtra("userId");
        CognitoUser cognitoUser = cognitoSettings.getUserPool().getUser(userId);

        cognitoUser.confirmSignUpInBackground(code, forcedAliasCreation, confirmationCallback);

    }
}
