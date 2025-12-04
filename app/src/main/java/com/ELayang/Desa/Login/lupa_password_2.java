package com.ELayang.Desa.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Lupa_Password.ResponPassword2;
import com.ELayang.Desa.DataModel.Register.ResponOTP;
import com.ELayang.Desa.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class lupa_password_2 extends AppCompatActivity {

    CountDownTimer countDownTimer;
    private long timeLeftInMillis = 30000;
    EditText pass1, pass2, otp;
    Button lanjut, kirim;
    ImageButton kembali;
    TextView timer;

    // ==== Fungsi cek emoji / karakter aneh ====
    private boolean containsEmoji(String s) {
        for (int i = 0; i < s.length(); i++) {
            int type = Character.getType(s.charAt(i));
            if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_password2);

        SharedPreferences sharedPreferences = getSharedPreferences("prefLupa_password", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        pass1 = findViewById(R.id.pass1);
        pass2 = findViewById(R.id.pass2);
        otp = findViewById(R.id.otp);
        lanjut = findViewById(R.id.lanjut);
        kirim = findViewById(R.id.kirimbos);
        kembali = findViewById(R.id.kembali);
        timer = findViewById(R.id.timer);

        // ==== Tombol Lanjut ====
        lanjut.setOnClickListener(view -> {
            String password1 = pass1.getText().toString();
            String password2 = pass2.getText().toString();
            String kode = otp.getText().toString();

            // ==== Validasi ====
            if (TextUtils.isEmpty(password1)) {
                pass1.setError("Password Harus Diisi");
                pass1.requestFocus();

            } else if (password1.length() < 6) {
                pass1.setError("Password harus lebih dari 6 karakter");
                pass1.requestFocus();

            } else if (password1.contains(" ")) {
                pass1.setError("Password tidak boleh mengandung spasi");
                pass1.requestFocus();

            } else if (containsEmoji(password1)) {
                pass1.setError("Password tidak boleh mengandung emoji atau simbol aneh");
                pass1.requestFocus();

            } else if (TextUtils.isEmpty(password2)) {
                pass2.setError("Konfirmasi password harus diisi");
                pass2.requestFocus();

            } else if (!password1.equals(password2)) {
                pass2.setError("Password tidak sama");
                pass2.requestFocus();

            } else if (TextUtils.isEmpty(kode)) {
                otp.setError("Kode OTP Harus Diisi");
                otp.requestFocus();

            } else {

                // ==== Jika valid, lanjut API ====
                APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
                Call<ResponPassword2> call = apiRequestData.lupa_password2(kode, password2, username);

                call.enqueue(new Callback<ResponPassword2>() {
                    @Override
                    public void onResponse(Call<ResponPassword2> call, Response<ResponPassword2> response) {
                        String pesan = response.body().getPesan();

                        if (response.body().kode == 0) {
                            Toast.makeText(lupa_password_2.this, pesan, Toast.LENGTH_SHORT).show();

                        } else if (response.body().kode == 1) {
                            Toast.makeText(lupa_password_2.this, pesan, Toast.LENGTH_SHORT).show();
                            Intent buka = new Intent(lupa_password_2.this, login.class);
                            startActivity(buka);

                        } else if (response.body().kode == 2) {
                            Toast.makeText(lupa_password_2.this, pesan, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponPassword2> call, Throwable t) {
                        Toast.makeText(lupa_password_2.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // ==== Tombol kembali ====
        kembali.setOnClickListener(view -> finish());

        // ==== Kirim OTP ====
        kirim.setOnClickListener(view -> {

            kirim.setEnabled(false);

            APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
            Call<ResponOTP> call = apiRequestData.kirim_otp(username);

            call.enqueue(new Callback<ResponOTP>() {
                @Override
                public void onResponse(Call<ResponOTP> call, Response<ResponOTP> response) {

                    if (response.body().kode == 0) {
                        Toast.makeText(lupa_password_2.this, "Username tidak valid", Toast.LENGTH_SHORT).show();

                    } else if (response.body().kode == 1) {
                        Toast.makeText(lupa_password_2.this, "Kode OTP terkirim", Toast.LENGTH_SHORT).show();

                    } else if (response.body().kode == 2) {
                        Toast.makeText(lupa_password_2.this, "Gagal mengirim OTP", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponOTP> call, Throwable t) {
                    Toast.makeText(lupa_password_2.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            startTimer();
        });

        startTimer();  // Timer berjalan saat activity dibuka
        kirim.setEnabled(false);
    }

    // ==== Timer ====
    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                kirim.setEnabled(true);
                timer.setText("Selesai!");
                resetTimer();
            }
        }.start();
    }

    private void updateTimer() {
        int seconds = (int) (timeLeftInMillis / 1000);
        timer.setText(seconds + " detik");
    }

    private void resetTimer() {
        timeLeftInMillis = 30000;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
