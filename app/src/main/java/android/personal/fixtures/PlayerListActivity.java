package android.personal.fixtures;

import android.content.Intent;
import android.os.Bundle;
import android.personal.fixtures.fragments.PlayersFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class PlayerListActivity extends AppCompatActivity
        implements PlayersFragment.OnPlayersListInteractionListener
{
    private static final int REQUEST_CODE_ADD_PLAYER = 1;
    private static final int REQUEST_CODE_EDIT_PLAYER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_list);

        final Toolbar myToolbar = findViewById(R.id.players_list_toolbar);
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
        inflater.inflate(R.menu.menu_players_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.players_list_action_add)
        {
            startActivityForResult(new Intent(this, EditPlayerActivity.class),
                    REQUEST_CODE_ADD_PLAYER);
            return true;
        }

        // If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        boolean handled = false;

        switch (requestCode)
        {
            case REQUEST_CODE_ADD_PLAYER:
            case REQUEST_CODE_EDIT_PLAYER:
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
    public void onPlayerSelected(final long id)
    {
        final Intent intent = new Intent(this, EditPlayerActivity.class);
        intent.putExtra(EditPlayerActivity.EXTRA_PLAYER_ID, id);
        startActivityForResult(intent, REQUEST_CODE_EDIT_PLAYER);
    }
}
