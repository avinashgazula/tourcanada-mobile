package com.dal.tourism;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewDestinationsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = "ViewDestinationsActivity";

    private ArrayList<String> mDestinations = new ArrayList<>();
    private ArrayList<String> mDescriptions = new ArrayList<>();
    private ArrayList<String> mImages       = new ArrayList<>();
    private ArrayList<String> mResult       = new ArrayList<>();

    RecyclerView recyclerView;
    DestinationViewAdapter adapter;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);

        setTitle("Destinations");

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        getDestinations();
        initRecyclerView();



    }

    private void filter(String text) {

        ArrayList<String> fDestinations = new ArrayList<>();
        ArrayList<String> fDescriptions = new ArrayList<>();
        ArrayList<String> fImages = new ArrayList<>();

        for (int i=0; i<mDestinations.size(); i++){
            if (mDestinations.get(i).toLowerCase().contains(text.toLowerCase())){
                fDestinations.add(mDestinations.get(i));
                fDescriptions.add(mDescriptions.get(i));
                fImages.add(mImages.get(i));
            }
        }

        adapter.filterList(fDestinations, fDescriptions, fImages);
    }

    private void getDestinations() {
        try {
            String url = "http://flaskapi-env.eba-pj7c3myx.us-east-1.elasticbeanstalk.com/destinations?location=";
            String location = getIntent().getStringExtra("location");
            location = location.toLowerCase();
            url += URLEncoder.encode(location, "utf-8");
            Log.d(TAG, "getDestinations: url " + url);
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
            JSONArray result = ((JSONArray) myResponse.get("result"));
            Log.d(TAG, "getLocations: "+ result);
            for (int i=0; i<result.length(); i++) {
                mResult.add(result.getString(i));
                Map<String, Object> retMap = new Gson().fromJson(
                        result.getString(i), new TypeToken<HashMap<String, Object>>() {}.getType()
                );
                mDestinations.add(retMap.get("name").toString());
                mDescriptions.add(retMap.get("description").toString());
                mImages.add(retMap.get("photoURL").toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new DestinationViewAdapter(mDestinations, mDescriptions, mImages, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search Destinations");
        searchView.setOnQueryTextListener(this);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setFocusable(false);

        MenuItem signOutItem = menu.findItem(R.id.menu_item_sign_out);
        signOutItem.setVisible(false);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_item_sign_out){
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        }
                    });
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
}
