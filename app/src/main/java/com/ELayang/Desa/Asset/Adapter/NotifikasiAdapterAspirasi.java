package com.ELayang.Desa.Asset.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ELayang.Desa.DataModel.Notifikasi.ModelNotifikasiAspirasi;
import com.ELayang.Desa.R;

import java.util.List;

public class NotifikasiAdapterAspirasi extends RecyclerView.Adapter<NotifikasiAdapterAspirasi.ViewHolder> {

    private List<ModelNotifikasiAspirasi> list; // 🔹 pakai ModelNotifikasiAspirasi

    public NotifikasiAdapterAspirasi(List<ModelNotifikasiAspirasi> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notifikasi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelNotifikasiAspirasi item = list.get(position);

        holder.tvJudul.setText(item.getJudul());
        holder.tvPesan.setText(item.getPesan());
        holder.tvTanggal.setText(item.getTanggal());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudul, tvPesan, tvTanggal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tvJudul);
            tvPesan = itemView.findViewById(R.id.tvPesan);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
        }
    }
}
