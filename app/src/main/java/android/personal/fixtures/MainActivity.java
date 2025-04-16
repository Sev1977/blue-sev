package android.personal.fixtures;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.fragments.FixturesFragment;
import android.personal.fixtures.fragments.ResultsFragment;
import android.personal.fixtures.utils.DataExporter;
import android.personal.fixtures.utils.DataImporter;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity
        implements FixturesFragment.OnFixturesListInteractionListener,
        ResultsFragment.OnResultsListInteractionListener
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_ADD_FIXTURE = 0;
    private static final int REQUEST_VIEW_FIXTURE = 1;
    private static final int REQUEST_EDIT_FIXTURE = 2;
    private static final int REQUEST_CHOOSE_EXPORT_DIRECTORY = 3;
    private static final int REQUEST_CHOOSE_IMPORT_DIRECTORY = 4;

    private static final String FIXTURES_TAG = "fixturesTab";
    private static final String RESULTS_TAG = "resultsTab";

    private String mVisibleTab;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.my_toolbar));

        /*
         * Create the database
         */
        Database.getInstance(getApplicationContext());

        final FixturesFragment fragment = new FixturesFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.main_content, fragment,
                FIXTURES_TAG).commit();
        mVisibleTab = FIXTURES_TAG;

        final TabLayout tabLayout = findViewById(R.id.main_tab_layout);
        tabLayout.addTab(
                tabLayout.newTab().setTag(FIXTURES_TAG).setText(R.string.fixtures_tab_label), true);
        tabLayout.addTab(
                tabLayout.newTab().setTag(RESULTS_TAG).setText(R.string.results_tab_label));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(final TabLayout.Tab tab)
            {
                final FragmentManager fragmentManager = getSupportFragmentManager();
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                final String tag = (String)tab.getTag();
                if (FIXTURES_TAG.equalsIgnoreCase(tag))
                {
                    transaction.replace(R.id.main_content, new FixturesFragment(), tag).commit();
                }
                else if (RESULTS_TAG.equalsIgnoreCase(tag))
                {
                    transaction.replace(R.id.main_content, new ResultsFragment(), tag).commit();
                }
                mVisibleTab = tag;
            }

            @Override
            public void onTabUnselected(final TabLayout.Tab tab)
            {
                // do nothing
            }

            @Override
            public void onTabReselected(final TabLayout.Tab tab)
            {
                // do nothing
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.home_action_add:
                startActivityForResult(new Intent(MainActivity.this, EditFixtureActivity.class),
                        REQUEST_ADD_FIXTURE);
                return true;

            case R.id.home_action_filter:
                startActivity(new Intent(MainActivity.this, FilterActivity.class));
                return true;

            case R.id.home_action_stats:
                startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
                return true;

            case R.id.home_action_clubs:
                startActivity(new Intent(MainActivity.this, ClubListActivity.class));
                return true;

            case R.id.home_action_comps:
                startActivity(new Intent(MainActivity.this, CompetitionListActivity.class));
                return true;

            case R.id.home_action_players:
                startActivity(new Intent(MainActivity.this, PlayerListActivity.class));
                return true;

            case R.id.home_action_seasons:
                startActivity(new Intent(MainActivity.this, SeasonsListActivity.class));
                return true;

            case R.id.home_action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;

            case R.id.home_action_export_import:
                onExportImportClicked();
                return true;

            case R.id.home_action_about:
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
        switch (requestCode)
        {
            case REQUEST_VIEW_FIXTURE:
                if (resultCode == RESULT_OK)
                {
                    if ((data != null) && data.getBooleanExtra(FixtureActivity.EXTRA_FIXTURE_EDITED,
                            false))
                    {
                        refresh();
                    }
                }
                break;

            case REQUEST_ADD_FIXTURE:
            case REQUEST_EDIT_FIXTURE:
                if (resultCode == RESULT_OK)
                {
                    refresh();
                }
                break;

            case REQUEST_CHOOSE_EXPORT_DIRECTORY:
                if (resultCode == RESULT_OK)
                {
                    new DataExporter(this, data.getData().getPath()).execute();
                }
                break;

            case REQUEST_CHOOSE_IMPORT_DIRECTORY:
                if (resultCode == RESULT_OK)
                {
                    new DataImporter(this, data.getData().getPath()).execute();
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onFixtureSelected(final long id)
    {
        viewFixture(id);
    }

    @Override
    public void onEditClicked(final long id)
    {
        editFixture(id);
    }

    private void editFixture(final long id)
    {
        final Intent edit = new Intent(MainActivity.this, EditFixtureActivity.class);
        edit.putExtra(EditFixtureActivity.EXTRA_FIXTURE_ID, id);
        startActivityForResult(edit, REQUEST_EDIT_FIXTURE);
    }

    private void viewFixture(final long id)
    {
        final Intent edit = new Intent(MainActivity.this, FixtureActivity.class);
        edit.putExtra(FixtureActivity.EXTRA_FIXTURE_ID, id);
        startActivityForResult(edit, REQUEST_VIEW_FIXTURE);
    }

    private void refresh()
    {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        final Fragment fragment = fragmentManager.findFragmentByTag(mVisibleTab);
        if (mVisibleTab.equalsIgnoreCase(FIXTURES_TAG))
        {
            // Fixtures are visible
            if (fragment != null)
            {
                transaction.remove(fragment).add(R.id.main_content, new FixturesFragment(),
                        FIXTURES_TAG).commitAllowingStateLoss();
            }
        }
        else
        {
            // Maybe the results are visible
            if (fragment != null)
            {
                transaction.remove(fragment).add(R.id.main_content, new ResultsFragment(),
                        RESULTS_TAG).commitAllowingStateLoss();
            }
        }
    }

    /**
     * Show a dialog asking the user to choose to either export the database or import it, then
     * action the request. End with a completion dialog for success or failure.
     */
    private void onExportImportClicked()
    {
        final CharSequence[] options = new CharSequence[]
                {
                    getString(R.string.export_option),
                    getString(R.string.import_option)
                };
        final AtomicInteger choice = new AtomicInteger(0); // default is to export.
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.export_data_popup_title)
                .setSingleChoiceItems(options, 0, (dialogInterface, i) -> choice.set(i))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) ->
                {
                    if (choice.get() == 0)
                    {
                        exportData();
                    }
                    else
                    {
                        importData();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onResultSelected(final long id)
    {
        viewFixture(id);
    }

    @Override
    public void onEditResult(final long id)
    {
        editFixture(id);
    }

    /**
     * Export the database contents to a CSV file. The user needs to choose where to put it.
     */
    public void exportData()
    {
        Log.v(TAG, "export");
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CHOOSE_EXPORT_DIRECTORY);
    }

    /**
     * Import data from a CSV file. The user will choose which file.
     */
    public void importData()
    {
        Log.v(TAG, "import");
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CHOOSE_IMPORT_DIRECTORY);
    }
}
