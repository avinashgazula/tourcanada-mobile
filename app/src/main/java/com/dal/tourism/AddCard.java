package com.dal.tourism;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

public class AddCard extends AppCompatActivity {
    Toolbar toolbar;
    EditText noteTitle, cardnum, expiry, cvv;
    Calendar c;
    String todaysDate;
    String currentTime;
    ImageView imageicon;
    Button btn_buy_tickets;
    TextView card_preview_name;
    TextView card_preview_number;
    TextView card_preview_expiry;
    int flag = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add card");

        cardnum = findViewById(R.id.CardNumber);
        expiry = findViewById(R.id.Expirydate);
        cvv = findViewById(R.id.cvv);
        noteTitle = findViewById(R.id.noteTitle);
        imageicon = findViewById(R.id.cardicon);
        card_preview_name = findViewById(R.id.card_preview_name);
        card_preview_number = findViewById(R.id.card_preview_number);
        card_preview_expiry = findViewById(R.id.card_preview_expiry);
        btn_buy_tickets = findViewById(R.id.btn_buy_tickets);

        final String name = getIntent().getStringExtra("name");
        final String email = getIntent().getStringExtra("email");
        final String phone_number = getIntent().getStringExtra("phone_number");
        final String destinationName = getIntent().getStringExtra("destinationName");


        //btn_buy_tickets.setEnabled(false);



        noteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    getSupportActionBar().setTitle(s);
                    card_preview_name.setText(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cardnum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkRequiredFields();
                if (s.length() != 0) {
                    if (s.charAt(0) == 1 || s.charAt(0) == 4) {
                        imageicon.setImageResource(R.drawable.visa);
                    }
                    card_preview_number.setText(s);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        expiry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkRequiredFields();
                if(charSequence.length() != 0){
                    card_preview_expiry.setText(charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // set current date and time
        c = Calendar.getInstance();
        todaysDate = c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH);
        Log.d("DATE", "Date: " + todaysDate);
        currentTime = pad(c.get(Calendar.HOUR)) + ":" + pad(c.get(Calendar.MINUTE));
        Log.d("TIME", "Time: " + currentTime);


        btn_buy_tickets.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View view){

                flag = 0;

                if(cardnum.getText().toString().isEmpty()){
                    cardnum.requestFocus();
                    cardnum.setError("Enter a valid card number");
                    flag = 1;
                }
                if(expiry.getText().toString().isEmpty()){
                    expiry.requestFocus();
                    expiry.setError("Enter the expiry date");
                    flag = 1;
                }
                if(!expiry.getText().toString().contains("/")){
                    expiry.requestFocus();
                    expiry.setError("Invalid date format");
                }
                if(cvv.getText().toString().isEmpty()){
                    cvv.requestFocus();
                    cvv.setError("Enter CVV");
                    flag = 1;
                }

               if(flag==0)
                {
                    Intent intent = new Intent(AddCard.this, TicketConfirmationActivity.class);
                    intent.putExtra("destinationName", destinationName);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    intent.putExtra("phone_number", phone_number);
                    startActivity(intent);
                }
            }
        });

    }

    public void checkRequiredFields() {
        if (!cardnum.getText().toString().isEmpty() && !expiry.getText().toString().isEmpty()) {
           // btn_buy_tickets.setEnabled(true);
            flag = 1;
        } else {
            flag = 0;
            //btn_buy_tickets.setEnabled(false);
        }
    }

    private String pad(int time) {
        if (time < 10)
            return "0" + time;
        return String.valueOf(time);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            if (noteTitle.getText().length() != 0) {
                Card card = new Card(noteTitle.getText().toString(), cardnum.getText().toString(), expiry.getText().toString(), cvv.getText().toString(), todaysDate, currentTime);
                SimpleDatabase sDB = new SimpleDatabase(this);
                long id = sDB.addNote(card);
                Card check = sDB.getNote(id);
                Log.d("inserted", "card: " + id + " -> Title:" + check.getTitle() + " Date: " + check.getDate());
                onBackPressed();

                Toast.makeText(this, "Card details added successfully", Toast.LENGTH_LONG).show();
            } else {
                noteTitle.setError("Title Can not be Blank.");
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }









}





