package android.personal.fixtures;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.FixturesHelper;
import android.personal.fixtures.enums.Graph;
import android.personal.fixtures.interfaces.IGraphsCallbacks;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class GraphLoader extends AsyncTask<Graph, Void, Void>
{
    private static final String TAG = GraphLoader.class.getSimpleName();

    private final WeakReference<Activity> mActivityWeakReference;
    private final IGraphsCallbacks mCallbacks;
    private final int mTextColor;
    private final Database mDatabase;
    private ProgressDialog mProgressDialog;

    public GraphLoader(final Activity activity, final IGraphsCallbacks callbacks)
    {
        mActivityWeakReference = new WeakReference<>(activity);
        mCallbacks = callbacks;
        mTextColor = activity.getColor(R.color.textColorPrimary);
        mDatabase = Database.getInstance(activity.getApplicationContext());
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        showProgressDialog();
    }

    @Override
    protected Void doInBackground(final Graph... params)
    {
        if ((params != null) && (params.length == 1) && (params[0] != null))
        {
            Log.v(TAG, "load graph for " + params[0]);
            final Graph graph = params[0];
            switch (graph)
            {
                case POINTS:
                    loadPointsChart();
                    break;

                case AVG_POINTS:
                    loadAveragePointsChart();
                    break;

                case GOALS_SCORED:
                case AVG_GOALS_SCORED:
                case GOALS_CONCEDED:
                case AVG_GOALS_CONCEDED:
                case GOALS_DIFFERENCE:
                case AVG_GOALS_DIFFERENCE:
                    break;

                case WINS_DRAWS_LOSSES:
                    loadWinsDrawsLossesChart();
                    break;
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(final Void aVoid)
    {
        super.onPostExecute(aVoid);
        if (mCallbacks != null)
        {
            mCallbacks.onGraphLoaded();
        }
        hideProgressDialog();
    }

    private void showProgressDialog()
    {
        mProgressDialog = new ProgressDialog(mActivityWeakReference.get());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
    }

    private void hideProgressDialog()
    {
        if (mProgressDialog != null)
        {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private void loadPointsChart()
    {
        final int[] data = FixturesHelper.getPointsProgress(mDatabase);
        
        final List<Entry> entries = new ArrayList<>(data.length);
        int index = 0;
        for (int i : data)
        {
            entries.add(new Entry(index++, i));
        }

        createLineChart((LineChart)mCallbacks.getSelectedChart(), entries);
    }

    private void loadAveragePointsChart()
    {
        final float[] data = FixturesHelper.getPointsAverage(mDatabase);

        final List<Entry> entries = new ArrayList<>(data.length);
        int index = 0;
        for (float i : data)
        {
            entries.add(new Entry(index++, i));
        }

        createLineChart((LineChart)mCallbacks.getSelectedChart(), entries);
    }

    private void createLineChart(final LineChart lineChart, final List<Entry> entries)
    {
        // Create the data styling
        final LineDataSet lineDataSet = new LineDataSet(entries, null);
        lineDataSet.setColor(mActivityWeakReference.get().getColor(R.color.colorAccent));
        lineDataSet.setDrawValues(false);

        // Create the line chart data and add it to the line chart
        final LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        // Style the chart
        // basic styling
        lineChart.getLegend().setEnabled(false);
        lineChart.setDescription(null);
        // Left y-axis style
        final YAxis axisLeft = lineChart.getAxisLeft();
        axisLeft.setGranularity(1f);
        axisLeft.setTextColor(mTextColor);
        // hide the right y-axis
        lineChart.getAxisRight().setEnabled(false);
        // style the x-axis
        final XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(mTextColor);
        xAxis.setValueFormatter(new IAxisValueFormatter()
        {
            @Override
            public String getFormattedValue(final float value, final AxisBase axis)
            {
                return String.valueOf((int)value + 1);
            }
        });
    }

    private void loadWinsDrawsLossesChart()
    {
        // Create the line chart
        final PieChart pieChart = (PieChart)mCallbacks.getSelectedChart();
        pieChart.setDescription(null);
        pieChart.setCenterTextSize(24);

        // Create the league position data
        final int[] results = FixturesHelper.getNumberOfWinsDrawsLosses(mDatabase);
        float[] data = new float[3];
        int numResults = 0;
        for (int i = 0; i < results.length; i++)
        {
            data[i] = results[i];
            numResults += results[i];
        }
        pieChart.setCenterText(String.valueOf(numResults));

        final ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < data.length; i++)
        {
            pieEntries.add(new PieEntry(data[i], i));
        }

        //create the data set
        final PieDataSet pieDataSet = new PieDataSet(pieEntries, "Wins, draws, losses");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(16);
        pieDataSet.setValueFormatter(new IValueFormatter()
        {
            @Override
            public String getFormattedValue(final float value, final Entry entry,
                    final int dataSetIndex, final ViewPortHandler viewPortHandler)
            {
                return String.valueOf((int)value);
            }
        });

        // add colors to data set
        final ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GREEN);
        colors.add(Color.GRAY);
        colors.add(Color.RED);
        pieDataSet.setColors(colors);

        //add legend to chart
        final Legend legend = pieChart.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setTextSize(16);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

        //create pie data object
        final PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
    }
}
