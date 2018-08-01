package android.personal.fixtures;

import android.content.Intent;
import android.os.Bundle;
import android.personal.fixtures.fragments.SeasonsFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SeasonsListActivity extends AppCompatActivity
        implements SeasonsFragment.OnSeasonListInteractionListener
{
    private static final int REQUEST_CODE_ADD_SEASON = 1;
    private static final int REQUEST_CODE_EDIT_SEASON = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seasons_list);

        final Toolbar myToolbar = findViewById(R.id.seasons_list_toolbar);
        setSupportActionBar(myToolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_seasons_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.seasons_list_action_add:
                startActivityForResult(new Intent(this, EditSeasonActivity.class),
                        REQUEST_CODE_ADD_SEASON);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        boolean handled = false;

        switch (requestCode)
        {
            case REQUEST_CODE_ADD_SEASON:
            case REQUEST_CODE_EDIT_SEASON:
                if (resultCode == RESULT_OK)
                {
                    recreate();
                    handled = true;
                }
                break;
        }

        if (!handled)
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSeasonSelected(final long id)
    {
        final Intent intent = new Intent(this, EditSeasonActivity.class);
        intent.putExtra(EditSeasonActivity.EXTRA_SEASON_ID, id);
        startActivityForResult(intent, REQUEST_CODE_EDIT_SEASON);
    }
}
