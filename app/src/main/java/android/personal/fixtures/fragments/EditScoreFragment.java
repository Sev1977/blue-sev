package android.personal.fixtures.fragments;

import android.os.Bundle;
import android.personal.fixtures.R;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Use the {@link EditScoreFragment#newInstance} factory method to create an instance of this
 * fragment.
 */
public class EditScoreFragment extends DialogFragment
{
    public static final String EXTRA_HOME_SCORE = "home_score";
    public static final String EXTRA_AWAY_SCORE = "away_score";

    private int mHomeScore;
    private int mAwayScore;
    private EditText mHomeScoreView;
    private EditText mAwayScoreView;

    private ScoreDialogListener mListener;

    public EditScoreFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param homeScore Number of goals scored by the home team.
     * @param awayScore Number of goals scored by the away team.
     * @return A new instance of fragment EditScoreFragment.
     */
    public static EditScoreFragment newInstance(int homeScore, int awayScore)
    {
        final EditScoreFragment fragment = new EditScoreFragment();
        final Bundle args = new Bundle();
        args.putInt(EXTRA_HOME_SCORE, homeScore);
        args.putInt(EXTRA_AWAY_SCORE, awayScore);
        fragment.setArguments(args);
        return fragment;
    }

    public void setListener(final ScoreDialogListener listener)
    {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mHomeScore = getArguments().getInt(EXTRA_HOME_SCORE, 0);
            mAwayScore = getArguments().getInt(EXTRA_AWAY_SCORE, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        final View layout = inflater.inflate(R.layout.fragment_edit_score, container, false);
        mHomeScoreView = layout.findViewById(R.id.homeScoreInput);
        mHomeScoreView.setText(String.valueOf(mHomeScore));
        mAwayScoreView = layout.findViewById(R.id.awayScoreInput);
        mAwayScoreView.setText(String.valueOf(mAwayScore));
        layout.findViewById(R.id.editScoreOkButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                final int[] score = new int[]{0, 0};
                if (!TextUtils.isEmpty(mHomeScoreView.getText()))
                {
                    score[0] = Integer.valueOf(mHomeScoreView.getText().toString());
                }
                if (!TextUtils.isEmpty(mAwayScoreView.getText()))
                {
                    score[1] = Integer.valueOf(mAwayScoreView.getText().toString());
                }
                if (mListener != null)
                {
                    mListener.onOkClicked(score);
                }
            }
        });

        return layout;
    }

    public interface ScoreDialogListener
    {
        void onOkClicked(final int[] score);
    }
}
