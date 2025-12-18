package com.ELayang.Desa.Login;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Register.ResponDelete;
import com.ELayang.Desa.DataModel.Register.ResponOTP;
import com.ELayang.Desa.DataModel.Register.ResponRegister2;
import com.ELayang.Desa.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class register2 extends AppCompatActivity {
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 30000;
    private int gagalOTP = 0;

    private Button lanjut, kirimbos;
    private TextView timer;
    private ImageButton kembali;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        EditText kode_otp = findViewById(R.id.kode_otp);
        kirimbos = findViewById(R.id.kirimbos);
        timer = findViewById(R.id.timer);
        kembali = findViewById(R.id.kembali);
        lanjut = findViewById(R.id.lanjut);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memproses...");
        progressDialog.setCancelable(false);

        SharedPreferences sp = getSharedPreferences("prefRegister", MODE_PRIVATE);
        String username = sp.getString("username", "");
        long startTime = sp.getLong("reg_start_time", 0);

        // BACK callback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                confirmCancel(username);
            }
        });

        // AUTO DELETE jika lewat 5 menit
        long now = System.currentTimeMillis();
        long fiveMinutes = 5 * 60 * 1000L;
        if (startTime > 0 && (now - startTime) > fiveMinutes) {
            deleteUser(username);
            clearRegistrationPrefs();
            Toast.makeText(this, "Waktu registrasi habis. Silakan ulangi.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(register2.this, register1.class));
            finish();
            return;
        }

        kirimbos.setEnabled(false);
        startTimer();
        sendOtp(username, false);

        kembali.setOnClickListener(v -> confirmCancel(username));

        lanjut.setOnClickListener(v -> {
            String otp = kode_otp.getText().toString().trim();
            if (otp.isEmpty()) {
                kode_otp.setError("Masukkan kode OTP");
                kode_otp.requestFocus();
                return;
            }

            lanjut.setEnabled(false);
            progressDialog.show();
            sp.edit().putString("otp", otp).apply();

            APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
            Call<ResponRegister2> call = api.register2(username, otp);
            call.enqueue(new Callback<ResponRegister2>() {
                @Override
                public void onResponse(Call<ResponRegister2> call, Response<ResponRegister2> response) {
                    progressDialog.dismiss();
                    lanjut.setEnabled(true);

                    if (response.body() == null) {
                        Toast.makeText(register2.this, "Response kosong", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (response.body().kode == 0) {
                        startActivity(new Intent(register2.this, register3.class));
                        finish();
                    } else if (response.body().kode == 1) {
                        gagalOTP++;
                        if (gagalOTP >= 3) {
                            deleteUser(username);
                            clearRegistrationPrefs();
                            Toast.makeText(register2.this, "Percobaan terlalu banyak. Registrasi dibatalkan.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(register2.this, register1.class));
                            finish();
                        } else {
                            Toast.makeText(register2.this, "Kode OTP salah", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(register2.this, "Respon tidak dikenali", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponRegister2> call, Throwable t) {
                    progressDialog.dismiss();
                    lanjut.setEnabled(true);
                    Toast.makeText(register2.this, "Gagal terhubung: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        kirimbos.setOnClickListener(view -> {
            kirimbos.setEnabled(false);
            startTimer();
            sendOtp(username, true);
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                timer.setText((millisUntilFinished / 1000) + " detik");
            }

            @Override
            public void onFinish() {
                timer.setText("Selesai!");
                kirimbos.setEnabled(true);
                timeLeftInMillis = 30000;
            }
        }.start();
    }

    private void sendOtp(String username, boolean showToast) {
        APIRequestData apiAuto = RetroServer.konekRetrofit().create(APIRequestData.class);
        apiAuto.kirim_otp(username).enqueue(new Callback<ResponOTP>() {
            @Override
            public void onResponse(Call<ResponOTP> call, Response<ResponOTP> response) {
                if (response.body() != null && response.body().kode == 1 && showToast)
                    Toast.makeText(register2.this, "OTP terkirim", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponOTP> call, Throwable t) {
                if (showToast) Toast.makeText(register2.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmCancel(String username) {
        new AlertDialog.Builder(this)
                .setTitle("Batal Registrasi")
                .setMessage("Apakah Anda yakin ingin membatalkan pendaftaran? Data sementara akan dihapus.")
                .setPositiveButton("Ya", (dialog, which) -> {
                    if (!username.isEmpty()) deleteUser(username);
                    clearRegistrationPrefs();
                    Toast.makeText(register2.this, "Registrasi dibatalkan", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(register2.this, register1.class));
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
