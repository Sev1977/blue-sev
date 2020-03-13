package android.personal.fixtures;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.tables.Clubs;
import android.personal.fixtures.fragments.OppoChoiceFragment;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

public class OpponentChooserActivity extends AppCompatActivity
        implements OppoChoiceFragment.GridInteractionListener
{
    private static final String TAG = OpponentChooserActivity.class.getSimpleName();

    static final String EXTRA_FILTER = "filter";
    static final String EXTRA_CHOSEN_ID = "chosen_id";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opponent_picker);

        Cursor cursor = null;

        final Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            ((TextView)findViewById(R.id.choose_title)).setText(R.string.opponent_chooser_title);
            final String filter = extras.getString(EXTRA_FILTER);
            if (TextUtils.isEmpty(filter))
            {
                cursor = Database.getInstance(getApplicationContext()).getColumnsForAllRecords(
                        Clubs.TABLE_NAME, new String[]{BaseColumns._ID, Clubs.COL_NAME_SHORT_NAME},
                        Clubs.DEFAULT_SORT_ORDER);
            }
            else
            {
                cursor = Database.getInstance(getApplicationContext()).getColumnsForSelection(
                        Clubs.TABLE_NAME, new String[]{BaseColumns._ID, Clubs.COL_NAME_SHORT_NAME},
                        filter, null, Clubs.DEFAULT_SORT_ORDER);
            }
        }

        final OppoChoiceFragment chooserFragment =
                (OppoChoiceFragment)getSupportFragmentManager().findFragmentById(R.id.chooserGrid);
        if (chooserFragment != null)
        {
            chooserFragment.setCursor(cursor);
        }
    }

    @Override
    public void onTeamSelected(final long id)
    {
        setResult(RESULT_OK, new Intent().putExtra(EXTRA_CHOSEN_ID, id));
        finish();
    }
}
