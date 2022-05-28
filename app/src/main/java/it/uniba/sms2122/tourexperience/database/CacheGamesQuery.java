package it.uniba.sms2122.tourexperience.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static it.uniba.sms2122.tourexperience.database.CacheGames.COLUMN_GAME_TYPE;
import static it.uniba.sms2122.tourexperience.database.CacheGames.COLUMN_NOME_OPERA;
import static it.uniba.sms2122.tourexperience.database.CacheGames.CACHE_GAMES_TABLE;

public class CacheGamesQuery {

    /**
     * Aggiunge un opera alla tabella, cioè aggiunge un gioco svolto.
     * @param db connessione al database già aperta.
     * @param nomeOpera nome dell'opera da controllare.
     * @param gameType enumerativo corrispondente al tipo di gioco.
     * @return true se la insert va a buon fine, false altrimenti.ti.
     */
    public boolean addOne(final SQLiteDatabase db, final String nomeOpera, final GameTypes gameType) {
        final ContentValues cv = new ContentValues();
        cv.put(COLUMN_NOME_OPERA, nomeOpera);
        cv.put(COLUMN_GAME_TYPE, gameType.toString());
        return db.insert(CACHE_GAMES_TABLE, null, cv) != -1;
    }

    /**
     * Ritorna true se il game esiste già nel db in base al suo nome, false altrimenti.
     * @param db connessione al database già aperta.
     * @param nomeOpera nome dell'opera da controllare.
     * @param gameType enumerativo corrispondente al tipo di gioco.
     * @return true se il game esiste già nel db in base al suo nome, false altrimenti.
     */
    public boolean exists(final SQLiteDatabase db, final String nomeOpera, final GameTypes gameType) {
        final String findQuery = "SELECT * FROM " + CACHE_GAMES_TABLE +
                " WHERE " + COLUMN_NOME_OPERA + "='"+nomeOpera+"' AND " +
                COLUMN_GAME_TYPE + "='"+gameType.toString()+"'";
        final Cursor cursor = db.rawQuery(findQuery, null);
        final boolean result =  cursor.moveToFirst();
        cursor.close();
        return result;
    }

    /**
     * Elimina tutto il contenuto della tabella.
     * @param db connessione al database già aperta.
     * @return true se l'eliminazione è andata a buon fine, false altrimenti.
     */
    public boolean deleteAll(final SQLiteDatabase db) {
        return db.delete(CACHE_GAMES_TABLE, "1", null) != 0;
    }

}
