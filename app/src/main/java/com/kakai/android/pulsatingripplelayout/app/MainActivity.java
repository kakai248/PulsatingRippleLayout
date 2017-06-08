package com.kakai.android.pulsatingripplelayout.app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.kakai.android.pulsatingripplelayout.PulsatingRippleLayout;

public class MainActivity extends AppCompatActivity {

    private boolean color = false;
    private boolean type = false;
    private boolean playing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final PulsatingRippleLayout ripple = (PulsatingRippleLayout) findViewById(R.id.content);
        Button btnColor = (Button) findViewById(R.id.btnColor);
        Button btnType = (Button) findViewById(R.id.btnType);
        Button btnPlay = (Button) findViewById(R.id.btnPlay);

        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ripple.setRippleColor(Color.parseColor(color ? "#EA524B" : "#C7C7C7"));
                color = !color;
            }
        });

        btnType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ripple.setRippleType(type ? PulsatingRippleLayout.RIPPLE_STROKE : PulsatingRippleLayout.RIPPLE_FILL);
                type = !type;
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing) {
                    ripple.stopRippleAnimation();
                } else {
                    ripple.startRippleAnimation();
                }
                playing = !playing;
            }
        });
    }
}
