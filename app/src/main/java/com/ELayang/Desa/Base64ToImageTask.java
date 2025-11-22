package com.ELayang.Desa;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.ImageView;
import com.ELayang.Desa.R;


public class Base64ToImageTask extends AsyncTask<String, Void, Bitmap> {
    private ImageView imageView;

    public Base64ToImageTask(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        try {
            String base64Str = strings[0].split(",")[1]; // buang "data:image/jpeg;base64,"
            byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            GlideApp.with(imageView.getContext())
                    .load(bitmap)
                    .circleCrop()
                    .placeholder(R.drawable.akun_profil) // gambar default
                    .into(imageView);
        }
    }
}
