package com.ELayang.Desa.Login;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Register.ResponRegister1;
import com.ELayang.Desa.DataModel.Register.ResponDelete;
import com.ELayang.Desa.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class register1 extends AppCompatActivity {
    ImageButton kembali;
    Button lanjut;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);

        EditText username = findViewById(R.id.username),
                email = findViewById(R.id.email),
                nama = findViewById(R.id.nama);

        // optional prefill
        Intent intent = getIntent();
        if (intent.getStringExtra("email") != null) email.setText(intent.getStringExtra("email"));
        if (intent.getStringExtra("nama") != null) nama.setText(intent.getStringExtra("nama"));

        SharedPreferences sharedPreferences = getSharedPreferences("prefRegister", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memproses...");
        progressDialog.setCancelable(false);

        kembali = findViewById(R.id.kembali);
        kembali.setOnClickListener(v -> confirmCancel());

        lanjut = findViewById(R.id.lanjut);
        lanjut.setEnabled(true);

        // BACK button callback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                confirmCancel();
            }
        });

        lanjut.setOnClickListener(view -> {
            lanjut.setEnabled(false);
            String usernameText = username.getText().toString().trim();
            String emailText = email.getText().toString().trim();
            String namaText = nama.getText().toString().trim().replaceAll(" +", " ");

            // VALIDASI
            if (!validateInputs(username, email, nama, usernameText, emailText, namaText)) {
                enableButton(lanjut);
                return;
            }

            progressDialog.show();

            // Panggil API register1
            APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
            Call<ResponRegister1> call = api.register1(usernameText, emailText, namaText);
            call.enqueue(new Callback<ResponRegister1>() {
                @Override
                public void onResponse(Call<ResponRegister1> call, Response<ResponRegister1> response) {
                    progressDialog.dismiss();
                    enableButton(lanjut);

                    if (response.body() == null) {
                        Toast.makeText(register1.this, "Response kosong dari server", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int kode = response.body().kode;
                    Log.d("REGISTER_DEBUG", "Kode dari server: " + kode);

                    switch (kode) {
                        case 1:
                            // simpan sementara di SharedPreferences
                            editor.putString("username", usernameText);
                            editor.putString("email", emailText);
                            editor.putString("nama", namaText);
                            try {
                                if (response.body().getOtp() != null && !response.body().getOtp().isEmpty())
                                    editor.putString("otp_server", response.body().getOtp());
                            } catch (Exception ignored) {}
                            editor.putLong("reg_start_time", System.currentTimeMillis());
                            editor.apply();

                            startActivity(new Intent(register1.this, register2.class));
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                            break;

                        case 0: Toast.makeText(register1.this, "Username sudah terdaftar", Toast.LENGTH_SHORT).show(); break;
                        case 4: Toast.makeText(register1.this, "Email tidak boleh sama", Toast.LENGTH_SHORT).show(); break;
                        case 2: Toast.makeText(register1.this, "Registrasi gagal", Toast.LENGTH_SHORT).show(); break;
                        case 3: Toast.makeText(register1.this, "Data tidak lengkap", Toast.LENGTH_SHORT).show(); break;
                        default: Toast.makeText(register1.this, "Kode tidak dikenal: " + kode, Toast.LENGTH_SHORT).show(); break;
                    }
                }

                @Override
                public void onFailure(Call<ResponRegister1> call, Throwable t) {
                    progressDialog.dismiss();
                    enableButton(lanjut);
                    Toast.makeText(register1.this, "Gagal terhubung: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private boolean validateInputs(EditText username, EditText email, EditText nama,
                                   String usernameText, String emailText, String namaText) {
        if (TextUtils.isEmpty(usernameText)) { username.setError("Username harus diisi"); username.requestFocus(); return false; }
        if (usernameText.length() <= 6) { username.setError("Username minimal 7 karakter"); username.requestFocus(); return false; }
        if (!usernameText.matches("^[a-zA-Z0-9._]+$")) { username.setError("Tidak boleh emoji atau simbol"); username.requestFocus(); return false; }
        if (TextUtils.isEmpty(emailText)) { email.setError("Email harus diisi"); email.requestFocus(); return false; }
        if (!emailText.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) { email.setError("Email harus @gmail.com"); email.requestFocus(); return false; }
        if (TextUtils.isEmpty(namaText)) { nama.setError("Nama harus diisi"); nama.requestFocus(); return false; }
        if (!namaText.matches("^[a-zA-Z ]+$")) { nama.setError("Nama hanya huruf dan spasi"); nama.requestFocus(); return false; }
        return true;
    }

    private void confirmCancel() {
        SharedPreferences sp = getSharedPreferences("prefRegister", MODE_PRIVATE);
        String username = sp.getString("username", "");
        new AlertDialog.Builder(this)
                .setTitle("Batal Registrasi")
                .setMessage("Apakah Anda yakin ingin membatalkan pendaftaran? Data sementara akan dihapus.")
                .setPositiveButton("Ya", (dialog, which) -> {
                    if (!username.isEmpty()) deleteUser(username);
                    clearRegistrationPrefs();
                    Toast.makeText(register1.this, "Registrasi dibatalkan", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void enableButton(Button button) { button.setEnabled(true); }

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
