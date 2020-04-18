package com.dal.tourism;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class ViewLocationsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private static final String TAG = "ViewLocationsActivity";

    private ArrayList<String>  mLocations = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();

    RecyclerView recyclerView;
    LocationViewAdapter adapter;

    private ProgressDialog waitDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);
        setTitle("Locations");

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        getLocations();
        initRecyclerView();
    }

    private void filter(String text) {

        ArrayList<String> fLocations = new ArrayList<>();
        ArrayList<String> fImages = new ArrayList<>();

        for (int i=0; i<mLocations.size(); i++){
            if (mLocations.get(i).toLowerCase().contains(text.toLowerCase())){
                fLocations.add(mLocations.get(i));
                fImages.add(mImages.get(i));
            }
        }
        adapter.filterList(fLocations, fImages);
    }

    public void getLocations(){
        try {
            String url = "http://flaskapi-env.eba-pj7c3myx.us-east-1.elasticbeanstalk.com/locations/";
            URL locationURL = new URL(url);
            HttpURLConnection con = (HttpURLConnection) locationURL.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            JSONObject myResponse = new JSONObject(content.toString());
            JSONArray locations = ((JSONArray) myResponse.get("locations"));
            Log.d(TAG, "getLocations: "+ locations);
            for (int i=0; i<locations.length(); i++) {
                mLocations.add(locations.getString(i));
            }
            JSONArray images = (JSONArray) myResponse.get("images");
            for(int i=0; i<images.length(); i++){
                mImages.add(images.getString(i));
            }
//            Collections.sort(mLocations);
            Log.d(TAG, "getLocations: mLocations" + mLocations);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new LocationViewAdapter(mLocations, mImages,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search Locations");
        searchView.setOnQueryTextListener(this);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setFocusable(false);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        final CognitoSettings cognitoSettings = new CognitoSettings(ViewLocationsActivity.this);

        if (id == R.id.menu_item_sign_out){
            showWaitDialog("Signing out..");
            GenericHandler handler = new GenericHandler() {

                @Override
                public void onSuccess() {
//                    closeWaitDialog();
                    Log.d(TAG, "onSuccess: Logout successful"+ cognitoSettings.getUserPool().getCurrentUser().getUserId());
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }

                @Override
                public void onFailure(Exception e) {
                }
            };

            CognitoUser user = cognitoSettings.getUserPool().getCurrentUser();
            user.globalSignOutInBackground(handler);


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        filter(s);
        return true;
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
