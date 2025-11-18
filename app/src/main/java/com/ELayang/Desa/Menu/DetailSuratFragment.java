package com.ELayang.Desa.Menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ELayang.Desa.R;
import android.widget.ImageButton;


public class DetailSuratFragment extends Fragment {

    private TextView tvNama, tvNik, tvNoPengajuan, tvTanggal, tvKodeSurat, tvStatus;
    private ImageButton btnKembali; // <-- tambahkan ini

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_surat, container, false);

        // Inisialisasi TextView
        tvNama = rootView.findViewById(R.id.tvNama);
        tvNik = rootView.findViewById(R.id.tvNik);
        tvNoPengajuan = rootView.findViewById(R.id.tvNoPengajuan);
        tvTanggal = rootView.findViewById(R.id.tvTanggal);
        tvKodeSurat = rootView.findViewById(R.id.tvKodeSurat);
        tvStatus = rootView.findViewById(R.id.tvStatus);

        // **Inisialisasi tombol back**
        btnKembali = rootView.findViewById(R.id.btnkembali);
        btnKembali.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        // Set data dari arguments
        if (getArguments() != null) {
            tvNama.setText(getArguments().getString("nama"));
            tvNik.setText(getArguments().getString("nik"));
            tvNoPengajuan.setText(getArguments().getString("no_pengajuan"));
            tvTanggal.setText(getArguments().getString("tanggal"));
            tvKodeSurat.setText(getArguments().getString("kode_surat"));
            tvStatus.setText(getArguments().getString("status"));
        }

        return rootView;
    }
}
