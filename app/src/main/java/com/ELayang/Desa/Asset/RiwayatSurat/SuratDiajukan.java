package com.ELayang.Desa.Asset.RiwayatSurat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        // === Format tanggal tanpa jam ===
        String fullDate = item.getTanggal();
        String onlyDate = fullDate;

        if (fullDate != null && fullDate.contains(" ")) {
            onlyDate = fullDate.split(" ")[0];
        }

        holder.tanggal.setText(onlyDate);
        holder.status.setText(item.getStatus());

        // ==== Pindah ke Activity DetailSuratFragment ====
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailSuratFragment.class);
            intent.putExtra("id_pengajuan_surat", item.getIdPengajuanSurat());
            intent.putExtra("no_pengajuan", item.getNoPengajuan());
            intent.putExtra("nama", item.getNama());
            intent.putExtra("nik", item.getNik());
            intent.putExtra("tanggal", item.getTanggal());
            intent.putExtra("kode_surat", item.getKodeSurat());
            intent.putExtra("status", item.getStatus());

            context.startActivity(intent);
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
