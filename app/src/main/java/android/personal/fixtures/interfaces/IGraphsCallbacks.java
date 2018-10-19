package android.personal.fixtures.interfaces;

import com.github.mikephil.charting.charts.Chart;

/**
 *
 */
public interface IGraphsCallbacks
{
    Chart getSelectedChart();
    void onGraphLoaded();
}
