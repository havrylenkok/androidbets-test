package com.example.robben1.testapp;

import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final int runNum = 3;
    ImageView[] imageViews = new ImageView[runNum];
    ThreadCounter[] threadCounters = new ThreadCounter[runNum];
    int finishX;
    int tries = 0;
    int playersBet = 0;
    int progress = 13;
    int betSize = 500;
    double rate = 2.3;
    boolean doWeHaveWinner = false;
    String betOnTextString;
    String tryTextString;
    String lastWinnerTextString;
    boolean doWeHaveBet = false;
    boolean isFirstTry = true;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(doWeHaveWinner) return;
            Bundle b = msg.getData();
            moveImageView(b.getInt("id"), b.getInt("px"));
        }
    };

    private void moveImageView(int id, int px) {
        System.out.println("moving " + id + " on " + px);
        System.out.println(imageViews[id].getX());

        imageViews[id].animate().x(imageViews[id].getX() + px).start();
        System.out.println(imageViews[id].getX());
        checkWinner();

    }

    private void checkWinner() {
        for (int i = 0; i < imageViews.length; i++) {
            if(imageViews[i].getX() + imageViews[i].getWidth() > finishX) {
                foundWinner(i);
            }
        }
    }

    private void foundWinner(int id) {
        doWeHaveWinner = true;
        TextView tv = (TextView)findViewById(R.id.winnerText);
        String s = lastWinnerTextString + " " + (id + 1);
        tv.setText(s);
        System.out.println("winner " + id);
        System.out.println("found winner");
        for(ThreadCounter tc : threadCounters) {
            tc.stopDoingWatchaDoing();
        }
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i].clearAnimation();
            imageViews[i].setX(0);
        }
        if(playersBet == id) {
            System.out.println("BET IS RIGHT");
            ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar);
            System.out.println("Progress: " + progress);
            pb.setProgress(pb.getProgress() + progress);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        foundWinner(0);
    }

    public void onClick(View v) {
        if(!doWeHaveWinner && !isFirstTry) return;
        doWeHaveBet = true;
        int id = 0;
        for (int i = 0; i < imageViews.length; i++) {
            if(imageViews[i].getId() == v.getId()) {
                id = i;
            }
        }
        TextView tv = (TextView)findViewById(R.id.betOnText);
        String s = betOnTextString + " " + (id+1);
        tv.setText(s);

        playersBet = id;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//deprecated        finishX = getWindowManager().getDefaultDisplay().getWidth();
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        finishX = size.x - size.x / 11;

        Button buttonGo = (Button) findViewById(R.id.buttonGo);
        buttonGo.setOnClickListener(new HandleClickStart());

        for (int i = 0; i < imageViews.length; i++) {
            String viewId = "imageView" + (i + 1);
            int resId = getResources().getIdentifier(viewId, "id", getPackageName());
            imageViews[i] = (ImageView) findViewById(resId);
            System.out.println(imageViews[i].getId());
            imageViews[i].setImageResource(R.drawable.first);
            imageViews[i].setOnClickListener(this);
        }

        TextView tryText = (TextView)findViewById(R.id.tryText);
        tryTextString = tryText.getText().toString();
        TextView betOn = (TextView)findViewById(R.id.betOnText);
        betOnTextString = betOn.getText().toString();
        TextView lastWinnerText = (TextView)findViewById(R.id.winnerText);
        lastWinnerTextString = lastWinnerText.getText().toString();

    }

    private class HandleClickStart implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(!doWeHaveBet) return;
            if(!doWeHaveWinner && !isFirstTry) return;
            isFirstTry = false;


            TextView tv = (TextView)findViewById(R.id.tryText);
            String s = tryTextString + " " + (++tries);
            tv.setText(s);

            for (int i = 0; i < imageViews.length; i++) {
                imageViews[i].setX(0);
            }
            doWeHaveWinner = false;
            System.out.println("finish x: " + finishX);
            for (int i = 0; i < imageViews.length; i++) {
                System.out.println("create thread");
                threadCounters[i] = new ThreadCounter(i, handler);
                threadCounters[i].start();
            }
        }
    }
}
