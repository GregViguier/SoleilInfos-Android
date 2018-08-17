package com.grenades.soleilinfos.data.datasource;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import timber.log.Timber;

public class MachineStatusImageDataSourceImpl implements MachineStatusImageDataSource {

    private static final String IMAGE_URL = "https://www.synchrotron-soleil.fr/sites/default/files/WebInterfaces/machinestatus/MachineStatus-extranet.png";

    @Override
    public Bitmap getImage() {
        Bitmap result = null;

        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(IMAGE_URL).build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            Timber.d(e);
        }
        if (response != null && response.isSuccessful()) {
            ResponseBody body = response.body();
            if (body != null) {
                result = BitmapFactory.decodeStream(body.byteStream());
            }
        }
        return result;
    }
}
