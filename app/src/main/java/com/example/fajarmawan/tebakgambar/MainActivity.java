package com.example.fajarmawan.tebakgambar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonMain;
    private TextView scoreTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_main);

        buttonMain = (Button) findViewById(R.id.buttonMain);
        buttonMain.setOnClickListener(this);

        scoreTextView = (TextView) findViewById(R.id.textViewScoreMain);
        scoreTextView.setText("Score " + Game.score);
    }

    private void gotoGameActivity() {
        Intent gameActivity = new Intent(this, GameActivity.class);
        MainActivity.this.finish();
        startActivity(gameActivity);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonMain: {
                this.gotoGameActivity();
                break;
            }

        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Yakin ingin keluar?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
    }
}
