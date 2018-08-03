/*
 * Created by Greg VIGUIER on 18/07/18 16:34
 * Last modified 18/07/18 16:02
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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.grenades.soleilinfos.MachineStatusViewModel;
import com.grenades.soleilinfos.R;

public class MachineStatusFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final int REFRESH_PERIOD = 60;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final Matrix matrix = new Matrix();


    public MachineStatusFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_machine_status, container, false);

        // SwipeRefreshLayout implementation (onRefresh() see below)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(this);

        final ImageView imageView = view.findViewById(R.id.imageView);
        final ImageView errorImageView = view.findViewById(R.id.errorImageView);
        final TextView errorTextView = view.findViewById(R.id.errorTextView);

        // ProgressBar displays delay next refresh (60 sec)
        final ProgressBar timeBeforeNextLoadProgressBar = view.findViewById(R.id.horizontalProgressBar);
        timeBeforeNextLoadProgressBar.setMax(60);

        // Instanciate maxtrix for Rotation when needed
        // because Machine Status image should be displayed in landscape mode
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            matrix.postRotate(90);
        }

        MachineStatusViewModel viewerModel = ViewModelProviders.of(this).get(MachineStatusViewModel.class);
        viewerModel.getImage().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(@Nullable Bitmap bitmap) {
                // New image is available: remove refresh arrow component
                swipeRefreshLayout.setRefreshing(false);
                if (isAdded()) {
                    if (bitmap != null) {
                        // Hide error widgets
                        errorImageView.setVisibility(View.INVISIBLE);
                        errorTextView.setVisibility(View.INVISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                        // Show loaded image
                        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        imageView.setImageBitmap(rotatedBitmap);

                    } else {
                        errorImageView.setVisibility(View.VISIBLE);
                        errorTextView.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.INVISIBLE);
                    }
                }
            }

        });
        // ProgressBar UI update
        viewerModel.getTimeBeforeRefresh().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                timeBeforeNextLoadProgressBar.setProgress(REFRESH_PERIOD - integer);
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * @see SwipeRefreshLayout.OnRefreshListener implementation
     */
    @Override
    public void onRefresh() {
        MachineStatusViewModel viewerModel = ViewModelProviders.of(this).get(MachineStatusViewModel.class);
        viewerModel.refreshDataAsync();
        swipeRefreshLayout.setRefreshing(false);
    }
}
