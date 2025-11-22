package com.ELayang.Desa.Asset.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ELayang.Desa.R;
import com.ELayang.Desa.DataModel.Notifikasi.ModelNotifikasi;
import com.bumptech.glide.Glide;

import java.util.List;

public class AdapterNotifikasi extends RecyclerView.Adapter<AdapterNotifikasi.VH> {

    private final Context ctx;
    private final List<ModelNotifikasi> items;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ModelNotifikasi item);
    }

    public AdapterNotifikasi(Context ctx, List<ModelNotifikasi> items) {
        this.ctx = ctx;
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.listener = l;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_notifikasi, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ModelNotifikasi d = items.get(position);

        holder.tvStatus.setText(d.getStatus() != null ? d.getStatus() : "-");
        holder.tvNo.setText("No: " + (d.getNopengajuan() != null ? d.getNopengajuan() : "-"));
        holder.tvKode.setText(d.getKode() != null ? d.getKode() : "-");
        holder.tvTanggal.setText(d.getTanggal() != null ? d.getTanggal() : "-");
        holder.tvAlasan.setText(d.getAlasan() != null && !d.getAlasan().isEmpty()
                ? "Alasan: " + d.getAlasan()
                : "");

        // warna status
        String status = d.getStatus() != null ? d.getStatus().toLowerCase() : "";
        int color;
        if (status.contains("selesai")) color = Color.parseColor("#2e7d32");
        else if (status.contains("tolak")) color = Color.parseColor("#c62828");
        else if (status.contains("proses") || status.contains("menunggu")) color = Color.parseColor("#fb8c00");
        else color = Color.parseColor("#9e9e9e");

        holder.viewStatusBar.setBackgroundColor(color);

        Glide.with(ctx)
                .load(R.drawable.bg_card)
                .into(holder.imgIcon);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(d);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        View viewStatusBar;
        ImageView imgIcon;
        TextView tvStatus, tvNo, tvKode, tvTanggal, tvAlasan;

        VH(@NonNull View v) {
            super(v);
            viewStatusBar = v.findViewById(R.id.view_status_bar);
            imgIcon = v.findViewById(R.id.img_icon);
            tvStatus = v.findViewById(R.id.tv_status);
            tvNo = v.findViewById(R.id.tv_nopengajuan);
            tvKode = v.findViewById(R.id.tv_kode);
            tvTanggal = v.findViewById(R.id.tv_tanggal);
            tvAlasan = v.findViewById(R.id.tv_alasan);
        }
    }
}
