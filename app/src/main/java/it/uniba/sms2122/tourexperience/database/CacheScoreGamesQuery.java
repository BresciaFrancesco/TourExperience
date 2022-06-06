package it.uniba.sms2122.tourexperience.database;

import static it.uniba.sms2122.tourexperience.database.CacheScoreGames.CACHE_SCORE_TABLE;
import static it.uniba.sms2122.tourexperience.database.CacheScoreGames.COLUMN_SCORE;
import static it.uniba.sms2122.tourexperience.database.CacheScoreGames.COLUMN_UID;
import static it.uniba.sms2122.tourexperience.database.CacheScoreGames.COLUMN_GAME_TYPE;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CacheScoreGamesQuery {

    /**
     * Aggiunge lo score di un game se quel game non era già presente, altrimenti
     * lo aggiorna sommando lo score nuovo a quello precedente.
     * @param db connessione al database già aperta.
     * @param uid user id alla quale associare questo score.
     * @param gameType tipo di game.
     * @param score score da inserire o aggiungere.
     * @return true o false.
     */
    public boolean saveOne(final SQLiteDatabase db, final String uid, final GameTypes gameType, final int score) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_UID, uid);
        cv.put(COLUMN_GAME_TYPE, gameType.toString());
        cv.put(COLUMN_SCORE, score);
        if (db.insert(CACHE_SCORE_TABLE, null, cv) == -1) {
            cv = new ContentValues();
            int currentScore = getScore(db, gameType, uid);
            cv.put(COLUMN_SCORE, currentScore + score);
            return db.update(CACHE_SCORE_TABLE, cv, COLUMN_GAME_TYPE+"=? AND "+COLUMN_UID+"=?", new String[]{gameType.toString(), uid}) > 0;
        }
        return true;
    }

    /**
     * Ritorna lo score di un game.
     * @param db connessione al database già aperta.
     * @param gameType tipo di game.
     * @param uid user id alla quale associare questo score.
     * @return lo score del game;
     * @throws IllegalArgumentException se lo score non esiste o è minore di 0.
     */
    public int getScore(final SQLiteDatabase db, final GameTypes gameType, final String uid) {
        final String query = "SELECT " + COLUMN_SCORE + " FROM " + CACHE_SCORE_TABLE +
            " WHERE " + COLUMN_GAME_TYPE + "='"+gameType.toString()+"' AND "+ COLUMN_UID + "='"+uid+"'" ;
        final Cursor cursor = db.rawQuery(query, null);
        int result = -1;
        if (cursor.moveToFirst()) {
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }

    /**
     * Elimina tutto il contenuto della tabella.
     * @param db connessione al database già aperta.
     * @return true se l'eliminazione è andata a buon fine, false altrimenti.
     */
    public boolean deleteAll(final SQLiteDatabase db) {
        return db.delete(CACHE_SCORE_TABLE, "1", null) != 0;
    }

    /**
     * Elimina un utente dal suo uid dalla tabella.
     * @param db connessione al database già aperta.
     * @param uid user id associato ai records da eliminare.
     * @return true se l'eliminazione è andata a buon fine, false altrimenti.
     */
    public boolean deleteByUid(final SQLiteDatabase db, final String uid) {
        return db.delete(CACHE_SCORE_TABLE, COLUMN_UID+"=?", new String[] {uid}) != 0;
    }

}
