package com.grenades.soleilinfos.util;

import android.os.Handler;
import android.os.HandlerThread;

public class SoleilInfosHandlerThread extends HandlerThread {
    private Handler mWorkerHandler;

    public SoleilInfosHandlerThread(String name) {
        super(name);
    }

    public void postTask(Runnable task) {
        mWorkerHandler.post(task);
    }

    public void prepareHandler() {
        mWorkerHandler = new Handler(getLooper());
    }
}


