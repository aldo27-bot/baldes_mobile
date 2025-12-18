package com.ELayang.Desa.Login;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Register.ResponDelete;
import com.ELayang.Desa.DataModel.Register.ResponRegister3;
import com.ELayang.Desa.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class register3 extends AppCompatActivity {
    private ImageButton kembali;
    private Button lanjut;
    private ProgressDialog progressDialog;

    private boolean containsEmoji(String s) {
        for (int i = 0; i < s.length(); i++) {
            int type = Character.getType(s.charAt(i));
            if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

        EditText pass1 = findViewById(R.id.pass1), pass2 = findViewById(R.id.pass2);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memproses...");
        progressDialog.setCancelable(false);

        SharedPreferences sp = getSharedPreferences("prefRegister", MODE_PRIVATE);
        String username = sp.getString("username", "");
        String email = sp.getString("email", "");
        String nama = sp.getString("nama", "");
        String otp = sp.getString("otp", "");
        long startTime = sp.getLong("reg_start_time", 0);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                confirmCancel(username);
            }
        });

        // AUTO DELETE 5 MENIT
        long now = System.currentTimeMillis();
        long fiveMinutes = 5 * 60 * 1000L;
        if (startTime > 0 && (now - startTime) > fiveMinutes) {
            deleteUser(username);
            clearRegistrationPrefs();
            Toast.makeText(this, "Waktu habis. Registrasi dibatalkan.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(register3.this, register1.class));
            finish();
            return;
        }

        kembali = findViewById(R.id.kembali);
        kembali.setOnClickListener(v -> confirmCancel(username));

        lanjut = findViewById(R.id.lanjut);
        lanjut.setOnClickListener(v -> {
            String password = pass1.getText().toString();
            String password2 = pass2.getText().toString();

            if (TextUtils.isEmpty(password)) { pass1.setError("Password Harus Diisi"); pass1.requestFocus(); return; }
            if (password.length() < 6) { pass1.setError("Minimal 6 karakter"); return; }
            if (password.contains(" ")) { pass1.setError("Tidak boleh ada spasi"); return; }
            if (containsEmoji(password)) { pass1.setError("Tidak boleh mengandung emoji"); return; }
            if (TextUtils.isEmpty(password2)) { pass2.setError("Konfirmasi harus diisi"); return; }
            if (!password.equals(password2)) { pass2.setError("Password tidak sama!"); return; }

            lanjut.setEnabled(false);
            progressDialog.show();

            APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
            api.register3(username, email, nama, otp, password2).enqueue(new Callback<ResponRegister3>() {
                @Override
                public void onResponse(Call<ResponRegister3> call, Response<ResponRegister3> response) {
                    progressDialog.dismiss();
                    lanjut.setEnabled(true);

                    if (response.body() == null) { Toast.makeText(register3.this, "Response kosong", Toast.LENGTH_SHORT).show(); return; }

                    if (response.body().kode == 1) {
                        clearRegistrationPrefs();
                        Toast.makeText(register3.this, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(register3.this, login.class));
                        finish();
                    } else {
                        if (response.body().kode == 2) deleteUser(username);
                        Toast.makeText(register3.this, "Registrasi gagal", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponRegister3> call, Throwable t) {
                    progressDialog.dismiss();
                    lanjut.setEnabled(true);
                    Toast.makeText(register3.this, "Gagal koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void confirmCancel(String username) {
        new AlertDialog.Builder(this)
                .setTitle("Batal Registrasi")
                .setMessage("Apakah Anda yakin ingin membatalkan pendaftaran? Data sementara akan dihapus.")
                .setPositiveButton("Ya", (dialog, which) -> {
                    if (!username.isEmpty()) deleteUser(username);
                    clearRegistrationPrefs();
                    Toast.makeText(register3.this, "Registrasi dibatalkan", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(register3.this, register1.class));
                    finish();
                })
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void deleteUser(String username) {
        if (username.isEmpty()) return;
        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        api.deleteUser(username).enqueue(new Callback<ResponDelete>() {
            @Override public void onResponse(Call<ResponDelete> call, Response<ResponDelete> response) {}
            @Override public void onFailure(Call<ResponDelete> call, Throwable t) {}
        });
    }

    private void clearRegistrationPrefs() {
        SharedPreferences sp = getSharedPreferences("prefRegister", MODE_PRIVATE);
        sp.edit().clear().apply();
    }
}
