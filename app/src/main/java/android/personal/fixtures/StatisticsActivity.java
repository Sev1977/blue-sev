package android.personal.fixtures;

import android.os.Bundle;
import android.personal.fixtures.fragments.GraphsFragment;
import android.personal.fixtures.fragments.NumbersFragment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class StatisticsActivity extends AppCompatActivity
{
    private static final String GRAPHS_TAG = "graphsTab";
    private static final String NUMBERS_TAG = "numbersTab";

    private String mVisibleTab;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        final Toolbar myToolbar = findViewById(R.id.stats_toolbar);
        setSupportActionBar(myToolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final GraphsFragment fragment = new GraphsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.stats_content, fragment, GRAPHS_TAG)
                .commit();
        mVisibleTab = GRAPHS_TAG;

        final TabLayout tabLayout = findViewById(R.id.stats_tab_layout);
        tabLayout.addTab(
                tabLayout.newTab().setTag(GRAPHS_TAG).setText(R.string.stats_tab_label_graphs),
                true);
        tabLayout.addTab(
                tabLayout.newTab().setTag(NUMBERS_TAG).setText(R.string.stats_tab_label_numbers));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(final TabLayout.Tab tab)
            {
                final FragmentManager fragmentManager = getSupportFragmentManager();
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                final String tag = (String)tab.getTag();
                if (GRAPHS_TAG.equalsIgnoreCase(tag))
                {
                    transaction.replace(R.id.stats_content, new GraphsFragment()).commit();
                }
                else if (NUMBERS_TAG.equalsIgnoreCase(tag))
                {
                    transaction.replace(R.id.stats_content, new NumbersFragment()).commit();
                }
                mVisibleTab = tag;
            }

            @Override
            public void onTabUnselected(final TabLayout.Tab tab)
            {
            }

            @Override
            public void onTabReselected(final TabLayout.Tab tab)
            {
            }
        });
    }
}
