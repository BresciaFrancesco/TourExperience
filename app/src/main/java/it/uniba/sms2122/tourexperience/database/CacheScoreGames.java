package it.uniba.sms2122.tourexperience.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CacheScoreGames {
    public final static String CACHE_SCORE_TABLE = "CACHE_SCORE";
    public final static String COLUMN_UID = "UID";
    public final static String COLUMN_GAME_TYPE = "GAME_TYPE";
    public final static String COLUMN_SCORE = "SCORE";

    private final DBHelper dbHelper;
    private final CacheScoreGamesQuery query;

    public CacheScoreGames(final Context context) {
        dbHelper = new DBHelper(context);
        query = new CacheScoreGamesQuery();
    }

    /**
     * Aggiunge lo score di un game se quel game non era già presente, altrimenti
     * lo aggiorna aggiungendo lo score nuovo a quello precedente.
     * @param uid user id alla quale associare questo score.
     * @param gameType tipo di game.
     * @param score score da inserire o aggiungere.
     * @return true o false.
     */
    public boolean saveOne(final String uid, final GameTypes gameType, final int score) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final boolean result = query.saveOne(db, uid, gameType, score);
        db.close();
        return result;
    }

    /**
     * Ritorna lo score di un game.
     * @param uid user id alla quale associare questo score.
     * @param gameType tipo di game.
     * @return lo score del game;
     * @throws IllegalArgumentException se lo score non esiste o è minore di 0.
     */
    public int getScore(final String uid, final GameTypes gameType) throws IllegalArgumentException {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        final int result = query.getScore(db, gameType, uid);
        db.close();
        if (result < 0)
            throw new IllegalArgumentException("CacheScoreGames.getScore ha ritornato -1");
        return result;
    }

    /**
     * Elimina tutto il contenuto della tabella.
     * @return true se l'eliminazione è andata a buon fine, false altrimenti.
     */
    public boolean deleteAll() {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final boolean result = query.deleteAll(db);
        db.close();
        return result;
    }
}
