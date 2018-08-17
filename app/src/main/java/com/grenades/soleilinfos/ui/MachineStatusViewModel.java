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

package com.grenades.soleilinfos.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;

import com.grenades.soleilinfos.R;
import com.grenades.soleilinfos.data.SoleilInfosRepository;
import com.grenades.soleilinfos.util.SoleilInfosHandlerThread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class MachineStatusViewModel extends AndroidViewModel implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final static int PROGRESS_BAR_REFRESH_PERIOD = 2;
    public static final int DEFAULT_REFRESH_PERIOD = 60;
    private final MutableLiveData<Boolean> autoRefresh = new MutableLiveData<>();
    private final MutableLiveData<Integer> refreshPeriod = new MutableLiveData<>();
    private final MutableLiveData<Integer> timeBeforeRefreshLiveData = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> image = new MutableLiveData<>();

    private SharedPreferences preferences;
    private String key_auto_refresh;
    private String key_refresh_period;

    private int timeBeforeRefresh = 0;
    private SoleilInfosHandlerThread loadDataThread;
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture future;

    public MachineStatusViewModel(Application application) {
        super(application);

        // Preferences values are going to setup the way the data is refreshed.
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplication().getApplicationContext());
        preferences.registerOnSharedPreferenceChangeListener(this);
        loadPreferences();

        prepareAndRunThreads();
        refreshDataAsync();
    }

    private void loadPreferences() {
        PreferenceManager.setDefaultValues(getApplication().getApplicationContext(), R.xml.preferences, false);
        Application app = getApplication();
        if (app != null) {
            Context context = app.getApplicationContext();
            key_auto_refresh = context.getResources().getString(R.string.key_preference_auto_refresh_machine_status);
            key_refresh_period = context.getResources().getString(R.string.key_preference_machine_status_refresh_period);
            fetchPreferencesValues();
        }
    }

    private void fetchPreferencesValues() {
        // Default values from preferences.xml will be used so we don't care about the second argument
        autoRefresh.setValue(preferences.getBoolean(key_auto_refresh, false));
        String refreshPeriodStr = preferences.getString(key_refresh_period, "");
        int refreshPeriodValue = DEFAULT_REFRESH_PERIOD;
        try {
            refreshPeriodValue = Integer.valueOf(refreshPeriodStr);
        } catch (NumberFormatException nfe) {

        }
        refreshPeriod.setValue(refreshPeriodValue);
        timeBeforeRefreshLiveData.setValue(refreshPeriodValue);
        timeBeforeRefresh = refreshPeriodValue;
    }

    private void prepareAndRunThreads() {
        // Automatic Image reloading is done via un scheduled thread
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduleRefreshTaskIfNeeded();

        // Create the Handler Thread used when the user forces a refresh
        loadDataThread = new SoleilInfosHandlerThread("Refresh Data Thread");
        loadDataThread.start();
        loadDataThread.prepareHandler();
    }

    private void updateRefreshThread() {
        if (future != null && !future.isDone()) {
            future.cancel(true);
        }
        scheduleRefreshTaskIfNeeded();
    }

    private void scheduleRefreshTaskIfNeeded() {
        if (autoRefresh.getValue()) {
            // This thread (RefreshImageTask) is scheduled every PROGRESS_BAR_REFRESH_PERIOD second
            // but will effectively load the data each user defined refresh period.
            //
            // We schedule this every second to update the progressbar UI.
            future = scheduledExecutorService.scheduleAtFixedRate(new RefreshImageTask(), 1, PROGRESS_BAR_REFRESH_PERIOD, TimeUnit.SECONDS);
        }
    }

    @Override
    protected void onCleared() {
        loadDataThread.quit();
        scheduledExecutorService.shutdown();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onCleared();
    }

    public MutableLiveData<Bitmap> getImage() {
        return image;
    }

    public MutableLiveData<Integer> getTimeBeforeRefresh() {
        return timeBeforeRefreshLiveData;
    }

    public MutableLiveData<Integer> getRefreshPeriod() {
        return refreshPeriod;
    }

    public void refreshDataAsync() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                loadImage();
                timeBeforeRefresh = refreshPeriod.getValue();
            }
        };
        loadDataThread.postTask(runnable);
    }

    /**
     * Load Data from SOLEIL's Web Site
     *
     * @return the loaded bitmap
     */
    @Nullable
    private void loadImage() {
        image.postValue(SoleilInfosRepository.getInstance().getImage());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        fetchPreferencesValues();
        updateRefreshThread();
    }

    private class RefreshImageTask implements Runnable {
        @Override
        public void run() {
            if (autoRefresh.getValue()) {
                // Publish timeBeforeRefresh for UI progressBar
                timeBeforeRefresh -= PROGRESS_BAR_REFRESH_PERIOD;
                timeBeforeRefreshLiveData.postValue(timeBeforeRefresh);
                if (timeBeforeRefresh <= 0) {
                    // Let's refresh data
                    timeBeforeRefresh = refreshPeriod.getValue();
                    loadImage();
                }
            }
        }
    }
}