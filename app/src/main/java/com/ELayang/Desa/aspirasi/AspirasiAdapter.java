package com.ELayang.Desa.aspirasi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.ELayang.Desa.R;
import com.ELayang.Desa.DataModel.Aspirasi;
import java.util.ArrayList;
import java.util.List;
import com.ELayang.Desa.DataModel.Aspirasi;

public class AspirasiAdapter extends RecyclerView.Adapter<AspirasiAdapter.ViewHolder> {

    private List<Aspirasi> list;

    public AspirasiAdapter(List<Aspirasi> list) {
        this.list = (list != null) ? list : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_aspirasi, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Aspirasi a = list.get(position);
        holder.tvJudul.setText(a.getJudul());
        holder.tvKategori.setText(a.getKategori());
        holder.tvStatus.setText(a.getStatus());
        holder.tvTanggal.setText(a.getTanggal());

        if (a.getFoto() != null && !a.getFoto().isEmpty()) {
            String url = "https://sikunir.pbltifnganjuk.com/uploads/upload_aspirasi/" + a.getFoto();
            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.ivFoto);
        } else {
            holder.ivFoto.setImageResource(R.drawable.placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudul, tvKategori, tvStatus, tvTanggal;
        ImageView ivFoto;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tvJudul);
            tvKategori = itemView.findViewById(R.id.tvKategori);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            ivFoto = itemView.findViewById(R.id.ivFoto);
        }
    }
}
