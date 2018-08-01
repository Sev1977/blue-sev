package android.personal.fixtures;

import android.content.Intent;
import android.os.Bundle;
import android.personal.fixtures.fragments.ClubsFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ClubListActivity extends AppCompatActivity
        implements ClubsFragment.OnClubListInteractionListener
{
    private static final int REQUEST_CODE_ADD_CLUB = 1;
    private static final int REQUEST_CODE_EDIT_CLUB = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_list);

        final Toolbar myToolbar = findViewById(R.id.clubs_list_toolbar);
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
        inflater.inflate(R.menu.menu_clubs_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.clubs_list_action_add:
                startActivityForResult(new Intent(this, EditClubActivity.class),
                        REQUEST_CODE_ADD_CLUB);
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
            case REQUEST_CODE_ADD_CLUB:
            case REQUEST_CODE_EDIT_CLUB:
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
    public void onClubSelected(final long id)
    {
        final Intent intent = new Intent(this, EditClubActivity.class);
        intent.putExtra(EditClubActivity.EXTRA_CLUB_ID, id);
        startActivityForResult(intent, REQUEST_CODE_EDIT_CLUB);
    }
}
