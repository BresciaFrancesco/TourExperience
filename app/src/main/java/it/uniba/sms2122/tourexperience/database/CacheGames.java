package it.uniba.sms2122.tourexperience.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class CacheGames {
    public final static String CACHE_GAMES_TABLE = "CACHE_GAMES";
    public final static String COLUMN_NOME_OPERA = "NOME_OPERA";
    public final static String COLUMN_GAME_TYPE = "GAME_TYPE";

    private final DBHelper dbHelper;
    private final CacheGamesQuery query;

    public CacheGames(final Context context) {
        dbHelper = new DBHelper(context);
        query = new CacheGamesQuery();
    }

    /**
     * Aggiunge un opera alla tabella, cioè aggiunge un gioco svolto.
     * @param nomeOpera nome dell'opera da aggiungere.
     * @param gameType enumerativo corrispondente al tipo di gioco.
     * @return true se la insert va a buon fine, false altrimenti.
     */
    public boolean addOne(final String nomeOpera, final GameTypes gameType) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final boolean result = query.addOne(db, nomeOpera, gameType);
        db.close();
        return result;
    }

    /**
     * Ritorna true se il game esiste già nel db in base al suo nome, false altrimenti.
     * @param nomeOpera nome dell'opera da controllare.
     * @param gameType enumerativo corrispondente al tipo di gioco.
     * @return true se il game esiste già nel db in base al suo nome, false altrimenti.
     */
    public boolean exists(final String nomeOpera, final GameTypes gameType) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        final boolean result = query.exists(db, nomeOpera, gameType);
        db.close();
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
