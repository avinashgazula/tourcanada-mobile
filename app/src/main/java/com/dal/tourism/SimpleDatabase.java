package com.dal.tourism;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SimpleDatabase extends SQLiteOpenHelper {
    // declare require values
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "SimpleDB";
    private static final String TABLE_NAME = "SimpleTable";

    public SimpleDatabase(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    // declare table column names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CARDNUM="CardNumber";
    private static final String KEY_EXPIRY="Expirydate";
    private static final String KEY_CVV="cvv";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";





    // creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {
         String createDb = "CREATE TABLE "+TABLE_NAME+" ("+
                 KEY_ID+" INTEGER PRIMARY KEY,"+
                 KEY_TITLE+" TEXT,"+
                 KEY_CARDNUM+" TEXT,"+
                 KEY_EXPIRY+" TEXT,"+
                 KEY_CVV+" TEXT,"+
                 KEY_DATE+" TEXT,"+
                 KEY_TIME+" TEXT"
                 +" )";
         db.execSQL(createDb);
    }

    // upgrade db if older version exists
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion >= newVersion)
            return;

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public long addNote(Card card){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(KEY_TITLE, card.getTitle());
        v.put(KEY_CARDNUM, card.getCardnum());
        v.put(KEY_EXPIRY, card.getExpiry());
        v.put(KEY_CVV, card.getCvv());
        v.put(KEY_DATE, card.getDate());
        v.put(KEY_TIME, card.getTime());

        // inserting data into db
        long ID = db.insert(TABLE_NAME,null,v);
        return  ID;
    }

    public Card getNote(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] query = new String[] {KEY_ID,KEY_TITLE,KEY_CARDNUM,KEY_EXPIRY,KEY_CVV,KEY_DATE,KEY_TIME};
       Cursor cursor=  db.query(TABLE_NAME,query,KEY_ID+"=?",new String[]{String.valueOf(id)},null,null,null,null);
        if(cursor != null)
            cursor.moveToFirst();

        return new Card(
                Long.parseLong(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6));
    }

    public List<Card> getAllNotes(){
        List<Card> allCards = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME+" ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                Card card = new Card();
                card.setId(Long.parseLong(cursor.getString(0)));
                card.setTitle(cursor.getString(1));
                card.setCardnum(cursor.getString(2));
                card.setExpiry(cursor.getString(3));
                card.setCvv(cursor.getString(4));
                card.setDate(cursor.getString(5));
                card.setTime(cursor.getString(6));
                allCards.add(card);
            }while (cursor.moveToNext());
        }

        return allCards;

    }

    public int editNote(Card card){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        Log.d("Edited", "Edited Title: -> "+ card.getTitle() + "\n ID -> "+ card.getId());
        c.put(KEY_TITLE, card.getTitle());
        c.put(KEY_CARDNUM, card.getCardnum());
        c.put(KEY_EXPIRY, card.getExpiry());
        c.put(KEY_CVV, card.getCvv());
        c.put(KEY_DATE, card.getDate());
        c.put(KEY_TIME, card.getTime());
        return db.update(TABLE_NAME,c,KEY_ID+"=?",new String[]{String.valueOf(card.getId())});
    }



    void deleteNote(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,KEY_ID+"=?",new String[]{String.valueOf(id)});
        db.close();
    }





}
