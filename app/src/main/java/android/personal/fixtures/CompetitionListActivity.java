package android.personal.fixtures;

import android.content.Intent;
import android.os.Bundle;
import android.personal.fixtures.fragments.CompetitionsFragment.OnCompetitionsListInteractionListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class CompetitionListActivity extends AppCompatActivity
        implements OnCompetitionsListInteractionListener
{
    private static final int REQUEST_CODE_ADD_COMPETITION = 1;
    private static final int REQUEST_CODE_EDIT_COMPETITION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competitions_list);

        final Toolbar myToolbar = findViewById(R.id.competitions_list_toolbar);
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
        inflater.inflate(R.menu.menu_competitions_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.competitions_list_action_add:
                startActivityForResult(new Intent(this, EditCompetitionActivity.class),
                        REQUEST_CODE_ADD_COMPETITION);
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
            case REQUEST_CODE_ADD_COMPETITION:
            case REQUEST_CODE_EDIT_COMPETITION:
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
    public void onCompetitionSelected(final long id)
    {
        final Intent intent = new Intent(this, EditCompetitionActivity.class);
        intent.putExtra(EditCompetitionActivity.EXTRA_COMPETITION_ID, id);
        startActivityForResult(intent, REQUEST_CODE_EDIT_COMPETITION);
    }
}
