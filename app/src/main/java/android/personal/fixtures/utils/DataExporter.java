/*
 * blue-sev
 * DataExporter
 * 14/08/2023
 */

package android.personal.fixtures.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.personal.fixtures.database.Database;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DataExporter extends AsyncTask<Void, Void, Void>
{
    private final String TAG = DataExporter.class.getSimpleName();

    private final WeakReference<Activity> mActivityWeakReference;
    private final Database mDatabase;
    private final String mDirectoryName;
    private ProgressDialog mProgressDialog;
    private long mShowTime;

    public DataExporter(final Activity callingActivity, String dirName)
    {
        mActivityWeakReference = new WeakReference<>(callingActivity);
        mDatabase = Database.getInstance(callingActivity.getApplicationContext());
        mDirectoryName = dirName;
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
        if (true)
        {
            File folder = mActivityWeakReference.get().getFilesDir();
            if (folder.exists())
            {
                File[] files = folder.listFiles();
                for (File file : files)
                {
                    Log.v(TAG, "File: " + file.getName());
                }
            }
        } else
        {
            // Go through each table and convert the contents to CSV strings.
            List<String> tableContents = new ArrayList<>();
            for (String tableName : Database.ALL_TABLE_NAMES)
            {
                Cursor records = mDatabase.getAllRecords(tableName, "");
                if (records.moveToFirst())
                {
                    JSONArray jsonArray = new JSONArray();
                    while (!records.isAfterLast())
                    {
                        for (int i = 0; i < records.getColumnCount(); i++)
                        {
                            JSONObject row = new JSONObject();
                            try
                            {
                                row.put("column name", records.getColumnName(i));
                                switch (records.getType(i))
                                {
                                    case android.database.Cursor.FIELD_TYPE_INTEGER:
                                        row.put(records.getColumnName(i), records.getInt(i));
                                        break;

                                    case android.database.Cursor.FIELD_TYPE_STRING:
                                        row.put(records.getColumnName(i), records.getString(i));
                                        break;

                                    default:
                                        break;
                                }
                            }
                            catch (JSONException ex)
                            {
                                throw new RuntimeException(ex);
                            }
                            jsonArray.put(row);
                        }

                        records.moveToNext();
                    }

                    // Create a CSV file and write the strings to it
                    try
                    {
                        String fileName = "Efstat-export-" + tableName + "-" + (System.currentTimeMillis() / 1000) + ".json";
                        File file = new File(mDirectoryName, fileName);
                        if (file.createNewFile())
                        {
                            FileWriter writer = new FileWriter(file);
                            writer.write(jsonArray.toString(4));
                            writer.close();
                            Log.v(TAG, "File: " + fileName + " written");
                        }
                        else
                        {
                            Log.e(TAG, "File: " + fileName + " failed");
                        }
                    }
                    catch (JSONException jsonException)
                    {
                        Log.e(TAG, "JSON exception: " + jsonException.getMessage());
                    }
                    catch (IOException exception)
                    {
                        Log.e(TAG, "IO exception: " + exception.getMessage());
                    }
                }
            }
        }
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
        mShowTime = System.currentTimeMillis();
        mProgressDialog = new ProgressDialog(mActivityWeakReference.get());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Exporting...");
        mProgressDialog.show();
    }

    private void hideProgressDialog()
    {
        if (mProgressDialog != null)
        {
            while (System.currentTimeMillis() < (mShowTime + 1000))
            {
            }
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
