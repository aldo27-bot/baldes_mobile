package com.ELayang.Desa.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.util.Log;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Register.ResponRegister1;
import com.ELayang.Desa.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class register1 extends AppCompatActivity {
    ImageButton kembali;
    Button lanjut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);

        EditText username = findViewById(R.id.username),
                email = findViewById(R.id.email),
                nama = findViewById(R.id.nama);

        Intent intent = getIntent();

        String isiemail = intent.getStringExtra("email");
        String isinama = intent.getStringExtra("nama");

        nama.setText(isinama);
        email.setText(isiemail);

        SharedPreferences sharedPreferences = getSharedPreferences("prefRegister", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        kembali = findViewById(R.id.kembali);
        kembali.setOnClickListener(view -> finish());

        lanjut = findViewById(R.id.lanjut);
        lanjut.setEnabled(true);

        lanjut.setOnClickListener(view -> {
            lanjut.setEnabled(false);

            String usernameText = username.getText().toString();
            String emailtext = email.getText().toString();
            String namatextRaw = nama.getText().toString();
            String namatext = namatextRaw.trim().replaceAll(" +", " ");


            if (TextUtils.isEmpty(usernameText)) {
                username.setError("Username harus diisi");
                username.requestFocus();
                enableButton(lanjut);

            } else if (username.length() <= 6) {
                username.setError("Username harus lebih dari 6 karakter");
                username.requestFocus();
                enableButton(lanjut);

            } else if (!usernameText.matches("^[a-zA-Z0-9._]+$")) {
                username.setError("Username tidak boleh mengandung emoji atau simbol");
                username.requestFocus();
                enableButton(lanjut);

            } else if (emailtext.isEmpty()) {
                email.setError("Email harus diisi");
                email.requestFocus();
                enableButton(lanjut);

            } else if (!emailtext.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) {
                email.setError("Email harus menggunakan format @gmail.com");
                email.requestFocus();
                enableButton(lanjut);

            } else if (namatext.isEmpty()) {
                nama.setError("Nama harus diisi");
                nama.requestFocus();
                enableButton(lanjut);

                // ➤ VALIDASI NAMA HANYA HURUF DAN SPASI
            } else if (!namatext.matches("^[a-zA-Z ]+$")) {
                nama.setError("Nama hanya boleh berisi huruf dan spasi");
                nama.requestFocus();
                enableButton(lanjut);

            } else {
                APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
                Call<ResponRegister1> call = apiRequestData.register1(usernameText, emailtext, namatext);

                call.enqueue(new Callback<ResponRegister1>() {
                    @Override
                    public void onResponse(Call<ResponRegister1> call, Response<ResponRegister1> response) {
                        if (response.body() == null) {
                            Toast.makeText(register1.this, "Response kosong dari server", Toast.LENGTH_SHORT).show();
                            enableButton(lanjut);
                            return;
                        }

                        int kode = response.body().kode;
                        Log.d("REGISTER_DEBUG", "Kode dari server: " + kode);

                        switch (kode) {
                            case 1:
                                editor.putString("username", usernameText);
                                editor.putString("email", emailtext);
                                editor.putString("nama", namatext);
                                editor.apply();
                                Intent pindah = new Intent(register1.this, register2.class);
                                startActivity(pindah);
                                finish();
                                break;

                            case 0:
                                Toast.makeText(register1.this, "Username sudah terdaftar", Toast.LENGTH_SHORT).show();
                                enableButton(lanjut);
                                break;

                            case 4:
                                Toast.makeText(register1.this, "Email tidak boleh sama", Toast.LENGTH_SHORT).show();
                                Log.d("REGISTER_DEBUG", "Email sudah digunakan: " + emailtext);
                                enableButton(lanjut);
                                break;

                            case 2:
                                Toast.makeText(register1.this, "Registrasi gagal", Toast.LENGTH_SHORT).show();
                                enableButton(lanjut);
                                break;

                            case 3:
                                Toast.makeText(register1.this, "Data tidak lengkap", Toast.LENGTH_SHORT).show();
                                enableButton(lanjut);
                                break;

                            default:
                                Toast.makeText(register1.this, "Kode tidak dikenal: " + kode, Toast.LENGTH_SHORT).show();
                                enableButton(lanjut);
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponRegister1> call, Throwable t) {
                        Toast.makeText(register1.this, "Gagal terhubung: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("REGISTER_ERROR", "onFailure: ", t);
                        enableButton(lanjut);
                    }
                });
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void enableButton(Button button) {
        button.setEnabled(true);
    }
}
