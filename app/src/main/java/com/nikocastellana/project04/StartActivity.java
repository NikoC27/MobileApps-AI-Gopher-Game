package com.nikocastellana.project04;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import java.util.Arrays;

public class StartActivity extends AppCompatActivity {

    // Matrix only in Ui thread
    // Tips:
    // Worker threads must use synchronization when accessing matrix
    // Never sleep UI thread

    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> {
            Intent startGame = new Intent(StartActivity.this, UiThread.class);
            startActivity(startGame);
        });
    }
}