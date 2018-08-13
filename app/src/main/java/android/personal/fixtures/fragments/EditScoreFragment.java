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
    public static final String EXTRA_CARDIFF_SCORE = "cardiff_score";
    public static final String EXTRA_OPPONENT_SCORE = "opponent_score";

    private int mCardiffScore;
    private int mOpponentScore;
    private EditText mCardiffScoreView;
    private EditText mOpponentScoreView;

    private ScoreDialogListener mListener;

    public EditScoreFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param cardiffGoals Number of goals scored by Cardiff.
     * @param opponentGoals Number of goals scored by the opponent.
     * @return A new instance of fragment EditScoreFragment.
     */
    public static EditScoreFragment newInstance(int cardiffGoals, int opponentGoals)
    {
        final EditScoreFragment fragment = new EditScoreFragment();
        final Bundle args = new Bundle();
        args.putInt(EXTRA_CARDIFF_SCORE, cardiffGoals);
        args.putInt(EXTRA_OPPONENT_SCORE, opponentGoals);
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
            mCardiffScore = getArguments().getInt(EXTRA_CARDIFF_SCORE, 0);
            mOpponentScore = getArguments().getInt(EXTRA_OPPONENT_SCORE, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        final View layout = inflater.inflate(R.layout.fragment_edit_score, container, false);
        mCardiffScoreView = layout.findViewById(R.id.homeScoreInput);
        mCardiffScoreView.setText(String.valueOf(mCardiffScore));
        mOpponentScoreView = layout.findViewById(R.id.awayScoreInput);
        mOpponentScoreView.setText(String.valueOf(mOpponentScore));
        layout.findViewById(R.id.editScoreOkButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                final int[] score = new int[]{0, 0};
                if (!TextUtils.isEmpty(mCardiffScoreView.getText()))
                {
                    score[0] = Integer.valueOf(mCardiffScoreView.getText().toString());
                }
                if (!TextUtils.isEmpty(mOpponentScoreView.getText()))
                {
                    score[1] = Integer.valueOf(mOpponentScoreView.getText().toString());
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
