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

package com.grenades.soleilinfos.fragments;

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
    private static final String IMAGE_URL = "https://www.synchrotron-soleil.fr/sites/default/files/WebInterfaces/machinestatus/MachineStatus-extranet.png";
    private static final int CONNECTION_TIME_OUT = 10000;
    private static final int REFRESH_STEP = 1;
    private static final int REFRESH_PERIOD = 60;

    private final MutableLiveData<Integer> timeBeforeRefreshLiveData = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> image = new MutableLiveData<>();

    private int timeBeforeRefresh = 0;
    private final SoleilInfosHandlerThread loadDataThread;

    public MachineStatusViewModel() {
        new Timer().schedule(new RefreshImageTimerTask(), 500, 2000);

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
                loadData();
                timeBeforeRefresh = REFRESH_PERIOD;
            }
        };
        loadDataThread.postTask(runnable);
    }

    /**
     * Load Data from SOLEIL's Web SiteZ
     *
     * @return the loaded bitmap
     */
    @Nullable
    private Bitmap loadData() {
        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(IMAGE_URL);

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

    private class RefreshImageTimerTask extends TimerTask {
        @Override
        public void run() {
            // Publish timeBeforeRefresh for UI progressBar
            timeBeforeRefresh -= REFRESH_STEP;
            timeBeforeRefreshLiveData.postValue(timeBeforeRefresh);
            if (timeBeforeRefresh <= 0) {
                // Let's refresh data
                timeBeforeRefresh = REFRESH_PERIOD;
                image.postValue(loadData());
            }
        }
    }
}