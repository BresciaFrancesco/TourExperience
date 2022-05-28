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
public class CacheGamesQueryTest {
    private static CacheGamesQuery cacheGames;
    private static SQLiteDatabase db;

    @BeforeClass
    public static void setup() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        cacheGames = new CacheGamesQuery();
        db = new DBHelper(appContext).getWritableDatabase();
        cacheGames.deleteAll(db);
    }

    @Before
    public void setupForMethods() {
        db.beginTransaction();
    }


    @Test
    public void existsQuizShouldBeFalse() throws Exception {
        assertFalse(cacheGames.exists(db,"test", GameTypes.QUIZ));
    }

    @Test
    public void addTestQuizAndExistsTestShouldBeTrue() throws Exception {
        final String test = "test";
        assertTrue("addOne", cacheGames.addOne(db, test, GameTypes.QUIZ));
        assertTrue("exists", cacheGames.exists(db, test, GameTypes.QUIZ));
    }

    @Test
    public void firstExistsShouldBeTrue_secondExistsShouldBeFalse_afterInsertAndDeleteAll() throws Exception {
        String test_1 = "test";
        String test_2 = "test_2";
        cacheGames.addOne(db, test_1, GameTypes.QUIZ);
        cacheGames.addOne(db, test_2, GameTypes.DIFF);
        cacheGames.addOne(db, test_1, GameTypes.DIFF);

        assertTrue("exists before", cacheGames.exists(db, test_1, GameTypes.QUIZ));
        assertTrue("exists before", cacheGames.exists(db, test_2, GameTypes.DIFF));
        assertTrue("exists before", cacheGames.exists(db, test_1, GameTypes.DIFF));

        cacheGames.deleteAll(db);

        assertFalse("exists after", cacheGames.exists(db, test_1, GameTypes.QUIZ));
        assertFalse("exists after", cacheGames.exists(db, test_2, GameTypes.DIFF));
        assertFalse("exists after", cacheGames.exists(db, test_1, GameTypes.DIFF));
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
