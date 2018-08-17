package com.grenades.soleilinfos.data;

import android.graphics.Bitmap;

import com.grenades.soleilinfos.data.datasource.MachineStatusImageDataSource;
import com.grenades.soleilinfos.data.datasource.MachineStatusImageDataSourceImpl;

public class SoleilInfosRepository implements Repository {

    private static volatile SoleilInfosRepository instance;
    private final MachineStatusImageDataSource imageDataSource;

    private SoleilInfosRepository() {
        imageDataSource = new MachineStatusImageDataSourceImpl();
    }

    public static SoleilInfosRepository getInstance() {
        if (instance == null) {
            synchronized (SoleilInfosRepository.class) {
                instance = new SoleilInfosRepository();
            }
        }
        return instance;
    }

    @Override
    public Bitmap getImage() {
        return imageDataSource.getImage();
    }
}
