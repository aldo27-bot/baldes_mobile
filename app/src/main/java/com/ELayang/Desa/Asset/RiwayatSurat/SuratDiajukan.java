package com.ELayang.Desa.Asset.RiwayatSurat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ELayang.Desa.DataModel.RiwayatSurat.ModelDiajukan;
import com.ELayang.Desa.Menu.DetailSuratFragment;
import com.ELayang.Desa.R;

import java.util.ArrayList;

public class SuratDiajukan extends RecyclerView.Adapter<SuratDiajukan.ViewHolder> {

    private Context context;
    private ArrayList<ModelDiajukan> listSurat;

    public SuratDiajukan(Context context, ArrayList<ModelDiajukan> listSurat) {
        this.context = context;
        this.listSurat = listSurat;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_riwayat_surat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ModelDiajukan item = listSurat.get(position);

        holder.nomor.setText(item.getIdPengajuanSurat());
        holder.nama.setText(item.getNama());
        holder.tanggal.setText(item.getTanggal());
        holder.status.setText(item.getStatus());

        holder.itemView.setOnClickListener(v -> {
            DetailSuratFragment fragment = new DetailSuratFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id_pengajuan_surat", item.getIdPengajuanSurat());
            bundle.putString("no_pengajuan", item.getNoPengajuan());
            bundle.putString("nama", item.getNama());
            bundle.putString("nik", item.getNik());
            bundle.putString("tanggal", item.getTanggal());
            bundle.putString("kode_surat", item.getKodeSurat());
            bundle.putString("status", item.getStatus());
            fragment.setArguments(bundle);

            ((AppCompatActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_riwayat, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return listSurat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomor, nama, tanggal, status;

        public ViewHolder(View itemView) {
            super(itemView);
            nomor = itemView.findViewById(R.id.tvNomor);
            nama = itemView.findViewById(R.id.tvNama);
            tanggal = itemView.findViewById(R.id.tvTanggal);
            status = itemView.findViewById(R.id.tvStatus);
        }
    }
}
