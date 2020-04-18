package com.dal.tourism;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";

    int splashTime = 1000;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);



        GetDetailsHandler getDetailsHandler = new GetDetailsHandler() {
            @Override
            public void onSuccess(CognitoUserDetails cognitoUserDetails) {
                // The user detail are in cognitoUserDetails
                Log.d(TAG, "onSuccess: user details "+cognitoUserDetails);
                handler = new Handler();

                handler.postDelayed(runnable, splashTime);

            }

            @Override
            public void onFailure(Exception exception) {
                // Fetch user details failed, check exception for the cause
                Log.d(TAG, "onFailure: error "+exception);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        };



        CognitoSettings cognitoSettings = new CognitoSettings(SplashScreenActivity.this);
        cognitoSettings.getUserPool().getCurrentUser().getDetailsInBackground(getDetailsHandler);

    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(getApplicationContext(), ViewLocationsActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    };
}
