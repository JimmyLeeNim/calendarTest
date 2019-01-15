package weather.geektam.com.calendartest;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CalendarView extends LinearLayout {

    private static final String TAG = CalendarDialog.class.getSimpleName();

    // how many days to show
    private static final int DAYS_COUNT = 42; // 7 days per week x 6 weeks = 42 days

    // default date format
    private static final String DATE_FORMAT = "MMMM yyyy";

    private final View mPrevButton;
    private final View mNextButton;
    private final TextView mHeader;
    private final GridView mGridView;

    private CalendarAdapter mAdapter;

    private Calendar mSelectedCalendar;
    private Calendar mCurrentCalendar;

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.control_calendar, this, true);

        mPrevButton = findViewById(R.id.prev_button);
        mNextButton = findViewById(R.id.next_button);
        mHeader = (TextView) findViewById(R.id.header);
        mGridView = (GridView) findViewById(R.id.grid);

        mSelectedCalendar = Calendar.getInstance();
        mCurrentCalendar = (Calendar) mSelectedCalendar.clone();

        mPrevButton.setOnClickListener(v -> {
            mCurrentCalendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        mNextButton.setOnClickListener(v -> {
            mCurrentCalendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        mAdapter = new CalendarAdapter(context, getCalendarList(mCurrentCalendar));

        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener((parent, view1, position, id) -> {
            //Log.v(TAG, "onItemClick(...)");

            Calendar selectedCalendar = (Calendar) parent.getItemAtPosition(position);

            if (!isDayAvailable(selectedCalendar)) {
                // not selected
                return;
            }

            mSelectedCalendar = (Calendar) selectedCalendar.clone();
            mAdapter.notifyDataSetChanged();
        });
    }

    public Calendar getCalendar() {
        return mSelectedCalendar;
    }

    public void setCalendar(Calendar calendar) {
        Log.d(TAG, "setCalendar(...)");

        if (calendar == null) {
            calendar = Calendar.getInstance();
        }

        mSelectedCalendar = (Calendar) calendar.clone();
        mCurrentCalendar = (Calendar) calendar.clone();

        updateCalendar();
    }

    private void updateCalendar() {

        mAdapter.clear();
        mAdapter.addAll(getCalendarList(mCurrentCalendar));
        mAdapter.notifyDataSetChanged();

        mHeader.setText(new SimpleDateFormat(DATE_FORMAT, Locale.US).format(mCurrentCalendar.getTime()));

        mPrevButton.setVisibility(isPrevButtonAvailable() ? View.VISIBLE : View.INVISIBLE);
        mNextButton.setVisibility(isNextButtonAvailable() ? View.VISIBLE : View.INVISIBLE);
    }

    private List<Calendar> getCalendarList(Calendar baseCalendar) {
        Calendar calendar = (Calendar) baseCalendar.clone();

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        List<Calendar> calendarList = new ArrayList<>();

        while (calendarList.size() < DAYS_COUNT) {
            calendarList.add((Calendar) calendar.clone());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return calendarList;
    }

    private boolean isPrevButtonAvailable() {
        final Calendar today = Calendar.getInstance();

        // 화면에 보여지고 있는 month 가 이미 이전 month 일 경우
        if (today.after(mCurrentCalendar)) {
            return false;
        }

        final Calendar weekEarlier = (Calendar) today.clone();
        weekEarlier.add(Calendar.DAY_OF_YEAR, -7);

        final Calendar firstDay = mAdapter.getItem(0);

        return firstDay.after(weekEarlier);
    }

    private boolean isNextButtonAvailable() {
        final Calendar today = Calendar.getInstance();

        // 화면에 보여지고 있는 month 가 이미 다음 month 일 경우
        if (today.before(mCurrentCalendar)) {
            return false;
        }

        final Calendar weekLater = (Calendar) today.clone();
        weekLater.add(Calendar.DAY_OF_YEAR, 7);

        final Calendar lastDay = mAdapter.getItem(DAYS_COUNT - 1);

        return lastDay.before(weekLater);
    }

    private boolean isDayAvailable(@NonNull Calendar calendar) {
        final Calendar today = Calendar.getInstance();
        final Calendar weekEarlier = (Calendar) today.clone();
        weekEarlier.add(Calendar.DAY_OF_YEAR, -7);
        final Calendar weekLater = (Calendar) today.clone();
        weekLater.add(Calendar.DAY_OF_YEAR, 7);

        return calendar.after(weekEarlier) && calendar.before(weekLater);
    }

    class CalendarAdapter extends ArrayAdapter<Calendar> {

        @BindView(R.id.today)
        View todayView;
        @BindView(R.id.day)
        TextView dayView;

        public CalendarAdapter(Context context, List<Calendar> calendarList) {
            super(context, R.layout.control_calendar_day, calendarList);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            //Log.v(TAG, "getView(...)");

            Context context = parent.getContext();

            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.control_calendar_day, parent, false);
            }

            ButterKnife.bind(this, view);

            final Calendar calendar = getItem(position);
            final Calendar today = Calendar.getInstance();

            todayView.setVisibility(Utils.isSameDay(calendar, today) ? View.VISIBLE : View.GONE);

            final int colorResource;
            final boolean isClickable;

            if (isDayAvailable(calendar)) {
                colorResource = R.color.calenar_item_active;
                isClickable = false;
            } else {
                colorResource = R.color.calenar_item_inactive;
                isClickable = true;
            }

            final Drawable backgroundDrawable;
            if (Utils.isSameDay(calendar, mSelectedCalendar)) {
                backgroundDrawable = new ColorDrawable(ContextCompat.getColor(context, R.color.calenar_item_selected));
            } else {
                backgroundDrawable = null;
            }

            dayView.setTextColor(ContextCompat.getColor(context, colorResource));
            dayView.setBackground(backgroundDrawable);
            dayView.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));

            // GridView 에서 이벤트를 처리를 막기위해 GridView 개별 아이템을 clickable 로 설정.
            // TODO 이 부분이 tricky 한데, 확인 필요.
            view.setClickable(isClickable);

            return view;
        }
    }
}
