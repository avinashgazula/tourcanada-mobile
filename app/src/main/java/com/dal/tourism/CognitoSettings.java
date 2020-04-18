package com.dal.tourism;

import android.content.Context;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.regions.Regions;

public class CognitoSettings {
//    private String userPoolId = "us-east-1_6C9xWATUV";
//    private String clientId = "5qpbl7f9aifqr2s3uc5nd5ve03";
//    private String clientSecret = "1bqd4ggeqgs6n939p11147p8d32bjted94hkhul488b229ecq176";


    private String userPoolId = "us-east-1_EDRvS0yWp";
    private String clientId = "5fa5lc0jovt00563bm2okr46p6";
    private String clientSecret = "atri6qj696fnu60eptub507s06gt9icpdffdej7je7lfltpaviu";

    private Regions cognitoRegions = Regions.US_EAST_1;

    private Context context;

    public String getUserPoolId() {
        return userPoolId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public Regions getCognitoRegions() {
        return cognitoRegions;
    }

    public CognitoSettings(Context context){
        this.context = context;

    }

    public CognitoUserPool getUserPool(){
        return new CognitoUserPool(context, userPoolId, clientId, clientSecret, cognitoRegions);
    }
}
