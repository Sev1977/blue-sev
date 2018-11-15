package android.personal.fixtures.fragments;

import android.os.Bundle;
import android.personal.fixtures.GraphLoader;
import android.personal.fixtures.R;
import android.personal.fixtures.enums.Graph;
import android.personal.fixtures.interfaces.IGraphsCallbacks;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.Chart;

/**
 * A fragment displaying graphs.
 */
public class GraphsFragment extends Fragment implements IGraphsCallbacks
{
    private static final String TAG = GraphsFragment.class.getSimpleName();

    private Chart[] mAllCharts = new Chart[Graph.values().length];
    private Graph mSelectedGraph;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
     * screen orientation changes).
     */
    public GraphsFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_graphs, container, false);
        final Spinner graphChooser = view.findViewById(R.id.graphSpinner);
        graphChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view,
                    final int position, final long id)
            {
                mSelectedGraph = Graph.getGraphWithName(graphChooser.getSelectedItem().toString());
                loadGraph();
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent)
            {
            }
        });
        mSelectedGraph = Graph.getGraphWithName(graphChooser.getSelectedItem().toString());

        mAllCharts[Graph.POINTS.ordinal()] = view.findViewById(R.id.lineChart);
        mAllCharts[Graph.AVG_POINTS.ordinal()] = view.findViewById(R.id.lineChart);
        mAllCharts[Graph.GOALS_SCORED.ordinal()] = view.findViewById(R.id.lineChart);
        mAllCharts[Graph.AVG_GOALS_SCORED.ordinal()] = view.findViewById(R.id.lineChart);
        mAllCharts[Graph.GOALS_CONCEDED.ordinal()] = view.findViewById(R.id.lineChart);
        mAllCharts[Graph.AVG_GOALS_CONCEDED.ordinal()] = view.findViewById(R.id.lineChart);
        mAllCharts[Graph.GOAL_DIFFERENCE.ordinal()] = view.findViewById(R.id.barChart);
        mAllCharts[Graph.AVG_GOAL_DIFFERENCE.ordinal()] = view.findViewById(R.id.lineChart);
        mAllCharts[Graph.WINS_DRAWS_LOSSES.ordinal()] = view.findViewById(
                R.id.pieChart);
        mAllCharts[Graph.ATTENDANCE.ordinal()] = view.findViewById(R.id.barChart);
        mAllCharts[Graph.AVERAGE_ATTENDANCE.ordinal()] = view.findViewById(R.id.lineChart);

        return view;
    }

    private void loadGraph()
    {
        for (Chart chart : mAllCharts)
        {
            if (chart != null)
            {
                chart.clear();
                chart.setVisibility(View.GONE);
            }
        }
        new GraphLoader(getActivity(), this).execute(mSelectedGraph);
    }

    @Override
    public void onGraphLoaded()
    {
        final Chart chart = mAllCharts[mSelectedGraph.ordinal()];
        if (chart != null)
        {
            chart.setVisibility(View.VISIBLE);
            chart.invalidate();
        }
    }

    @Override
    public Chart getSelectedChart()
    {
        return mAllCharts[mSelectedGraph.ordinal()];
    }
}
