package it.uniba.sms2122.tourexperience.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static it.uniba.sms2122.tourexperience.database.GameTypes.*;
import static it.uniba.sms2122.tourexperience.database.CacheGames.CACHE_GAMES_TABLE;
import static it.uniba.sms2122.tourexperience.database.CacheGames.COLUMN_NOME_OPERA;
import static it.uniba.sms2122.tourexperience.database.CacheGames.COLUMN_GAME_TYPE;

import static it.uniba.sms2122.tourexperience.database.CacheScoreGames.CACHE_SCORE_TABLE;
import static it.uniba.sms2122.tourexperience.database.CacheScoreGames.COLUMN_SCORE;

import androidx.annotation.Nullable;

import java.util.LinkedList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "tourexperience.db";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    // Chiamato la prima volta che si accede ad un database.
    // Ci deve essere del codice all'interno per creare il database.
    @Override
    public void onCreate(SQLiteDatabase db) {
        final List<String> createTableList = new LinkedList<>();
        createTableList.add("CREATE TABLE " + CACHE_GAMES_TABLE + " (" +
                COLUMN_NOME_OPERA + " TEXT NOT NULL," +
                COLUMN_GAME_TYPE + " TEXT NOT NULL CHECK( "+COLUMN_GAME_TYPE+" IN ('"+ QUIZ +"', '"+DIFF+"') )," +
                "PRIMARY KEY (" + COLUMN_NOME_OPERA + "," + COLUMN_GAME_TYPE + ") )");
        createTableList.add("CREATE TABLE " + CACHE_SCORE_TABLE + " (" +
                COLUMN_GAME_TYPE + " TEXT NOT NULL CHECK( "+COLUMN_GAME_TYPE+" IN ('"+QUIZ+"', '"+DIFF+"') ) PRIMARY KEY," +
                COLUMN_SCORE + " INTEGER NOT NULL )");

        for (String s : createTableList) {
            db.execSQL(s);
        }
    }

    // Chiamato quando il numero di versione del database cambia.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CACHE_GAMES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CACHE_SCORE_TABLE);

        onCreate(db);
    }

}
