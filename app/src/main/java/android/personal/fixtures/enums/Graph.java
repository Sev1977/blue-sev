package android.personal.fixtures.enums;

/**
 * The order of this enum MUST match the order of the strings in the string array,
 * R.array.graphs_descriptions.
 */
public enum Graph
{
    POINTS("Points"),
    AVG_POINTS("Average points"),
    GOALS_SCORED("Goals scored"),
    AVG_GOALS_SCORED("Average goals scored"),
    GOALS_CONCEDED("Goals conceded"),
    AVG_GOALS_CONCEDED("Average goals conceded"),
    GOALS_DIFFERENCE("Goals difference"),
    AVG_GOALS_DIFFERENCE("Average goals difference"),
    WINS_DRAWS_LOSSES("Wins, draws & losses");

    private String mName;

    Graph(final String name)
    {
        mName = name;
    }

    public String getName()
    {
        return mName;
    }

    public static Graph getGraphWithName(final String name)
    {
        for (Graph graph : Graph.values())
        {
            if (graph.mName.equalsIgnoreCase(name))
            {
                return graph;
            }
        }

        return null;
    }
}
