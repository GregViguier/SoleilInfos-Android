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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.grenades.soleilinfos.ui.AboutFragment;
import com.grenades.soleilinfos.ui.ComeToSoleilFragment;
import com.grenades.soleilinfos.ui.MachineStatusFragment;
import com.grenades.soleilinfos.ui.SettingsFragment;

import timber.log.Timber;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomView = findViewById(R.id.navigationView);
        bottomView.setItemHorizontalTranslationEnabled(false);
        bottomView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        Timber.plant(new Timber.DebugTree());

        // Default fragment -> MachineStatus
        loadFragment(new MachineStatusFragment());

        // Navigation logic
        bottomView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_machine_status:
                                MachineStatusFragment machineStatusFragment = new MachineStatusFragment();
                                loadFragment(machineStatusFragment);
                                break;
                            case R.id.action_come_to:
                                ComeToSoleilFragment comeToSoleilFragment = new ComeToSoleilFragment();
                                loadFragment(comeToSoleilFragment);
                                break;
                            case R.id.action_settings:
                                SettingsFragment settingsFragment = new SettingsFragment();
                                loadFragment(settingsFragment);
                                break;
                            case R.id.action_about:
                                AboutFragment aboutFragment = new AboutFragment();
                                loadFragment(aboutFragment);
                                break;
                        }
                        return true;
                    }
                });
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        if (!this.isDestroyed()) {
            transaction.commitAllowingStateLoss();
        }
    }
}