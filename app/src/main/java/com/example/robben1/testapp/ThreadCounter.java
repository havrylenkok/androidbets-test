package com.example.robben1.testapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.util.Random;

class ThreadCounter extends Thread {

    public static final int FPS = 1;

    private final int THREAD_ID;
    private final Handler HANDLER;
    private final int MIN_MOVE = 10;
    private final int MAX_MOVE = 100;
    private Random r;
    private boolean isRunning = false;

    ThreadCounter(int threadId, Handler handler) {
        this.THREAD_ID = threadId;
        this.HANDLER = handler;
        this.r = new Random();
    }

    @Override
    public void run() {
        isRunning = true;
        while(isRunning) {
            try {
                int px = r.nextInt(MAX_MOVE) + MIN_MOVE;
                final Bundle b = new Bundle();
                b.putInt("id", THREAD_ID);
                b.putInt("px", px);
                final Message msg = new Message();
                msg.setData(b);
                HANDLER.sendMessage(msg);
                System.out.println(THREAD_ID + " move " + px + " right");
                Thread.sleep(FPS * 500);
            } catch (Exception e) {
                System.err.println("FIX YOUR BUGS ALREADY: " + e);
            }
        }
    }

    public synchronized void stopDoingWatchaDoing() {
        isRunning = false;
    }
}
