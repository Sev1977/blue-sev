package android.personal.fixtures.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 *
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener
{
    private static final String TAG = TimePickerFragment.class.getSimpleName();

    private TimeSelectedListener mListener;

    private long mTimestamp = 0L;

    public TimePickerFragment()
    {
        // Required empty public constructor
    }

    public void setTime(final long timestamp)
    {
        mTimestamp = timestamp;
    }

    public void setListener(final TimeSelectedListener listener)
    {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState)
    {
        final Calendar calendar = Calendar.getInstance();
        if (mTimestamp != 0L)
        {
            calendar.setTimeInMillis(mTimestamp);
        }
        final int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hourOfDay, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(final TimePicker timePicker, final int hourOfDay, final int minute)
    {
        Log.v(TAG, "onTimeSet");
        if (mListener != null)
        {
            mListener.onTimeSelected(hourOfDay, minute);
        }
    }

    public interface TimeSelectedListener
    {
        void onTimeSelected(final int hourOfDay, final int minute);
    }
}
