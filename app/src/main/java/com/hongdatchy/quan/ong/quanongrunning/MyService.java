package com.hongdatchy.quan.ong.quanongrunning;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.hongdatchy.quan.ong.quanongrunning.ui.curent_running.CurrentRunningFragment;

public class MyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        CurrentRunningFragment.getInstance().handelCreateRunning(CurrentRunningFragment.isGoBackRunning());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
