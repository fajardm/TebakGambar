package com.example.fajarmawan.tebakgambar;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView karakterImageView;
    private Button cekButton;
    private TextView nyawaTextView;
    private TextView timerTextView;
    private Button tandaTantaButton;
    private EditText jawabEditText;
    private TextView scoreTextView;

    private String[] jawabanBenar;
    private int timer = 60000;
    private MyCount counter = new MyCount(timer, 1000);

    private Dialog gameOverDialog;
    private Dialog bantuanDialog;
    private Dialog laporanDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_game);

        nyawaTextView = (TextView) findViewById(R.id.textViewNyawa);
        nyawaTextView.setText(String.valueOf(Game.nyawa));

        karakterImageView = (ImageView) findViewById(R.id.imageViewKarakter);
        karakterImageView.setOnClickListener(this);
        jawabEditText = (EditText) findViewById(R.id.editTextJawaban);
        cekButton = (Button) findViewById(R.id.buttonCek);
        cekButton.setOnClickListener(this);
        timerTextView = (TextView) findViewById(R.id.textViewTimer);
        tandaTantaButton = (Button) findViewById(R.id.buttonTanya);
        tandaTantaButton.setOnClickListener(this);
        setKarakterImageView(loadImage(Game.karakterKe));
        scoreTextView = (TextView) findViewById(R.id.textViewScore);
        scoreTextView.setText(scoreTextView.getText() + " " + Game.score);

        dialogGameOver();
        dialogBantuan();
        dialogLaporan();

        counter.cancel();
        counter.start();
    }

    public void setKarakterImageView(Drawable karakterImageView) {
        this.karakterImageView.setImageDrawable(karakterImageView);
    }

    private void loadLevelLocal() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.fromLocalDatastore();
        try {
            Game.id = query.getFirst().getObjectId();
            Game.score = query.getFirst().getInt("score");
            Game.karakterKe = query.getFirst().getInt("karakterKe");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private Drawable loadImage(int index) {
        String name[] = null;
        try {
            name = getAssets().list("karakter");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (index > name.length) {
            goToMainMenu();
        } else {
            setJawabanBenar(name[index]);
        }

        InputStream ims = null;
        try {
            ims = getAssets().open("karakter/" + name[index]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable d = Drawable.createFromStream(ims, null);
        return d;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.buttonCek: {
                if (cekJawaban()) {
                    Game.karakterKe++;
                    Game.score = Game.score + Game.SCORE_;
                    if (Game.score % 50 == 0) {
                        Game.nyawa++;
                        Game.update(Game.score, Game.karakterKe, Game.nyawa);
                    } else {
                        Game.update(Game.score, Game.karakterKe, Game.nyawa);
                    }
                    setKarakterImageView(loadImage(Game.karakterKe));
                    jawabEditText.setText("");
                    nyawaTextView.setText(String.valueOf(Game.nyawa));
                    scoreTextView.setText("Score " + Game.score);
                    counter.cancel();
                    counter.start();
                } else {
                    Game.nyawa--;
                    Game.update(Game.score, Game.karakterKe, Game.nyawa);
                    if (Game.nyawa <= 0) {
                        gameOverDialog.show();
                    }
                    nyawaTextView.setText(String.valueOf(Game.nyawa));
                    counter.cancel();
                    counter.start();
                }
                break;
            }
            case R.id.buttonTanya: {
                bantuanDialog.show();
                break;
            }
            case R.id.imageViewKarakter: {
                laporanDialog.show();
                break;
            }
        }
    }

    private void dialogLaporan() {
        // custom dialog
        laporanDialog = new Dialog(this);
        laporanDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        laporanDialog.setContentView(R.layout.lapor_kesalahan);

        Button iyaButton = (Button) laporanDialog.findViewById(R.id.buttonKirimLaporan);
        final EditText laporan = (EditText) laporanDialog.findViewById(R.id.editTextLaporan);
        iyaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject gameScore = new ParseObject("Error");
                gameScore.put("type", 1);
                gameScore.put("karakter", Game.karakterKe);
                gameScore.put("description", String.valueOf(laporan.getText()));
                gameScore.saveInBackground();
                laporanDialog.hide();
            }
        });
    }

    private void dialogGameOver() {
        // custom dialog
        gameOverDialog = new Dialog(this);
        gameOverDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        gameOverDialog.setContentView(R.layout.game_over_dialog);

        Button iyaButton = (Button) gameOverDialog.findViewById(R.id.buttonGameOverIya);
        iyaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game.update(Game.score, 0, 3);
                setKarakterImageView(loadImage(Game.karakterKe));
                nyawaTextView.setText(String.valueOf(Game.nyawa));
                gameOverDialog.hide();
            }
        });

        Button tidakButton = (Button) gameOverDialog.findViewById(R.id.buttonGameOverTidak);
        tidakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game.update(Game.score, 0, 3);
                goToMainMenu();
            }
        });
    }

    private String getBantuan() {
        int random = random(0, jawabanBenar.length);
        String bantuan = jawabanBenar[random];
        String result = "";
        if (bantuan.length() > 1) {
            for (int i = 0; i < bantuan.length(); i++) {
                if (Math.random() < 0.5) {
                    result += bantuan.charAt(i) + " ";
                } else {
                    result += "_ ";
                }
            }
        }
        return result;
    }

    private int random(int min, int max) {
        Random rand = new Random();
        int random = rand.nextInt((max - min)) + min;
        return random;
    }

    private void dialogBantuan() {
        // custom dialog
        bantuanDialog = new Dialog(this);
        bantuanDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bantuanDialog.setContentView(R.layout.bantua_dialog);

        Button iyaButton = (Button) bantuanDialog.findViewById(R.id.buttonBantuanOk);
        TextView bantuan = (TextView) bantuanDialog.findViewById(R.id.textViewBantuan);
        bantuan.setText(getBantuan());

        iyaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bantuanDialog.hide();
            }
        });
    }

    private void goToMainMenu() {
        Intent mainActivity = new Intent(GameActivity.this, MainActivity.class);
        GameActivity.this.finish();
        startActivity(mainActivity);
    }

    private void setJawabanBenar(String karakter) {
        String splitDot = karakter.replaceAll("\\s+", "").split("\\.")[1];
        String splitComma[] = splitDot.split("\\,");
        this.jawabanBenar = splitComma;
    }

    private boolean cekJawaban() {
        boolean jawaban = false;
        String jawab = jawabEditText.getText().toString();

        for (int i = 0; i < jawabanBenar.length; i++) {
            System.out.println(jawabanBenar[i] + " - " + jawab.replaceAll("\\s+", ""));
            if (jawabanBenar[i].equals(jawab.replaceAll("\\s+", ""))) {
                jawaban = true;
                break;
            }

        }

        return jawaban;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Ingin kembali ke menu utama?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        goToMainMenu();
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
    }

    // countdowntimer is an abstract class, so extend it and fill in methods
    public class MyCount extends CountDownTimer {

        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            Game.nyawa--;
            Game.update(Game.score, Game.karakterKe, Game.nyawa);
            if (Game.nyawa <= 0) {
                counter.cancel();
                gameOverDialog.show();
            } else {
                nyawaTextView.setText(String.valueOf(Game.nyawa));
                counter.cancel();
                counter.start();
            }

        }

        @Override
        public void onTick(long millisUntilFinished) {
            long tim = millisUntilFinished / 1000;
            timerTextView.setText(String.valueOf(tim));
        }
    }

}
