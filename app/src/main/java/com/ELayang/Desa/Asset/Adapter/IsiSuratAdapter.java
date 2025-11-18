package com.ELayang.Desa.Asset.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ELayang.Desa.DataModel.RiwayatSurat.ModelDiajukan;
import com.ELayang.Desa.Menu.riwayat_surat_suratDiajukan;
import com.ELayang.Desa.R;

import java.util.List;

public class IsiSuratAdapter extends RecyclerView.Adapter<IsiSuratAdapter.ViewHolder> {

    private Context context;
    private List<ModelDiajukan> listSurat;

    public IsiSuratAdapter(Context context, List<ModelDiajukan> listSurat) {
        this.context = context;
        this.listSurat = listSurat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_riwayat_surat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelDiajukan surat = listSurat.get(position);
        holder.tvNama.setText(surat.getNama());
        holder.tvTanggal.setText(surat.getTanggal());
        holder.tvStatus.setText(surat.getStatus());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, riwayat_surat_suratDiajukan.class);
            intent.putExtra("id_pengajuan_surat", surat.getIdPengajuanSurat());
            intent.putExtra("no_pengajuan", surat.getNoPengajuan());
            intent.putExtra("nama", surat.getNama());
            intent.putExtra("nik", surat.getNik());
            intent.putExtra("tanggal", surat.getTanggal());
            intent.putExtra("kode_surat", surat.getKodeSurat());
            intent.putExtra("status", surat.getStatus());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listSurat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvTanggal, tvStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
