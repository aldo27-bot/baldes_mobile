package com.ELayang.Desa.Asset.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ELayang.Desa.DataModel.Notifikasi.ModelNotifikasi;
import com.ELayang.Desa.R;
import java.util.List;

public class AdapterNotifikasi extends RecyclerView.Adapter<AdapterNotifikasi.HolderNotifikasi> {

    private Context context;
    private List<ModelNotifikasi> listData;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ModelNotifikasi item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public AdapterNotifikasi(Context context, List<ModelNotifikasi> listData) {
        this.context = context;
        this.listData = listData;
    }

    @NonNull
    @Override
    public HolderNotifikasi onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notifikasi, parent, false);
        return new HolderNotifikasi(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderNotifikasi holder, int position) {
        ModelNotifikasi data = listData.get(position);

        holder.tvStatus.setText(data.getStatus());
        holder.tvNoPengajuan.setText("No: " + data.getJudul()); // <-- pakai judul
        holder.tvKode.setText(data.getJenis());
        holder.tvTanggal.setText(data.getTanggal());

        if (data.getAlasan() != null && !data.getAlasan().isEmpty()) {
            holder.tvAlasan.setVisibility(View.VISIBLE);
            holder.tvAlasan.setText("Alasan: " + data.getAlasan());
        } else {
            holder.tvAlasan.setVisibility(View.GONE);
        }

        switch (data.getStatus().toLowerCase()) {
            case "ditolak":
                holder.viewStatusBar.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_dark));
                break;
            case "diproses":
                holder.viewStatusBar.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                break;
            case "selesai":
                holder.viewStatusBar.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
                break;
            default:
                holder.viewStatusBar.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(data);
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class HolderNotifikasi extends RecyclerView.ViewHolder {

        TextView tvStatus, tvNoPengajuan, tvKode, tvTanggal, tvAlasan;
        ImageView imgIcon;
        View viewStatusBar;

        public HolderNotifikasi(@NonNull View itemView) {
            super(itemView);

            tvStatus = itemView.findViewById(R.id.tv_status);
            tvNoPengajuan = itemView.findViewById(R.id.tv_nopengajuan);
            tvKode = itemView.findViewById(R.id.tv_kode);
            tvTanggal = itemView.findViewById(R.id.tv_tanggal);
            tvAlasan = itemView.findViewById(R.id.tv_alasan);
            imgIcon = itemView.findViewById(R.id.img_icon);
            viewStatusBar = itemView.findViewById(R.id.view_status_bar);
        }
    }
}
