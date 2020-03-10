package android.personal.fixtures.fragments;

import android.os.Bundle;
import android.personal.fixtures.R;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

/**
 * Use the {@link EditScoreFragment#newInstance} factory method to create an instance of this
 * fragment.
 */
public class EditScoreFragment extends DialogFragment
{
    public static final String EXTRA_CARDIFF_SCORE = "cardiff_score";
    public static final String EXTRA_OPPONENT_SCORE = "opponent_score";

    private int mCardiffScore = 0;
    private int mOpponentScore = 0;
    private NumberPicker mCardiffScorePicker;
    private NumberPicker mOpponentScorePicker;

    private ScoreDialogListener mListener;

    public EditScoreFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param cardiffGoals  Number of goals scored by Cardiff.
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
        mCardiffScorePicker = layout.findViewById(R.id.cardiffScorePicker);
        mCardiffScorePicker.setMinValue(0);
        mCardiffScorePicker.setMaxValue(10);
        mCardiffScorePicker.setValue(mCardiffScore);
        mOpponentScorePicker = layout.findViewById(R.id.opponentScorePicker);
        mOpponentScorePicker.setMinValue(0);
        mOpponentScorePicker.setMaxValue(10);
        mOpponentScorePicker.setValue(mOpponentScore);
        layout.findViewById(R.id.editScoreOkButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                if (mListener != null)
                {
                    mListener.onOkClicked(mCardiffScorePicker.getValue(),
                            mOpponentScorePicker.getValue());
                }
            }
        });

        return layout;
    }

    public interface ScoreDialogListener
    {
        void onOkClicked(final int cardiffScore, final int opponentScore);
    }
}
