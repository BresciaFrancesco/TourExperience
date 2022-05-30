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
        assertEquals(cacheScoreGames.getScore(db, GameTypes.QUIZ, "abc123"), -1);
    }

    @Test
    public void getScoreForDiffShouldReturn_MinusOne() throws Exception {
        assertEquals(cacheScoreGames.getScore(db, GameTypes.DIFF, "abc123"), -1);
    }

    @Test
    public void addScoreShouldReturnTrue() throws Exception {
        assertTrue(cacheScoreGames.saveOne(db, "abc123", GameTypes.DIFF, 10));
    }

    @Test
    public void getScoreShoudlReturn_10() throws Exception {
        final int score = 10;
        final String uid = "abc123";
        final GameTypes type = GameTypes.QUIZ;
        cacheScoreGames.saveOne(db, uid, type, score);

        assertEquals(score, cacheScoreGames.getScore(db, type, uid));
    }

    @Test
    public void updateShouldReturnTrue() throws Exception {
        final GameTypes type = GameTypes.QUIZ;
        final String uid = "abc123";
        cacheScoreGames.saveOne(db, uid, type, 10);
        assertTrue(cacheScoreGames.saveOne(db, uid, type, 11));
    }

    @Test
    public void getScoreAfterUpdateShouldReturn_11() throws Exception {
        final GameTypes type = GameTypes.DIFF;
        final String uid = "abc123";
        cacheScoreGames.saveOne(db, uid, type, 10);
        cacheScoreGames.saveOne(db, uid, type, 1);

        assertEquals(11, cacheScoreGames.getScore(db, type, uid));
    }

    @Test
    public void deleteAllAfterMultipleInsertsSholdReturnTrue() throws Exception {
        cacheScoreGames.saveOne(db, "abc123", GameTypes.QUIZ, 10);
        cacheScoreGames.saveOne(db, "123abc", GameTypes.DIFF, 12);

        assertTrue(cacheScoreGames.deleteAll(db));
    }

    @Test
    public void getScoreAfterDeleteShouldReturnMinusOne() throws Exception {
        final String uid = "abc123";
        cacheScoreGames.saveOne(db, uid, GameTypes.DIFF, 10);
        cacheScoreGames.saveOne(db, uid, GameTypes.QUIZ, 10);
        cacheScoreGames.deleteAll(db);

        assertEquals(-1, cacheScoreGames.getScore(db, GameTypes.DIFF, uid));
        assertEquals(-1, cacheScoreGames.getScore(db, GameTypes.QUIZ, uid));
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
