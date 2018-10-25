package android.personal.fixtures;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.personal.fixtures.database.Database;
import android.personal.fixtures.database.FixturesHelper;
import android.personal.fixtures.database.tables.Fixtures;
import android.personal.fixtures.enums.Graph;
import android.personal.fixtures.interfaces.IGraphsCallbacks;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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
                    loadGoalsScoredChart();
                    break;

                case AVG_GOALS_SCORED:
                    loadAverageGoalsScoredChart();
                    break;

                case GOALS_CONCEDED:
                    loadGoalsConcededChart();
                    break;

                case AVG_GOALS_CONCEDED:
                    loadAverageGoalsConcededChart();
                    break;

                case GOAL_DIFFERENCE:
                    loadGoalDifferenceChart();
                    break;

                case AVG_GOAL_DIFFERENCE:
                    loadAverageGoalDifferenceChart();
                    break;

                case WINS_DRAWS_LOSSES:
                    loadWinsDrawsLossesChart();
                    break;

                case ATTENDANCE:
                    loadAttendance();
                    break;

                case AVERAGE_ATTENDANCE:
                    loadAverageAttendance();
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
        final int[] pointsData = FixturesHelper.getPointsProgress(mDatabase);
        if (pointsData != null)
        {
            final List<Entry> entries = new ArrayList<>(pointsData.length);
            int x = 0;
            for (int points : pointsData)
            {
                entries.add(new Entry(x++, points));
            }

            createLineChart((LineChart)mCallbacks.getSelectedChart(), entries, Graph.POINTS);
        }
    }

    private void loadAveragePointsChart()
    {
        final float[] pointsData = FixturesHelper.getPointsAverage(mDatabase);
        if (pointsData != null)
        {
            final List<Entry> entries = new ArrayList<>(pointsData.length);
            int x = 0;
            for (float points : pointsData)
            {
                entries.add(new Entry(x++, points));
            }

            createLineChart((LineChart)mCallbacks.getSelectedChart(), entries, Graph.AVG_POINTS);
        }
    }

    private void loadGoalsScoredChart()
    {
        final int[] goalsScored = FixturesHelper.getGoalsScored(mDatabase);
        if (goalsScored != null)
        {
            final List<Entry> entries = new ArrayList<>(goalsScored.length);
            int x = 0;
            for (int goals : goalsScored)
            {
                entries.add(new Entry(x++, goals));
            }

            createLineChart((LineChart)mCallbacks.getSelectedChart(), entries, Graph.GOALS_SCORED);
        }
    }

    private void loadAverageGoalsScoredChart()
    {
        final float[] averageGoalsScored = FixturesHelper.getAverageGoalsScored(mDatabase);
        if (averageGoalsScored != null)
        {
            final List<Entry> entries = new ArrayList<>(averageGoalsScored.length);
            int x = 0;
            for (float average : averageGoalsScored)
            {
                entries.add(new Entry(x++, average));
            }

            createLineChart((LineChart)mCallbacks.getSelectedChart(), entries,
                    Graph.AVG_GOALS_SCORED);
        }
    }

    private void loadGoalsConcededChart()
    {
        final int[] goalsConceded = FixturesHelper.getGoalsConceded(mDatabase);
        if (goalsConceded != null)
        {
            final List<Entry> entries = new ArrayList<>(goalsConceded.length);
            int x = 0;
            for (int goals : goalsConceded)
            {
                entries.add(new Entry(x++, goals));
            }

            createLineChart((LineChart)mCallbacks.getSelectedChart(), entries,
                    Graph.GOALS_CONCEDED);
        }
    }

    private void loadAverageGoalsConcededChart()
    {
        final float[] averageGoalsConceded = FixturesHelper.getAverageGoalsConceded(mDatabase);
        if (averageGoalsConceded != null)
        {
            final List<Entry> entries = new ArrayList<>(averageGoalsConceded.length);
            int x = 0;
            for (float average : averageGoalsConceded)
            {
                entries.add(new Entry(x++, average));
            }

            createLineChart((LineChart)mCallbacks.getSelectedChart(), entries,
                    Graph.AVG_GOALS_CONCEDED);
        }
    }

    private void loadGoalDifferenceChart()
    {
        // Create the goals difference data
        final int[] goalDifference = FixturesHelper.getGoalDifference(mDatabase);
        if (goalDifference != null)
        {
            // Create the bar chart
            final BarChart barChart = (BarChart)mCallbacks.getSelectedChart();
            barChart.setDescription(null);
            barChart.getLegend().setEnabled(false);

            final ArrayList<BarEntry> barEntries = new ArrayList<>();
            for (int gameNumber = 0; gameNumber < goalDifference.length; gameNumber++)
            {
                barEntries.add(new BarEntry(gameNumber, goalDifference[gameNumber]));
            }

            final BarDataSet barDataSet = new BarDataSet(barEntries, "Goal difference");
            // Set the bar colours depending if the difference is +'ve or -'ve
            final int positiveDiffColour = mActivityWeakReference.get().getColor(
                    R.color.bar_chart_positive);
            final int negativeDiffColour = mActivityWeakReference.get().getColor(
                    R.color.bar_chart_negative);
            final int[] barColours = new int[goalDifference.length];
            for (int i = 0; i < goalDifference.length; i++)
            {
                if (goalDifference[i] >= 0)
                {
                    barColours[i] = positiveDiffColour;
                }
                else
                {
                    barColours[i] = negativeDiffColour;
                }
            }
            barDataSet.setColors(barColours);

            // create bar data object
            final BarData barData = new BarData(barDataSet);
            barChart.setData(barData);
            // Set the left y-axis label colour
            barChart.getAxisLeft().setTextColor(mTextColor);
            // hide the right y-axis
            barChart.getAxisRight().setEnabled(false);
            // style the x-axis
            final XAxis xAxis = barChart.getXAxis();
            xAxis.setTextColor(mTextColor);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        }
    }

    private void loadAverageGoalDifferenceChart()
    {
        final float[] averageGoalDifference = FixturesHelper.getAverageGoalDifference(mDatabase);
        if (averageGoalDifference != null)
        {
            final List<Entry> entries = new ArrayList<>(averageGoalDifference.length);
            int x = 0;
            for (float average : averageGoalDifference)
            {
                entries.add(new Entry(x++, average));
            }

            createLineChart((LineChart)mCallbacks.getSelectedChart(), entries,
                    Graph.AVG_GOAL_DIFFERENCE);
        }
    }

    private void loadWinsDrawsLossesChart()
    {
        // Create the league position data
        final int[] results = FixturesHelper.getNumberOfWinsDrawsLosses(mDatabase);
        if (results != null)
        {
            // Create the line chart
            final PieChart pieChart = (PieChart)mCallbacks.getSelectedChart();
            pieChart.setDescription(null);
            pieChart.setCenterTextSize(24);

            float[] resultsData = new float[3];
            int numResults = 0;
            for (int i = 0; i < results.length; i++)
            {
                resultsData[i] = results[i];
                numResults += results[i];
            }
            pieChart.setCenterText(String.valueOf(numResults));

            final ArrayList<PieEntry> pieEntries = new ArrayList<>();
            for (int y = 0; y < resultsData.length; y++)
            {
                pieEntries.add(new PieEntry(resultsData[y], y));
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

    private class AttendanceXAxisValueFormatter implements IAxisValueFormatter
    {
        private final int[] mAttendances;

        AttendanceXAxisValueFormatter(final int[] attendances)
        {
            mAttendances = attendances;
        }

        @Override
        public String getFormattedValue(final float value, final AxisBase axis)
        {
            String label = null;
            final int gameNumber = (int)value;
            final Cursor match = mDatabase.getReadableDatabase().query(Fixtures.TABLE_NAME,
                    new String[]{Fixtures.COL_NAME_OPPOSITION}, Fixtures.COL_NAME_ATTENDANCE + "=?",
                    new String[]{String.valueOf(mAttendances[gameNumber])}, null, null, null);
            if (match != null)
            {
                if (match.moveToFirst())
                {
                    label = match.getString(0);
                }
                match.close();
            }

            return label;
        }
    }

    private void loadAttendance()
    {
        // Create the attendance data
        final int[] attendances = FixturesHelper.getAttendances(mDatabase,
                mActivityWeakReference.get().getApplicationContext());
        if (attendances != null)
        {
            // Create the bar chart
            final BarChart barChart = (BarChart)mCallbacks.getSelectedChart();
            barChart.setDescription(null);
            barChart.getLegend().setEnabled(false);

            final ArrayList<BarEntry> barEntries = new ArrayList<>();
            for (int gameNumber = 0; gameNumber < attendances.length; gameNumber++)
            {
                barEntries.add(new BarEntry(gameNumber, attendances[gameNumber]));
            }

            final BarDataSet barDataSet = new BarDataSet(barEntries, "Attendances");
            barDataSet.setColor(mActivityWeakReference.get().getColor(R.color.colorAccent));
            barDataSet.setValueTextColor(mTextColor);

            // create bar data object
            final BarData barData = new BarData(barDataSet);
            barChart.setData(barData);
            // hide the left y-axis
            barChart.getAxisLeft().setEnabled(false);
            // hide the right y-axis
            barChart.getAxisRight().setEnabled(false);
            // style the x-axis
            final XAxis xAxis = barChart.getXAxis();
            xAxis.setTextColor(mTextColor);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(new AttendanceXAxisValueFormatter(attendances));
            xAxis.setLabelRotationAngle(270f);
        }
    }

    private void loadAverageAttendance()
    {
        // Get the attendances
        final int[] attendances = FixturesHelper.getAttendances(mDatabase,
                mActivityWeakReference.get().getApplicationContext());
        if (attendances != null)
        {
            final List<Entry> entries = new ArrayList<>(attendances.length);
            int gameNumber = 0;
            int runningTotalAttendance = 0;
            for (int attendance : attendances)
            {
                runningTotalAttendance += attendance;
                entries.add(new Entry(gameNumber++, runningTotalAttendance / gameNumber));
            }

            createLineChart((LineChart)mCallbacks.getSelectedChart(), entries,
                    Graph.AVERAGE_ATTENDANCE);
        }
    }

    private void createLineChart(final LineChart lineChart, final List<Entry> entries,
            final Graph graph)
    {
        if (lineChart != null)
        {
            lineChart.clear();

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
            final YAxis yAxis = lineChart.getAxisLeft();
            final boolean floatingPointValues =
                    (graph == Graph.AVG_POINTS) || (graph == Graph.AVG_GOALS_SCORED) ||
                            (graph == Graph.AVG_GOALS_CONCEDED) ||
                            (graph == Graph.AVG_GOAL_DIFFERENCE);
            yAxis.setGranularity(floatingPointValues ? 0.1f : 1f);
            yAxis.setTextColor(mTextColor);
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
    }
}
