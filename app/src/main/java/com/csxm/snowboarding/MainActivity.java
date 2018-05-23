package com.csxm.snowboarding;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SnowboardingView waveView = findViewById(R.id.waveView);
        waveView.startAnimator() ;
    }
}
