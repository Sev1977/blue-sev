/*
 * blue-sev
 * Copyright (c) 2023 Cellnovo Ltd. - All rights reserved
 * DataExporter
 * 14/08/2023
 */

package android.personal.fixtures.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.tables.Clubs;
import android.personal.fixtures.database.tables.Competitions;
import android.personal.fixtures.database.tables.Fixtures;
import android.personal.fixtures.database.tables.Goals;
import android.personal.fixtures.database.tables.PlayerSeasons;
import android.personal.fixtures.database.tables.Players;
import android.personal.fixtures.database.tables.Seasons;
import android.personal.fixtures.database.tables.Statistics;

import java.lang.ref.WeakReference;

public class DataExporter extends AsyncTask<Void, Void, Void>
{
    private final WeakReference<Activity> mActivityWeakReference;
    private final Database mDatabase;

    private ProgressDialog mProgressDialog;

    public DataExporter(final Activity callingActivity)
    {
        mActivityWeakReference = new WeakReference<>(callingActivity);
        mDatabase = Database.getInstance(callingActivity.getApplicationContext());
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        showProgressDialog();
    }

    @Override
    protected Void doInBackground(final Void... voids)
    {
        // Go through each table and convert the contents to CSV strings.
        for (String tableName:Database.ALL_TABLE_NAMES)
        {
            Cursor records = mDatabase.getAllRecords(tableName, "");
            Json
        }
        // Create a CSV file and write the strings to it
        return null;
    }

    @Override
    protected void onPostExecute(final Void param)
    {
        super.onPostExecute(param);
        hideProgressDialog();
    }

    private void showProgressDialog()
    {
        mProgressDialog = new ProgressDialog(mActivityWeakReference.get());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
    }

    private void hideProgressDialog()
    {
        if (mProgressDialog != null)
        {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
