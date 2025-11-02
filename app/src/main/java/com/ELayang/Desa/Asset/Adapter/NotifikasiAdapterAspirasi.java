package com.ELayang.Desa.Asset.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ELayang.Desa.DataModel.Notifikasi.ModelNotifikasi;
import com.ELayang.Desa.DataModel.Notifikasi.ModelNotifikasi;
import com.ELayang.Desa.R;
import java.util.List;

public class NotifikasiAdapterAspirasi extends RecyclerView.Adapter<NotifikasiAdapterAspirasi.ViewHolder> {

    private List<ModelNotifikasi> notifikasiList;

    public NotifikasiAdapterAspirasi(List<ModelNotifikasi> list) {
        this.notifikasiList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notifikasi, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelNotifikasi n = notifikasiList.get(position);
        holder.tvJudul.setText(n.getJudul());
        holder.tvPesan.setText(n.getPesan());
        holder.tvTanggapan.setText(n.getTanggapan());
        holder.tvTanggal.setText(n.getTanggal());
    }

    @Override
    public int getItemCount() {
        return notifikasiList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudul, tvPesan, tvTanggapan, tvTanggal;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tvJudul);
            tvPesan = itemView.findViewById(R.id.tvPesan);
            tvTanggapan = itemView.findViewById(R.id.tvTanggapan);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
        }
    }
}

