package weather.geektam.com.calendartest;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final String tag = MainActivity.class.getSimpleName();

    Calendar selectedCalenar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_test01).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDialog dialog = CalendarDialog.newInstance(selectedCalenar);
                dialog.setOnDateSelectedListener((d, date) -> {
                    Log.d(tag, "onDateSelected()" + date);
                    selectedCalenar.setTimeInMillis(date.getTime());
                    dialog.dismiss();
                });
                dialog.show(getFragmentManager(), CalendarDialog.class.getSimpleName());
            }
        });
    }
}
