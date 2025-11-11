package com.ELayang.Desa.Surat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.database.Cursor;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Surat.ModelSkk;
import com.ELayang.Desa.DataModel.Surat.ResponSkk;
import com.ELayang.Desa.R;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import okhttp3.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SKK extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private static final String TAG = "SKKActivity";

    Spinner spJenisKelamin;
    EditText eNama, eAgama, eTTL, eAlamat, eKewarganegaraan, eKeterangan;

    Button btnKirim, btnUpdate, btnChooseFile;

    String username, fileNameSaved = "";
    String currentNoPengajuan = null;

    ProgressDialog progressDialog;
    DatePickerDialog picker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surat_kehilangan);

        SharedPreferences sp = getSharedPreferences("prefLogin", Context.MODE_PRIVATE);
        username = sp.getString("username","");

        eNama = findViewById(R.id.e_nama_pelapor);
        eAgama = findViewById(R.id.e_agama);
        spJenisKelamin = findViewById(R.id.e_jenis_kelamin);
        eTTL = findViewById(R.id.e_tempat_tanggal_lahir);
        eAlamat = findViewById(R.id.e_alamat_pelapor);
        eKewarganegaraan = findViewById(R.id.e_kewarganegaraan);
        eKeterangan = findViewById(R.id.e_keterangan);

        btnKirim = findViewById(R.id.kirim);
        btnUpdate = findViewById(R.id.update);
        btnChooseFile = findViewById(R.id.btn_choose_file);

        eKewarganegaraan.setText("WNI");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Laki-laki", "Perempuan"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spJenisKelamin.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memproses...");
        progressDialog.setCancelable(false);

        Intent intent = getIntent();
        currentNoPengajuan = intent.getStringExtra("no_pengajuan");

        if(currentNoPengajuan != null){
            btnKirim.setVisibility(View.GONE);
            loadData(currentNoPengajuan);
        } else {
            btnUpdate.setVisibility(View.GONE);
        }

        btnChooseFile.setOnClickListener(v -> chooseFile());

        eTTL.setOnClickListener(v -> showDatePicker());

        btnKirim.setOnClickListener(v -> {
            if(isValid()) konfirmasiKirim();
        });

        btnUpdate.setOnClickListener(v -> {
            if(isValid()) konfirmasiUpdate(currentNoPengajuan);
        });
    }


    /*==================== VALIDASI ====================*/

    private boolean isValid(){
        if(eNama.getText().toString().trim().isEmpty()){
            eNama.setError("Nama wajib diisi");
            return false;
        }
        if(eAlamat.getText().toString().trim().isEmpty()){
            eAlamat.setError("Alamat wajib diisi");
            return false;
        }
        if(eKeterangan.getText().toString().trim().isEmpty()){
            eKeterangan.setError("Keterangan kehilangan wajib diisi");
            return false;
        }
        return true;
    }

    private void showDatePicker(){
        Calendar cal = Calendar.getInstance();
        picker = new DatePickerDialog(SKK.this, (view, y, m, d) -> {
            cal.set(y, m, d);
            eTTL.setText(new SimpleDateFormat("dd MMMM yyyy", new Locale("id","ID")).format(cal.getTime()));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        picker.show();
    }


    /*==================== KIRIM ====================*/

    private void konfirmasiKirim(){
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Kirim surat kehilangan?")
                .setPositiveButton("Kirim", (d,w)-> kirim())
                .setNegativeButton("Batal",null)
                .show();
    }

    private void kirim(){
        progressDialog.show();
        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);

        Call<ResponSkk> call = api.kirimSkk(
                rb(username),
                rb(eNama.getText().toString()),
                rb(eAgama.getText().toString()),
                rb(spJenisKelamin.getSelectedItem().toString()),
                rb(eTTL.getText().toString()),
                rb(eAlamat.getText().toString()),
                rb(eKewarganegaraan.getText().toString()),
                rb(eKeterangan.getText().toString()),
                getFilePart()
        );

        call.enqueue(DefaultCallback("Berhasil mengirim surat"));
    }


    /*==================== LOAD DATA ====================*/

    private void loadData(String no){
        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        api.ambilSkk(no,"0").enqueue(new Callback<ResponSkk>() {
            @Override public void onResponse(Call<ResponSkk> call, Response<ResponSkk> res){
                if(res.body()!=null && res.body().isKode()){
                    ModelSkk d = res.body().getData().get(0);
                    eNama.setText(d.getNama());
                    eAgama.setText(d.getAgama());
                    spJenisKelamin.setSelection(d.getJenis_kelamin().equals("Laki-laki")?0:1);
                    eTTL.setText(d.getTempat_tanggal_lahir());
                    eAlamat.setText(d.getAlamat());
                    eKewarganegaraan.setText(d.getKewarganegaraan());
                    eKeterangan.setText(d.getKeterangan());
                }
            }
            @Override public void onFailure(Call<ResponSkk> call, Throwable t){}
        });
    }


    /*==================== UPDATE ====================*/

    private void konfirmasiUpdate(String no){
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Simpan perubahan?")
                .setPositiveButton("Update",(d,w)->update(no))
                .setNegativeButton("Batal",null)
                .show();
    }

    private void update(String no){
        progressDialog.show();
        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);

        api.updateSkk(
                rb(no), rb("1"),
                rb(eNama.getText().toString()),
                rb(eAgama.getText().toString()),
                rb(spJenisKelamin.getSelectedItem().toString()),
                rb(eTTL.getText().toString()),
                rb(eAlamat.getText().toString()),
                rb(eKewarganegaraan.getText().toString()),
                rb(eKeterangan.getText().toString()),
                getFilePart()
        ).enqueue(DefaultCallback("Berhasil update"));
    }


    /*==================== FILE ====================*/

    private void chooseFile(){
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("*/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(i,"Pilih File"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==PICK_FILE_REQUEST && resultCode==RESULT_OK && data!=null){
            Uri uri = data.getData();
            fileNameSaved = getFileName(uri);
            saveFile(uri);
            btnChooseFile.setText("File: " + fileNameSaved);
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri){
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        if(cursor!=null && cursor.moveToFirst()){
            return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        }
        return null;
    }

    private void saveFile(Uri uri){
        try(InputStream is = getContentResolver().openInputStream(uri);
            OutputStream os = new FileOutputStream(new File(getFilesDir(), fileNameSaved))){

            byte[] buf = new byte[1024];
            int len;
            while((len=is.read(buf))>0) os.write(buf,0,len);

        } catch(Exception ignored){}
    }

    private MultipartBody.Part getFilePart(){
        if(fileNameSaved.isEmpty()) return null;
        File file = new File(getFilesDir(), fileNameSaved);
        RequestBody rb = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        return MultipartBody.Part.createFormData("file", file.getName(), rb);
    }


    /*==================== UTIL ====================*/

    private Callback<ResponSkk> DefaultCallback(String successMessage){
        return new Callback<ResponSkk>() {
            @Override public void onResponse(Call<ResponSkk> call, Response<ResponSkk> response){
                progressDialog.dismiss();
                Toast.makeText(SKK.this, successMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
            @Override public void onFailure(Call<ResponSkk> call, Throwable t){
                progressDialog.dismiss();
                Toast.makeText(SKK.this, "Gagal: "+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

    private RequestBody rb(String v){
        return RequestBody.create(MediaType.parse("text/plain"), v == null ? "" : v);
    }
}
