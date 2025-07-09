package com.example.alarmclock;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    TextView clockText, dateText;
    EditText timeInput;
    CheckBox activeCheck;
    Button setButton;
    LinearLayout alarmList;
    Handler handler = new Handler();
    MediaPlayer beep;

    static class Alarm {
        String time;
        boolean active;
        boolean triggered = false;
        Alarm(String t, boolean a) {
            time = t;
            active = a;
        }
    }

    List<Alarm> alarms = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout programmatically (no XML)
        ScrollView scroll = new ScrollView(this);
        LinearLayout base = new LinearLayout(this);
        base.setOrientation(LinearLayout.VERTICAL);
        base.setPadding(40, 40, 40, 40);
        scroll.addView(base);

        clockText = new TextView(this);
        clockText.setTextSize(36);
        clockText.setText("00:00:00");
        base.addView(clockText);

        dateText = new TextView(this);
        dateText.setTextSize(16);
        base.addView(dateText);

        timeInput = new EditText(this);
        timeInput.setHint("Set Alarm (HH:mm)");
        timeInput.setInputType(InputType.TYPE_CLASS_DATETIME);
        base.addView(timeInput);

        activeCheck = new CheckBox(this);
        activeCheck.setText("Activate Alarm");
        base.addView(activeCheck);

        setButton = new Button(this);
        setButton.setText("Set Alarm");
        base.addView(setButton);

        alarmList = new LinearLayout(this);
        alarmList.setOrientation(LinearLayout.VERTICAL);
        base.addView(alarmList);

        setContentView(scroll);

        beep = MediaPlayer.create(this, R.raw.beep); // Place beep.mp3 in res/raw/

        setButton.setOnClickListener(v -> {
            String time = timeInput.getText().toString().trim();
            if (!time.matches("^([01]?\\d|2[0-3]):[0-5]\\d$")) {
                Toast.makeText(this, "Invalid format (HH:mm)", Toast.LENGTH_SHORT).show();
                return;
            }

            Alarm alarm = new Alarm(time, activeCheck.isChecked());
            alarms.add(alarm);
            TextView view = new TextView(this);
            view.setText("⏰ " + time + (alarm.active ? " [Active]" : " [Inactive]"));
            view.setTextSize(16);
            alarmList.addView(view);
            timeInput.setText("");
            activeCheck.setChecked(false);
        });

        updateClock();
    }

    void updateClock() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Date now = new Date();
                String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(now);
                String hhmm = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(now);
                String date = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault()).format(now);

                clockText.setText(time);
                dateText.setText(date);

                for (Alarm a : alarms) {
                    if (a.active && !a.triggered && a.time.equals(hhmm)) {
                        a.triggered = true;
                        if (!beep.isPlaying()) beep.start();
                        Toast.makeText(MainActivity.this, "⏰ Alarm! " + a.time, Toast.LENGTH_LONG).show();
                    }
                }

                handler.postDelayed(this, 1000);
            }
        }, 0);
    }
}
