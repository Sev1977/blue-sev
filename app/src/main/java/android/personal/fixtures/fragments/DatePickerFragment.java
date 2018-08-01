package android.personal.fixtures.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 *
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{
    private static final String TAG = DatePickerFragment.class.getSimpleName();

    private DateSelectedListener mListener;

    private long mTimestamp = 0L;

    public DatePickerFragment()
    {
        // Required empty public constructor
    }

    public void setDate(final long timestamp)
    {
        mTimestamp = timestamp;
    }

    public void setListener(final DateSelectedListener listener)
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
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(final DatePicker datePicker, final int year, final int month,
            final int day)
    {
        Log.v(TAG, "onDateSet");
        if (mListener != null)
        {
            mListener.onDateSelected(year, month, day);
        }
    }

    public interface DateSelectedListener
    {
        void onDateSelected(final int year, final int month, final int day);
    }
}
