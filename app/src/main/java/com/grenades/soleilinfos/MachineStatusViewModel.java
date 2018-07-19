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
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MachineStatusViewModel extends ViewModel {

    private MutableLiveData<Bitmap> image = new MutableLiveData<>();

    private static String IMAGE_URL = "https://www.synchrotron-soleil.fr/sites/default/files/WebInterfaces/machinestatus/MachineStatus-extranet.png";
    private static int TIME_OUT = 10000;
    private MutableLiveData<Integer> timeBeforeRefreshLiveData = new MutableLiveData<>();
    private int timeBeforeRefresh = 0;

    public MachineStatusViewModel() {
        new Timer().schedule(new MyTimerTask(), 500, 2000);
    }

    public MutableLiveData<Bitmap> getImage() {
        return image;
    }

    public MutableLiveData<Integer> getTimeBeforeRefresh() {
        return timeBeforeRefreshLiveData;
    }

    class MyTimerTask extends TimerTask {
        Handler handler = new Handler();

        @Override
        public void run() {
            timeBeforeRefresh -= 2;
            timeBeforeRefreshLiveData.postValue(timeBeforeRefresh);
            if (timeBeforeRefresh <= 0) {
                timeBeforeRefresh = 60;
                handler.post(new Runnable() {
                    public void run() {
                        MyTask performBackgroundTask = new MyTask();
                        performBackgroundTask.execute(IMAGE_URL);
                    }
                });
            }
        }
    }

    class MyTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urlToLoads) {

            Bitmap bitmap = null;
            try {
                int i = 0;
                URL url = new URL(urlToLoads[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setConnectTimeout(TIME_OUT);
                connection.setReadTimeout(TIME_OUT);
                InputStream inputStream = connection.getInputStream();

                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                Log.e("TAG", "doInBackground: ", e);
                // bitmap is already null
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            image.setValue(bitmap);
        }
    }
}