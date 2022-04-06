package it.uniba.sms2122.tourexperience;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DB_PATH = "/data/user/0/it.uniba.sms2122.tourexperience/databases/";
    private static final String DB_NAME = "database.db";
    private final Context context;
    SQLiteDatabase myDatabase;

    // Costruttore
    public DataBaseHelper(@Nullable Context context) {
        super(context, "database.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // Controlla se il database esiste già
    private boolean checkDataBase() {
        try {
            final String path = DB_PATH + DB_NAME;
            final File file = new File(path);
            return file.exists();
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void copyDataBase() throws IOException{
        // Il database si trova nella cartella "assets"
        InputStream inputStream = context.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream outputStream = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while((length = inputStream.read(buffer)) >0) {
            outputStream.write(buffer,0,length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    public void createDataBase() {
        boolean databaseExist = checkDataBase();
        if(!databaseExist) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error("Error copying database...");
            } finally {
                this.close();
            }
        }
    }

    @Override
    public synchronized void close() {
        if(myDatabase != null)
            myDatabase.close();
        SQLiteDatabase.releaseMemory();
        super.close();
    }

    public String loadHandler() {
        createDataBase();
        StringBuilder result = new StringBuilder();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from museo;",null);
        while(cursor.moveToNext()) {
            String resultDescription = cursor.getString(3);
            result.append(resultDescription);
        }
        cursor.close();
        db.close();

        return result.toString();
    }
}
