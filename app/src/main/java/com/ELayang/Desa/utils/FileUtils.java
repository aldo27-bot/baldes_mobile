package com.ELayang.Desa.utils;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FileUtils {

    /**
     * Membuat MultipartBody.Part langsung dari Uri, aman di Android 10+.
     * @param context Context
     * @param partName Nama field di form-data
     * @param fileUri Uri file yang dipilih
     * @return MultipartBody.Part siap dipakai di Retrofit
     * @throws IOException
     */
    public static MultipartBody.Part prepareFilePart(Context context, String partName, Uri fileUri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
        if (inputStream == null) {
            throw new IOException("Tidak dapat membuka file dari URI: " + fileUri);
        }

        // Baca semua bytes dari InputStream
        byte[] bytes = new byte[inputStream.available()];
        int read = inputStream.read(bytes);
        inputStream.close();

        if (read <= 0) {
            throw new IOException("File kosong atau tidak dapat dibaca");
        }

        // Buat RequestBody
        RequestBody requestFile = RequestBody.create(bytes, MediaType.parse("image/*"));

        // Buat MultipartBody.Part
        return MultipartBody.Part.createFormData(partName, "upload.jpg", requestFile);
    }
}
