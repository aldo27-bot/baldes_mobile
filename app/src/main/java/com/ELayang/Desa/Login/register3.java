package com.ELayang.Desa.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
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
ImageButton kembali;
Button lanjut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);
        EditText pass1 = findViewById(R.id.pass1),
        pass2 = findViewById(R.id.pass2);

        SharedPreferences sharedPreferences =getSharedPreferences("prefRegister", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username","");
        String email = sharedPreferences.getString("email","");
        String nama = sharedPreferences.getString("nama","");
        String otp = sharedPreferences.getString("otp","");

        kembali = findViewById(R.id.kembali);
        kembali.setOnClickListener(v -> {
            APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
            Call<ResponDelete> call = apiRequestData.delete(username);

            call.enqueue(new Callback<ResponDelete>() {
                @Override
                public void onResponse(Call<ResponDelete> call, Response<ResponDelete> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().kode == 0) {
                            // sukses hapus → kembali ke register1
                            Intent intent = new Intent(register3.this, register1.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(register3.this, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(register3.this, "Response kosong", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(register3.this, register1.class));
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<ResponDelete> call, Throwable t) {
                    Toast.makeText(register3.this, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(register3.this, register1.class));
                    finish();
                }
            });
        });

        lanjut = findViewById(R.id.lanjut);
        lanjut.setOnClickListener(v ->{
            String password = pass1.getText().toString();
            String password2 = pass2.getText().toString();
            if (TextUtils.isEmpty(password)) {
                pass1.setError("Password Harus Diisi");
                pass1.requestFocus();
            } else if(pass1.length() <6){
                pass1.setError("Password Harus Lebih Dari 6 Karakter");
                pass1.requestFocus();
            }else if(pass2.getText().toString().isEmpty()){
                pass2.setError("Password Harus Diisi");
                pass2.requestFocus();
            } else if(password.equals(password2)){
                APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
                Call<ResponRegister3> call = apiRequestData.register3(username, email, nama, otp, password2);
                call.enqueue(new Callback<ResponRegister3>() {
                    @Override
                    public void onResponse(Call<ResponRegister3> call, Response<ResponRegister3> response) {
                        if (response.body().kode ==0){
                            Toast.makeText(register3.this, "masuk kode 0", Toast.LENGTH_SHORT).show();
                        } else if (response.body().kode == 1){
                            Toast.makeText(register3.this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show();
                            Intent buka = new Intent(register3.this, login.class);
                            startActivity(buka);
                            finish();
                        } else if (response.body().kode ==2 ) {
                            Toast.makeText(register3.this, "Registrasi gagal", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(register3.this, "gk ngapa ngapa in", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponRegister3> call, Throwable t) {
                        Toast.makeText(register3.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                pass2.setError("Password Tidak Sama");
                pass2.requestFocus();
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}