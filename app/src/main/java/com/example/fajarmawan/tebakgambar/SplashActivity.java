package com.example.fajarmawan.tebakgambar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.parse.CountCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by fajarmawan on 1/11/16.
 */
public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        setContentView(R.layout.activity_splash_screen);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.fromLocalDatastore();
        query.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    if (count == 0) {
                        ParseObject game = new ParseObject("Game");
                        game.put("score", 0);
                        game.put("karakterKe", 0);
                        game.put("nyawa", 3);
                        game.pinInBackground();

                        Game.load();
                    } else {
                        Game.load();
                    }
                } else {
                    Log.e("NULL", e.getMessage());
                }
            }
        });

        new Handler().postDelayed(new Runnable() {

         /*
          * Showing splash screen with a timer. This will be useful when you
          * want to show case your app logo / company
          */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
