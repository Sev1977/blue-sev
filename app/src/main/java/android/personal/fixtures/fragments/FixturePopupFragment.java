package android.personal.fixtures.fragments;

import android.os.Bundle;
import android.personal.fixtures.R;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

/**
 * Use the {@link FixturePopupFragment#newInstance} factory method to create an instance of this
 * fragment.
 */
public class FixturePopupFragment extends DialogFragment
{
    public static final String EXTRA_FIXTURE_ID = "fixture_id";

    private long mFixtureId = 0;

    public FixturePopupFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param fixtureId ID of the fixture to be viewed.
     * @return A new instance of fragment FixturePopupFragment.
     */
    public static FixturePopupFragment newInstance(long fixtureId)
    {
        final FixturePopupFragment fragment = new FixturePopupFragment();
        final Bundle args = new Bundle();
        args.putLong(EXTRA_FIXTURE_ID, fixtureId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mFixtureId = getArguments().getInt(EXTRA_FIXTURE_ID, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        final View layout = inflater.inflate(R.layout.fragment_fixture_popup, container, false);
        mCardiffScorePicker = layout.findViewById(R.id.cardiffScorePicker);
        mOpponentScorePicker = layout.findViewById(R.id.opponentScorePicker);
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
}
