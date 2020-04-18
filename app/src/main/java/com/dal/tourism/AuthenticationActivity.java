package com.dal.tourism;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;


public class AuthenticationActivity extends AppCompatActivity {

    private static final String TAG = "AuthenticationActivity";

    private FirebaseAuth mAuth;

    private String authenticationCode;
    Button btn_submitCode;
    EditText input_code;

    private ProgressDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        btn_submitCode = findViewById(R.id.btn_submitCode);
        input_code = findViewById(R.id.input_code);

        Log.d(TAG, "onCreate: point2");

//        finish();

        btn_submitCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(input_code.getText().toString().isEmpty()){
                    input_code.requestFocus();
                    input_code.setError("Enter verification code");
                }

                showWaitDialog("Logging in..");
                String code = input_code.getText().toString();
                if (code == ""){
                    input_code.setError("Enter Authentication Code");
                }else{
                    System.out.println("input code: "+code);
                }
                MultiFactorAuthenticationContinuation mfac = LoginActivity.getMFAC();
                mfac.setMfaCode(code);
                mfac.continueTask();
            }
        });
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


//public class AuthenticationActivity extends AppCompatActivity {
//
//    private FirebaseAuth mAuth;
//
//    private String authenticationCode;
//    Button btn_submitCode;
//    EditText input_code;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_authentication);
//
//        btn_submitCode = findViewById(R.id.btn_submitCode);
//        input_code = findViewById(R.id.input_code);
//
//        mAuth = FirebaseAuth.getInstance();
//
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        String email = user.getEmail();
//
//        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference ref = database.child(getString(R.string.database_name));
//
//        Query phoneQuery = ref.orderByChild("email").equalTo(email);
//        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
//                    User user = singleSnapshot.getValue(User.class);
//                    System.out.println("number "+ user.mobile_number);
//                    sendVerificationCode(user.mobile_number);
//
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.out.println("onCancelled" +  databaseError.toException());
//            }
//        });
//
//        btn_submitCode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String code = input_code.getText().toString();
//                if (code == ""){
//                    input_code.setError("Enter Authentication Code");
//                }else{
//                    System.out.println("input code: "+code);
//                    verifyCode(code);
//                }
//            }
//        });
//    }
//
//    private void verifyCode(String code){
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(authenticationCode, code);
//        mAuth.signInWithCredential(credential).addOnCompleteListener(
//                new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()){
//
//                            startActivity(new Intent(AuthenticationActivity.this, ViewLocationsActivity.class));
//                            finish();
//
//                        }else{
//                            System.out.println("Verification Failed"+ task.getException().toString());
//                            Toast verification_failed = Toast.makeText(getApplicationContext(), "Verification Failed"+ task.getException().toString(), Toast.LENGTH_LONG);
//                            verification_failed.show();
//                        }
//                    }
//                }
//        );
//
//    }
//
//
//    private void sendVerificationCode(String mobile_number){
//        System.out.println("in sendVerificationCode");
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                mobile_number,
//                60,
//                TimeUnit.SECONDS,
//                this,
//                mCallbacks
//        );
//    }
//
//    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
//            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//        @Override
//        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//            super.onCodeSent(s, forceResendingToken);
//
//            authenticationCode = s;
//        }
//
//        @Override
//        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//            String code = phoneAuthCredential.getSmsCode();
//            if (code!=null){
//                input_code.setText(code);
//                verifyCode(code);
//            }
//        }
//
//        @Override
//        public void onVerificationFailed(@NonNull FirebaseException e) {
//            System.out.println("Verification Failed! "+ e.toString());
//            Toast verification_failed = Toast.makeText(getApplicationContext(), "Verification Failed"+ e.toString(), Toast.LENGTH_LONG);
//            verification_failed.show();
//        }
//    };
//}
