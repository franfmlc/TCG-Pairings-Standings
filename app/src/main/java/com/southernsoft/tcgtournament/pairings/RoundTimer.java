package com.southernsoft.tcgtournament.pairings;

import android.os.CountDownTimer;

import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

public class RoundTimer {
    private MutableLiveData<Integer> roundTime;
    private CountDownTimer timer;
    int remainingTime;

    @Inject
    public RoundTimer() {}

    public void start(MutableLiveData<Integer> mutableRoundTime, int secondsInFuture, int countDownInterval) {
        roundTime = mutableRoundTime;
        remainingTime = secondsInFuture;
        timer = new CountDownTimer(secondsInFuture * 1000, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                roundTime.setValue(--remainingTime);
            }

            @Override
            public void onFinish() {
                if (roundTime.getValue() > 0)
                    roundTime.setValue(--remainingTime);
            }
        };
        timer.start();
    }

    public void cancel() {
        if (timer != null)
            timer.cancel();
    }
}