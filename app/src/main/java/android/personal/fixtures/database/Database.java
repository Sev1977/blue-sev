package android.personal.fixtures.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.personal.fixtures.database.tables.Clubs;
import android.personal.fixtures.database.tables.Competitions;
import android.personal.fixtures.database.tables.Fixtures;
import android.personal.fixtures.database.tables.Goals;
import android.personal.fixtures.database.tables.PlayerSeasons;
import android.personal.fixtures.database.tables.Players;
import android.personal.fixtures.database.tables.Seasons;
import android.personal.fixtures.database.tables.Statistics;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class Database extends SQLiteOpenHelper
{
    private static final String TAG = Database.class.getSimpleName();

    /*
     * Version 6: 5-Apr-2020, added Player.IsCurrent
     */
    private static final int DATABASE_VERSION = 6;

    private static final String DATABASE_NAME = "fixtures_db";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS %s";

    private static final AtomicInteger openCounter = new AtomicInteger();

    private static Database instance;

    private static final String[] ALL_TABLE_NAMES =
            new String[]{Clubs.TABLE_NAME, Competitions.TABLE_NAME, Fixtures.TABLE_NAME,
                    Goals.TABLE_NAME, Players.TABLE_NAME, PlayerSeasons.TABLE_NAME,
                    Seasons.TABLE_NAME, Statistics.TABLE_NAME};

    /**
     * Private constructor to prevent multiple instances
     *
     * @param context
     */
    private Database(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * @param c
     * @return
     */
    public static synchronized Database getInstance(final Context c)
    {
        if (instance == null)
        {
            instance = new Database(c.getApplicationContext());
        }
        openCounter.incrementAndGet();
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Clubs.createTable(db);
        Competitions.createTable(db);
        Fixtures.createTable(db);
        Goals.createTable(db);
        Players.createTable(db);
        PlayerSeasons.createTable(db);
        Seasons.createTable(db);
        Statistics.createTable(db);

        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i(TAG, "onUpgrade, oldVersion(" + oldVersion + ") newVersion(" + newVersion + ")");
        for (String tableName : ALL_TABLE_NAMES)
        {
            /*
             * Copy all the existing data into an array of ContentValues
             */
            final List<ContentValues> existingData = dumpExistingData(db, tableName);

            /*
             * Re-create the table
             */
            db.execSQL(String.format(DROP_TABLE, tableName));
            switch (tableName)
            {
                case Clubs.TABLE_NAME:
                    Clubs.createTable(db);
                    break;

                case Competitions.TABLE_NAME:
                    Competitions.createTable(db);
                    break;

                case Fixtures.TABLE_NAME:
                    Fixtures.createTable(db);
                    break;

                case Goals.TABLE_NAME:
                    Goals.createTable(db);
                    break;

                case Players.TABLE_NAME:
                    Players.createTable(db);
                    break;

                case PlayerSeasons.TABLE_NAME:
                    PlayerSeasons.createTable(db);
                    break;

                case Seasons.TABLE_NAME:
                    Seasons.createTable(db);
                    break;

                case Statistics.TABLE_NAME:
                    Statistics.createTable(db);
                    break;
            }

            /*
             * Re-populate the new table.
             */
            for (ContentValues oldValues : existingData)
            {
                db.insert(tableName, null, oldValues);
            }
        }
    }

    /**
     * @param database
     * @param tableName
     * @return
     */
    private List<ContentValues> dumpExistingData(final SQLiteDatabase database,
            final String tableName)
    {
        final List<ContentValues> existingData = new ArrayList<>();

        /*
         * Copy all the existing data into an array of ContentValues
         */
        final Cursor cursor = database.query(tableName, null, null, null, null, null, null);
        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                do
                {
                    final ContentValues rowValues = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cursor, rowValues);
                    existingData.add(rowValues);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return existingData;
    }

    /**
     * @param tableName
     * @param data
     * @return
     */
    public long addRecord(final String tableName, final ContentValues data)
    {
        return getWritableDatabase().insert(tableName, null, data);
    }

    /**
     * @param tableName
     * @param id
     * @return
     */
    public boolean updateRecord(final String tableName, final long id, final ContentValues values)
    {
        return getWritableDatabase().update(tableName, values, BaseColumns._ID + "=?",
                new String[]{String.valueOf(id)}) == 1;
    }

    /**
     * @param tableName
     * @param id
     * @return
     */
    public boolean deleteRecord(final String tableName, final long id)
    {
        return getWritableDatabase().delete(tableName, BaseColumns._ID + "=?",
                new String[]{String.valueOf(id)}) == 1;
    }

    /**
     * @param tableName
     * @param id
     * @return
     */
    public Cursor getFullRecordWithId(final String tableName, final long id)
    {
        final Cursor record = getReadableDatabase().query(tableName, null,
                BaseColumns._ID + "=" + id, null, null, null, null);
        if (record != null)
        {
            record.moveToFirst();
        }
        return record;
    }

    /**
     * @param tableName
     * @param sorting
     * @return
     */
    public Cursor getAllRecords(final String tableName, final String sorting)
    {
        final Cursor records = getReadableDatabase().query(tableName, null, null, null, null, null,
                sorting);
        if (records != null)
        {
            records.moveToFirst();
        }
        return records;
    }

    /**
     * @param tableName
     * @param columns
     * @param sorting
     * @return
     */
    public Cursor getColumnsForAllRecords(final String tableName, final String[] columns,
            final String sorting)
    {
        final Cursor records = getReadableDatabase().query(tableName, columns, null, null, null,
                null, sorting);
        if (records != null)
        {
            records.moveToFirst();
        }
        return records;
    }

    /**
     * @param tableName
     * @param selection
     * @param selectionArgs
     * @param sorting
     * @return
     */
    public Cursor getSelection(final String tableName, final String selection,
            final String[] selectionArgs, final String sorting)
    {
        final Cursor records = getReadableDatabase().query(tableName, null, selection,
                selectionArgs, null, null, sorting);
        if (records != null)
        {
            records.moveToFirst();
        }
        return records;
    }

    /**
     * @param tableName
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param sorting
     * @return
     */
    public Cursor getColumnsForSelection(final String tableName, final String[] columns,
            final String selection, final String[] selectionArgs, final String sorting)
    {
        final Cursor records = getReadableDatabase().query(tableName, columns, selection,
                selectionArgs, null, null, sorting);
        if (records != null)
        {
            records.moveToFirst();
        }
        return records;
    }
}
