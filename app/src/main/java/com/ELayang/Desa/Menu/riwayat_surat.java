package com.ELayang.Desa.Menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentManager;



import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ELayang.Desa.R;

public class riwayat_surat extends Fragment {

    private Button diajukan, selesai;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_riwayat_surat, container, false);

        diajukan = rootView.findViewById(R.id.diajukan);
        selesai = rootView.findViewById(R.id.selesai);

        diajukan.setOnClickListener(v -> {
            loadFragment(new riwayat_surat_suratDiajukan());
            diajukan.setEnabled(false);
            selesai.setEnabled(true);
        });

        selesai.setOnClickListener(v -> {
            loadFragment(new riwayat_surat_suratSelesai());
            diajukan.setEnabled(true);
            selesai.setEnabled(false);
        });


        // Load default
        diajukan.performClick();

        return rootView;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.frame_riwayat, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
