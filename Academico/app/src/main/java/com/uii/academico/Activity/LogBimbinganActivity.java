package com.uii.academico.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.uii.academico.Adapter.PagerAdapter;
import com.uii.academico.Adapter.PagerAdapterLogBimbingan;
import com.uii.academico.R;

public class LogBimbinganActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_bimbingan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        setupViewPager();
        setupTabLayout();
        setupTabIcons();

    }


    private void setupViewPager(){

        // Panggil dan pasang adapter ke viewpager
        viewPager = (ViewPager) findViewById(R.id.viewPagerLogBimbingan);
        viewPager.setAdapter(new PagerAdapterLogBimbingan(getSupportFragmentManager()));

        // Menjaga Fragment view InMemory (tersimpan) jadi tidak selalu refresh ketika di klik tabnya
        viewPager.setOffscreenPageLimit(3);

        // Custom View
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                // Ubah Title Aplikasi saat page di scroll
                switch (position) {
                    case 0:
                        getSupportActionBar().setTitle("\t Usulan Jadwal Tatap Muka");
                        break;
                    case 1:
                        getSupportActionBar().setTitle("\t Konfirmasi Mahasiswa");
                        break;
                    case 2:
                        getSupportActionBar().setTitle("\t Riwayat Tatap Muka");
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    private void setupTabLayout (){

        // Panggil dan pasang tablayout sesuai viewpager
        tabLayout = (TabLayout) findViewById(R.id.tabLayoutLogBimbingan);
        tabLayout.setupWithViewPager(viewPager);


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });

    }


    private void setupTabIcons() {

        int[] tabIcons = {
                R.drawable.ask,
                R.drawable.sendtopic,
                R.drawable.confirmed
        };

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

}
