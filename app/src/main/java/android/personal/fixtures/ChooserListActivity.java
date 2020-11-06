package android.personal.fixtures;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.helpers.PlayersHelper;
import android.personal.fixtures.database.tables.Clubs;
import android.personal.fixtures.database.tables.Competitions;
import android.personal.fixtures.database.tables.Seasons;
import android.personal.fixtures.fragments.ChooserFragment;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

public class ChooserListActivity extends AppCompatActivity
        implements ChooserFragment.OnListInteractionListener
{
    private static final String TAG = ChooserListActivity.class.getSimpleName();

    static final String EXTRA_ITEM_TYPE = "item_type";
    static final String EXTRA_FILTER = "filter";
    static final String EXTRA_CHOSEN_ID = "chosen_id";

    static final int ITEM_TYPE_COMPETITION = 0;
    static final int ITEM_TYPE_SEASON = 1;
    static final int ITEM_TYPE_CLUB = 2;
    static final int ITEM_TYPE_PLAYER = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser_list);

        Cursor cursor = null;

        final Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            final int itemType = extras.getInt(EXTRA_ITEM_TYPE);
            switch (itemType)
            {
                case ITEM_TYPE_COMPETITION:
                    ((TextView)findViewById(R.id.choose_title)).setText(
                            R.string.competition_chooser_title);
                    cursor = Database.getInstance(getApplicationContext()).getColumnsForAllRecords(
                            Competitions.TABLE_NAME,
                            new String[]{BaseColumns._ID, Competitions.COL_NAME_SHORT_NAME},
                            Competitions.DEFAULT_SORT_ORDER);
                    break;

                case ITEM_TYPE_SEASON:
                    ((TextView)findViewById(R.id.choose_title)).setText(
                            R.string.season_chooser_title);
                    cursor = Database.getInstance(getApplicationContext()).getColumnsForAllRecords(
                            Seasons.TABLE_NAME,
                            new String[]{BaseColumns._ID, Seasons.COL_NAME_NAME},
                            Seasons.DEFAULT_SORT_ORDER);
                    break;

                case ITEM_TYPE_CLUB:
                {
                    ((TextView)findViewById(R.id.choose_title)).setText(
                            R.string.opponent_chooser_title);
                    final String filter = extras.getString(EXTRA_FILTER);
                    if (TextUtils.isEmpty(filter))
                    {
                        cursor = Database.getInstance(getApplicationContext())
                                .getColumnsForAllRecords(Clubs.TABLE_NAME,
                                        new String[]{BaseColumns._ID, Clubs.COL_NAME_SHORT_NAME},
                                        Clubs.DEFAULT_SORT_ORDER);
                    }
                    else
                    {
                        cursor = Database.getInstance(getApplicationContext())
                                .getColumnsForSelection(Clubs.TABLE_NAME,
                                        new String[]{BaseColumns._ID, Clubs.COL_NAME_SHORT_NAME},
                                        filter, null, Clubs.DEFAULT_SORT_ORDER);
                    }
                }
                break;

                case ITEM_TYPE_PLAYER:
                    ((TextView)findViewById(R.id.choose_title)).setText(
                            R.string.player_chooser_title);
                    cursor = PlayersHelper.getAllNamesWithIds(
                            Database.getInstance(ChooserListActivity
                                    .this), true);
                    break;
            }
        }

        final ChooserFragment chooserFragment =
                (ChooserFragment)getSupportFragmentManager().findFragmentById(R.id.chooserList);
        if (chooserFragment != null)
        {
            chooserFragment.setCursor(cursor);
        }
    }

    @Override
    public void onItemSelected(final long id)
    {
        setResult(RESULT_OK, new Intent().putExtra(EXTRA_CHOSEN_ID, id));
        finish();
    }
}
