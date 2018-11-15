package android.personal.fixtures.enums;

import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertEquals;

/**
 *
 */
public class GraphTest
{
    private String[] mGraphNames;

    @Before
    public void setGraphNames()
    {
        mGraphNames =
                new String[]{"Points", "Average points", "Goals scored", "Average goals scored",
                        "Goals conceded", "Average goals conceded", "Goals difference",
                        "Average goals difference", "Wins, draws & losses", "Attendance",
                        "Average attendance"};
    }

    @Test
    public void getName()
    {
        assertEquals(mGraphNames[0], Graph.POINTS.getName());
        assertEquals(mGraphNames[1], Graph.AVG_POINTS.getName());
        assertEquals(mGraphNames[2], Graph.GOALS_SCORED.getName());
        assertEquals(mGraphNames[3], Graph.AVG_GOALS_SCORED.getName());
        assertEquals(mGraphNames[4], Graph.GOALS_CONCEDED.getName());
        assertEquals(mGraphNames[5], Graph.AVG_GOALS_CONCEDED.getName());
        assertEquals(mGraphNames[6], Graph.GOAL_DIFFERENCE.getName());
        assertEquals(mGraphNames[7], Graph.AVG_GOAL_DIFFERENCE.getName());
        assertEquals(mGraphNames[8], Graph.WINS_DRAWS_LOSSES.getName());
        assertEquals(mGraphNames[9], Graph.ATTENDANCE.getName());
        assertEquals(mGraphNames[10], Graph.AVERAGE_ATTENDANCE.getName());
    }

    @Test
    public void getGraphWithName()
    {
        assertEquals(Graph.POINTS, Graph.getGraphWithName(mGraphNames[0]));
        assertEquals(Graph.AVG_POINTS, Graph.getGraphWithName(mGraphNames[1]));
        assertEquals(Graph.GOALS_SCORED, Graph.getGraphWithName(mGraphNames[2]));
        assertEquals(Graph.AVG_GOALS_SCORED, Graph.getGraphWithName(mGraphNames[3]));
        assertEquals(Graph.GOALS_CONCEDED, Graph.getGraphWithName(mGraphNames[4]));
        assertEquals(Graph.AVG_GOALS_CONCEDED, Graph.getGraphWithName(mGraphNames[5]));
        assertEquals(Graph.GOAL_DIFFERENCE, Graph.getGraphWithName(mGraphNames[6]));
        assertEquals(Graph.AVG_GOAL_DIFFERENCE, Graph.getGraphWithName(mGraphNames[7]));
        assertEquals(Graph.WINS_DRAWS_LOSSES, Graph.getGraphWithName(mGraphNames[8]));
        assertEquals(Graph.ATTENDANCE, Graph.getGraphWithName(mGraphNames[9]));
        assertEquals(Graph.AVERAGE_ATTENDANCE, Graph.getGraphWithName(mGraphNames[10]));
    }
}
