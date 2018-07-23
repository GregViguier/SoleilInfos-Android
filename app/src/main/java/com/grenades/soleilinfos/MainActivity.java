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
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.grenades.soleilinfos.fragments.AboutFragment;
import com.grenades.soleilinfos.fragments.ComeToSoleilFragment;
import com.grenades.soleilinfos.fragments.MachineStatusFragment;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView view = findViewById(R.id.navigationView);
        loadFragment(new MachineStatusFragment());

        view.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_machine_status:
                                // Create a new Fragment to be placed in the activity layout
                                MachineStatusFragment machineStatusFragment = new MachineStatusFragment();
                                loadFragment(machineStatusFragment);
                                break;
                            case R.id.action_come_to:
                                ComeToSoleilFragment comeToSoleilFragment = new ComeToSoleilFragment();
                                loadFragment(comeToSoleilFragment);
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
        transaction.commit();
    }
}
