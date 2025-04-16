package android.personal.fixtures.utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.personal.fixtures.database.Database;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;

public class DataImporter extends AsyncTask<Void, Void, Void>
{
    private final String TAG = DataImporter.class.getSimpleName();
    private final WeakReference<Activity> mActivityWeakReference;
    private final String mDirectoryName;
    private final Database mDatabase;

    public DataImporter(final Activity callingActivity, String dirName)
    {
        mActivityWeakReference = new WeakReference<>(callingActivity);
        mDatabase = Database.getInstance(callingActivity.getApplicationContext());
        mDirectoryName = dirName;
    }

    @Override
    protected Void doInBackground(Void... voids)
    {
        /* all the files are there, the files app can't see them because they belong to this app
        I just need to list them all in the UI somehow and let the user choose which to import.
         */
        File folder = mActivityWeakReference.get().getFilesDir();
        if (folder.exists())
        {
            File[] files = folder.listFiles();
            for (File file : files)
            {
                Log.v(TAG, "File: " + file.getName());
            }
        }

        return null;
    }
}
