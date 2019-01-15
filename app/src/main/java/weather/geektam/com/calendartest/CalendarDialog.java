package weather.geektam.com.calendartest;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class CalendarDialog extends DialogFragment {

    private static final String TAG = "CalendarDialog";

    private static final String SELECTED_CALENDAR = "SELECTED_CALENDAR";

    Unbinder unbinder;
    @BindView(R.id.calendar_view)
    CalendarView mCalendarView;

    private Calendar mSelectedCalendar;

    private OnDateSelectedListener mListener;

    public static CalendarDialog newInstance(Calendar calendar) {
        CalendarDialog dialog = new CalendarDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_CALENDAR, calendar);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSelectedCalendar = (Calendar) getArguments().getSerializable(SELECTED_CALENDAR);

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_calendar, container, false);
        unbinder = ButterKnife.bind(this, view);

        mCalendarView.setCalendar(mSelectedCalendar);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(unbinder!=null) unbinder.unbind();
    }

    @OnClick(R.id.negative_button)
    void onClickNegativeButton(View v) {
        dismissAllowingStateLoss();
    }

    @OnClick(R.id.positive_button)
    void onClickPositiveButton(View v) {
        final Calendar selectedCalendar = mCalendarView.getCalendar();

        if (selectedCalendar != null && mSelectedCalendar != null && mListener != null &&
                !Utils.isSameDay(selectedCalendar, mSelectedCalendar)) {
            mListener.onDateSelected(this, selectedCalendar.getTime());
        }

        dismissAllowingStateLoss();
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        mListener = listener;
    }

    public interface OnDateSelectedListener {
        void onDateSelected(CalendarDialog dialog, Date date);
    }
}
