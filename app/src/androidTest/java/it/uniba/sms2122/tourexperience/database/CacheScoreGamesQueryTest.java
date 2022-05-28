package it.uniba.sms2122.tourexperience.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CacheScoreGamesQueryTest {
    private static CacheScoreGamesQuery cacheScoreGames;
    private static SQLiteDatabase db;

    @BeforeClass
    public static void setup() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        cacheScoreGames = new CacheScoreGamesQuery();
        db = new DBHelper(appContext).getWritableDatabase();
        cacheScoreGames.deleteAll(db);
    }

    @Before
    public void setupForMethods() {
        db.beginTransaction();
    }


    @Test
    public void getScoreForQuizShouldReturn_MinusOne() throws Exception {
        assertEquals(cacheScoreGames.getScore(db, GameTypes.QUIZ), -1);
    }

    @Test
    public void getScoreForDiffShouldReturn_MinusOne() throws Exception {
        assertEquals(cacheScoreGames.getScore(db, GameTypes.DIFF), -1);
    }

    @Test
    public void addScoreShouldReturnTrue() throws Exception {
        assertTrue(cacheScoreGames.saveOne(db, GameTypes.DIFF, 10));
    }

    @Test
    public void getScoreShoudlReturn_10() throws Exception {
        final int score = 10;
        final GameTypes type = GameTypes.QUIZ;
        cacheScoreGames.saveOne(db, type, score);

        assertEquals(score, cacheScoreGames.getScore(db, type));
    }

    @Test
    public void updateShouldReturnTrue() throws Exception {
        final GameTypes type = GameTypes.QUIZ;
        cacheScoreGames.saveOne(db, type, 10);
        assertTrue(cacheScoreGames.saveOne(db, type, 11));
    }

    @Test
    public void getScoreAfterUpdateShouldReturn_11() throws Exception {
        final GameTypes type = GameTypes.DIFF;
        cacheScoreGames.saveOne(db, type, 10);
        cacheScoreGames.saveOne(db, type, 1);

        assertEquals(11, cacheScoreGames.getScore(db, type));
    }

    @Test
    public void deleteAllAfterMultipleInsertsSholdReturnTrue() throws Exception {
        cacheScoreGames.saveOne(db, GameTypes.QUIZ, 10);
        cacheScoreGames.saveOne(db, GameTypes.DIFF, 12);

        assertTrue(cacheScoreGames.deleteAll(db));
    }

    @Test
    public void getScoreAfterDeleteShouldReturnMinusOne() throws Exception {
        cacheScoreGames.saveOne(db, GameTypes.DIFF, 10);
        cacheScoreGames.saveOne(db, GameTypes.QUIZ, 10);
        cacheScoreGames.deleteAll(db);

        assertEquals(-1, cacheScoreGames.getScore(db, GameTypes.DIFF));
        assertEquals(-1, cacheScoreGames.getScore(db, GameTypes.QUIZ));
    }


    @After
    public void teardownForMethods() {
        db.endTransaction();
    }

    @AfterClass
    public static void teardown() {
        db.close();
    }

}
