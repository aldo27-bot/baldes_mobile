//package com.ELayang.Desa.Asset.Adapter;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.ELayang.Desa.DataModel.Notifikasi.ModelNotifikasiAspirasi;
//import com.ELayang.Desa.R;
//
//import java.util.List;
//
//public class NotifikasiAdapterAspirasi extends RecyclerView.Adapter<NotifikasiAdapterAspirasi.NotifikasiViewHolder> {
//
//    private final List<ModelNotifikasiAspirasi> notifikasiList;
//
//    public NotifikasiAdapterAspirasi(List<ModelNotifikasiAspirasi> notifikasiList) {
//        this.notifikasiList = notifikasiList;
//    }
//
//    @NonNull
//    @Override
//    public NotifikasiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notifikasi, parent, false);
//        return new NotifikasiViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull NotifikasiViewHolder holder, int position) {
//        ModelNotifikasiAspirasi notif = notifikasiList.get(position);
//        holder.tvPesan.setText(notif.getPesan());
//        holder.tvStatus.setText("Status: " + notif.getStatus());
//        holder.tvTanggal.setText(notif.getTanggal());
//    }
//
//    @Override
//    public int getItemCount() {
//        return notifikasiList.size();
//    }
//
//    public static class NotifikasiViewHolder extends RecyclerView.ViewHolder {
//        TextView tvPesan, tvStatus, tvTanggal;
//
//        public NotifikasiViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvPesan = itemView.findViewById(R.id.tvPesan);
//            tvStatus = itemView.findViewById(R.id.tvStatus);
//            tvTanggal = itemView.findViewById(R.id.tvTanggal);
//        }
//    }
//}
