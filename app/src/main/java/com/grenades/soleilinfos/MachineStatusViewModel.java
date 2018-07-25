/*
 * Created by Greg VIGUIER on 18/07/18 16:34
 * Last modified 18/07/18 15:59
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.grenades.soleilinfos;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import com.grenades.soleilinfos.util.SoleilInfosHandlerThread;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MachineStatusViewModel extends ViewModel {

    private final MutableLiveData<Bitmap> image = new MutableLiveData<>();

    private static final String IMAGE_URL = "https://www.synchrotron-soleil.fr/sites/default/files/WebInterfaces/machinestatus/MachineStatus-extranet.png";
    private static final int CONNECTION_TIME_OUT = 10000;
    private static final int REFRESH_STEP = 2;
    private static final int REFRESH_PERIOD = 60;
    private final MutableLiveData<Integer> timeBeforeRefreshLiveData = new MutableLiveData<>();
    private int timeBeforeRefresh = 0;
    private SoleilInfosHandlerThread loadDataThread;

    public MachineStatusViewModel() {
        new Timer().schedule(new MyTimerTask(), 500, 2000);

        loadDataThread = new SoleilInfosHandlerThread("Refresh Data Thread");
        loadDataThread.start();
        loadDataThread.prepareHandler();
    }

    @Override
    protected void onCleared() {
        loadDataThread.quit();
        super.onCleared();
    }

    public MutableLiveData<Bitmap> getImage() {
        return image;
    }

    public MutableLiveData<Integer> getTimeBeforeRefresh() {
        return timeBeforeRefreshLiveData;
    }

    public void refreshDataAsync() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                loadData(IMAGE_URL);
                timeBeforeRefresh = REFRESH_PERIOD;
            }
        };
        loadDataThread.postTask(runnable);
    }

    @Nullable
    private Bitmap loadData(String urlToLoad) {
        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        try {
            int i = 0;
            URL url = new URL(urlToLoad);

            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECTION_TIME_OUT);
            connection.setReadTimeout(CONNECTION_TIME_OUT);
            InputStream inputStream = connection.getInputStream();

            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            // bitmap is already null
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

        }
        return bitmap;
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            timeBeforeRefresh -= REFRESH_STEP;
            timeBeforeRefreshLiveData.postValue(timeBeforeRefresh);
            if (timeBeforeRefresh <= 0) {
                timeBeforeRefresh = REFRESH_PERIOD;
                image.postValue(loadData(IMAGE_URL));
            }
        }
    }
}