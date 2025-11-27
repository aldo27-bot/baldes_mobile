package com.ELayang.Desa;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ELayang.Desa.Menu.Notifikasi;
import com.ELayang.Desa.Menu.akun;
import com.ELayang.Desa.Menu.dashboard;
import com.ELayang.Desa.Menu.riwayat_surat;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new dashboard();
            case 1: return new Notifikasi();
            case 2: return new riwayat_surat();
            case 3: return new akun();
        }
        return new dashboard();
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
