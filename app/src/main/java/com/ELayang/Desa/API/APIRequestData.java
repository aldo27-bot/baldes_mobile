package com.ELayang.Desa.API;

import com.ELayang.Desa.DataModel.Akun.ResponFotoProfil;
import com.ELayang.Desa.DataModel.Akun.ResponUpdate;
import com.ELayang.Desa.DataModel.Lupa_Password.ResponPassword1;
import com.ELayang.Desa.DataModel.Lupa_Password.ResponPassword2;
import com.ELayang.Desa.DataModel.Akun.ResponLogin;
import com.ELayang.Desa.DataModel.Notifikasi.ResponNotifikasi;
import com.ELayang.Desa.DataModel.Notifikasi.ResponNotifikasiAspirasi;
import com.ELayang.Desa.DataModel.Register.ResponDelete;
import com.ELayang.Desa.DataModel.Register.ResponOTP;
import com.ELayang.Desa.DataModel.Register.ResponRegister1;
import com.ELayang.Desa.DataModel.Register.ResponRegister2;
import com.ELayang.Desa.DataModel.Register.ResponRegister3;
import com.ELayang.Desa.DataModel.StatusDasboardRespon;
import com.ELayang.Desa.DataModel.Surat.ResponSkck;
import com.ELayang.Desa.DataModel.ResponSurat;
import com.ELayang.Desa.DataModel.RiwayatSurat.ResponDiajukan;
import com.ELayang.Desa.DataModel.RiwayatSurat.ResponSelesai;
import com.ELayang.Desa.DataModel.Surat.ResponSktm;
import com.ELayang.Desa.DataModel.Surat.ResponSuratijin;
import com.ELayang.Desa.DataModel.AspirasiResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;


public interface APIRequestData {
    //buat ngambil data dari API/webservice retrieve.php
    @GET("Retrieve.php")
    Call<ResponLogin> ardRetrieveData();

    @POST("ambil_notifikasi_aspirasi.php")
    @FormUrlEncoded
    Call<ResponNotifikasi> getNotifikasi(@Field("username") String username);

    @POST("notifikasi_popup_aspirasi.php")
    @FormUrlEncoded
    Call<ResponNotifikasi> notifikasi_popup_aspirasi(@Field("username") String username);

    @POST("notifikasi_aspirasi.php")
    @FormUrlEncoded
    Call<ResponNotifikasi> notifikasi_aspirasi(@Field("username") String username);

    @FormUrlEncoded
    @POST("login.php")
    Call<ResponLogin> ardLogin(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("Login.php")
    Call<ResponLogin> login(
            @Field("username") String username,
            @Field("password") String password
    );

    @GET("surat.php")
    Call<ResponSurat> surat();

    @GET("aspirasi.php")
    Call<AspirasiResponse> getAspirasi();
    @Multipart
    @POST("simpan_aspirasi.php")
    Call<AspirasiResponse> kirimAspirasi(
            @Part("username") RequestBody username,
            @Part("judul") RequestBody judul,
            @Part("kategori") RequestBody kategori,
            @Part("deskripsi") RequestBody deskripsi,
            @Part MultipartBody.Part foto // foto boleh null
    );

    @FormUrlEncoded
    @POST("ambil_notifikasi_aspirasi.php")
    Call<ResponNotifikasiAspirasi> getNotifikasiAspirasi(
            @Field("username") String username
    );

    @Multipart
    @POST("surat/skck.php")
    Call<ResponSkck> uploadFile(
            @Part("nama") RequestBody nama,
            @Part("nik") RequestBody nik,
            @Part("tempat_tanggal_lahir") RequestBody tempat_tanggal_lahir,
            @Part("kebangsaan") RequestBody kebangsaan,
            @Part("agama") RequestBody agama,
            @Part("status_perkawinan") RequestBody status,
            @Part("pekerjaan") RequestBody pekerjaan,
            @Part("tempat_tinggal") RequestBody tempatTinggal,
            @Part("username") RequestBody username,
            @Part("jenis_kelamin") RequestBody jenis_kelamin,
            @Part MultipartBody.Part file
    );

    @Multipart
    @POST("updatesurat/skck.php")
    Call<ResponSkck> updatefileskck(
            @Part("no_pengajuan") RequestBody no_pengajuan,
            @Part("kode_surat") RequestBody kode_surat,
            @Part("nama") RequestBody nama,
            @Part("nik") RequestBody nik,
            @Part("tempat_tgl_lahir") RequestBody tempat_tgl_lahir,
            @Part("kebangsaan") RequestBody kebangsaan,
            @Part("agama") RequestBody agama,
            @Part("status_perkawinan") RequestBody status_perkawinan,
            @Part("pekerjaan") RequestBody pekerjaan,
            @Part("alamat") RequestBody alamat,
            @Part("jenis_kelamin") RequestBody jenis_kelamin,
            @Part MultipartBody.Part file
    );


    @FormUrlEncoded
    @POST("register/register1.php")
    Call<ResponRegister1> register1(
            @Field("username") String username,
            @Field("email") String email,
            @Field("nama") String nama

    );

    @FormUrlEncoded
    @POST("register/register2.php")
    Call<ResponRegister2> register2(
            @Field("username") String username,
            @Field("kode_otp") String kode_otp

    );

    @FormUrlEncoded
    @POST("register/register3.php")
    Call<ResponRegister3> register3(
            @Field("username") String username,
            @Field("email") String email,
            @Field("nama") String nama,
            @Field("kode_otp") String kode_otp,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("register/delete_akun.php")
    Call<ResponDelete> delete(
            @Field("username") String username
    );

    @FormUrlEncoded
    @POST("register/kode_otp.php")
    Call<ResponOTP> kirim_otp(
            @Field("username") String username
    );

    @FormUrlEncoded
    @POST("lupa_password/password1.php")
    Call<ResponPassword1> lupa_password1(
            @Field("username") String username
    );

    @FormUrlEncoded
    @POST("lupa_password/password2.php")
    Call<ResponPassword2> lupa_password2(
            @Field("kode_otp") String kode_otp,
            @Field("password") String password,
            @Field("username") String username
    );

    @FormUrlEncoded
    @POST("riwayat_surat/surat_proses.php")
    Call<ResponDiajukan> proses(
            @Field("username") String username
    );

    @FormUrlEncoded
    @POST("riwayat_surat/surat_selesai.php")
    Call<ResponSelesai> selesai(
            @Field("username") String username
    );

    @Multipart
    @POST("surat/surat_ijin.php")
    Call<ResponSuratijin> suratijin(
            @Part("username") RequestBody username,
            @Part("nama") RequestBody nama,
            @Part("nik") RequestBody nik,
            @Part("jenis_kelamin") RequestBody jenis_kelamin,
            @Part("tempat_tanggal_lahir") RequestBody tempat_tanggal_lahir,
            @Part("kewarganegaraan") RequestBody kewarganegaraan,
            @Part("agama") RequestBody agama,
            @Part("pekerjaan") RequestBody pekerjaan,
            @Part("alamat") RequestBody alamat,
            @Part("tempat_kerja") RequestBody tempat_kerja,
            @Part("bagian") RequestBody bagian,
            @Part("tanggal") RequestBody tanggal,
            @Part("alasan") RequestBody alasan,
            @Part MultipartBody.Part file
    );

    @Multipart
    @POST("surat/sktm.php")
    Call<ResponSktm> sktm(
            @Part("username") RequestBody username,

            //bapak
            @Part("nama_bapak") RequestBody nama_bapak,
            @Part("tempat_tanggal_lahir_bapak") RequestBody tempat_tanggal_lahir_bapak,
            @Part("pekerjaan_bapak") RequestBody pekerjaan_bapak,
            @Part("alamat_bapak") RequestBody alamat_bapak,

            //ibu
            @Part("nama_ibu") RequestBody nama_ibu,
            @Part("tempat_tanggal_lahir_ibu") RequestBody tempat_tanggal_lahir_ibu,
            @Part("pekerjaan_ibu") RequestBody pekerjaan_ibu,
            @Part("alamat_ibu") RequestBody alamat_ibu,

            //anak
            @Part("nama") RequestBody nama_anak,
            @Part("tempat_tanggal_lahir_anak") RequestBody tempat_tanggal_lahir_anak,
            @Part("jenis_kelamin_anak") RequestBody jenis_kelamin_anak,
            @Part("alamat") RequestBody alamat,
            @Part MultipartBody.Part file
    );

    @Multipart
    @POST("surat/beda_nama.php")
    Call<ResponBedaNama> bedaNama(
            @Part("username") RequestBody username,
            @Part("kode_surat") RequestBody kode_surat,

            @Part("nama_lama") RequestBody nama_lama,
            @Part("nama_baru") RequestBody nama_baru,
            @Part("nik") RequestBody nik,
            @Part("alamat") RequestBody alamat,
            @Part("tempat_tanggal_lahir") RequestBody tempat_tanggal_lahir,
            @Part("pekerjaan") RequestBody pekerjaan,
            @Part("keterangan") RequestBody keterangan,

            // bagian file upload (bukti pendukung, foto, atau dokumen)
            @Part MultipartBody.Part file
    );


    @FormUrlEncoded
    @POST("update_akun.php")
    Call<ResponUpdate> update_akun(
            @Field("username") String username,
            @Field("email") String email,
            @Field("password") String password,
            @Field("nama") String nama
    );

    @Multipart
    @POST("update_akun.php")
    Call<ResponUpdate> updateAkunWithImage(
            @Part("username") RequestBody username,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("nama") RequestBody nama,
            @Part MultipartBody.Part profile_image // Menambahkan bagian gambar
    );

    @Multipart
    @POST("update_akun.php")
    Call<ResponUpdate> updateAkunWithoutImage(
            @Part("username") RequestBody username,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("nama") RequestBody nama
    );

    @GET("ambil_foto_profil.php") // Sesuaikan dengan URL API Anda
    Call<ResponFotoProfil> getFotoProfil(
            @Query("username") String username)
    ;

    @FormUrlEncoded
    @POST("dashboard.php")
    Call<StatusDasboardRespon> dashboard(
            @Field("username") String usernmae
    );

    @FormUrlEncoded
    @POST("logingoogle.php")
    Call<ResponLogin> logingoogle(
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("updatesurat/skck.php")
    Call<ResponSkck> ambilskck(
            @Field("no_pengajuan") String no,
            @Field("kode") String kode
    );

    @FormUrlEncoded
    @POST("updatesurat/sktm.php")
    Call<ResponSktm> ambilsktm(
            @Field("no_pengajuan") String no,
            @Field("kode") String kode
    );
    @Multipart
    @POST("updatesurat/sktm.php")
    Call<ResponSktm> updatesktm(
            @Part("no_pengajuan") RequestBody no_pengajuan,
            @Part("kode") RequestBody kode,

            //bapak
            @Part("nama_bapak") RequestBody nama_bapak,
            @Part("tempat_tanggal_lahir_bapak") RequestBody tempat_tanggal_lahir_bapak,
            @Part("pekerjaan_bapak") RequestBody pekerjaan_bapak,
            @Part("alamat_bapak") RequestBody alamat_bapak,

            //ibu
            @Part("nama_ibu") RequestBody nama_ibu,
            @Part("tempat_tanggal_lahir_ibu") RequestBody tempat_tanggal_lahir_ibu,
            @Part("pekerjaan_ibu") RequestBody pekerjaan_ibu,
            @Part("alamat_ibu") RequestBody alamat_ibu,

            //anak
            @Part("nama") RequestBody nama_anak,
            @Part("tempat_tanggal_lahir_anak") RequestBody tempat_tanggal_lahir_anak,
            @Part("jenis_kelamin_anak") RequestBody jenis_kelamin_anak,
            @Part("alamat") RequestBody alamat,
            @Part MultipartBody.Part file
    );

//    @Multipart
//    @POST("updatesurat/skck.php")
//    Call<ResponSkck> updatefileskck(
//            @Part("no_pengajuan") RequestBody no_pengajuan,
//            @Part("kode_surat") RequestBody kode_surat,
//            @Part("nama") RequestBody nama,
//            @Part("nik") RequestBody nik,
//            @Part("tempat_tgl_lahir") RequestBody tempat_tgl_lahir,
//            @Part("kebangsaan") RequestBody kebangsaan,
//            @Part("agama") RequestBody agama,
//            @Part("status_perkawinan") RequestBody status_perkawinan,
//            @Part("pekerjaan") RequestBody pekerjaan,
//            @Part("alamat") RequestBody alamat,
//            @Part("jenis_kelamin") RequestBody jenis_kelamin,
//            @Part MultipartBody.Part file
//    );

    @FormUrlEncoded
    @POST("updatesurat/surat_ijin.php")
    Call<ResponSuratijin> ambilsuratijin(
            @Field("no_pengajuan") String no,
            @Field("kode") String kode
    );
    @Multipart
    @POST("updatesurat/surat_ijin.php")
    Call<ResponSuratijin> updatesuratijin(
            @Part("no_pengajuan") RequestBody no,
            @Part("kode") RequestBody kode,
            @Part("nama") RequestBody nama,
            @Part("nik") RequestBody nik,
            @Part("jenis_kelamin") RequestBody jenis_kelamin,
            @Part("tempat_tanggal_lahir") RequestBody tempat_tanggal_lahir,
            @Part("kewarganegaraan") RequestBody kewarganegaraan,
            @Part("agama") RequestBody agama,
            @Part("pekerjaan") RequestBody pekerjaan,
            @Part("alamat") RequestBody alamat,
            @Part("tempat_kerja") RequestBody tempat_kerja,
            @Part("bagian") RequestBody bagian,
            @Part("tanggal") RequestBody tanggal,
            @Part("alasan") RequestBody alasan,
            @Part MultipartBody.Part file
    );

    @FormUrlEncoded
    @POST("updatesurat/beda_nama.php")
    Call<ResponBedaNama> ambilBedaNama(
            @Field("no_pengajuan") String no_pengajuan,
            @Field("kode_surat") String kode_surat
    );

    @Multipart
    @POST("updatesurat/beda_nama.php")
    Call<ResponBedaNama> updateBedaNama(
            @Part("no_pengajuan") RequestBody no_pengajuan,
            @Part("kode_surat") RequestBody kode_surat,

            @Part("nama_lama") RequestBody nama_lama,
            @Part("nama_baru") RequestBody nama_baru,
            @Part("nik") RequestBody nik,
            @Part("alamat") RequestBody alamat,
            @Part("tempat_tanggal_lahir") RequestBody tempat_tanggal_lahir,
            @Part("pekerjaan") RequestBody pekerjaan,
            @Part("keterangan") RequestBody keterangan,
            @Part("username") RequestBody username,

            // bagian file upload
            @Part MultipartBody.Part file
    );

//    @Multipart
//    @POST("updatesurat/skck.php")
//    Call<ResponSkck> updatefileskck(
//            @Part("no_pengajuan") RequestBody no,
//            @Part("kode") RequestBody kode,
//            @Part("nama") RequestBody nama,
//            @Part("nik") RequestBody nik,
//            @Part("tempat_tanggal_lahir") RequestBody tempat_tanggal_lahir,
//            @Part("kebangsaan") RequestBody kebangsaan,
//            @Part("agama") RequestBody agama,
//            @Part("status_perkawinan") RequestBody status,
//            @Part("pekerjaan") RequestBody pekerjaan,
//            @Part("tempat_tinggal") RequestBody tinggal,
//            @Part("jenis_kelamin") RequestBody jenis_kelamin,
//            @Part MultipartBody.Part file
//    );

    @FormUrlEncoded
    @POST("notifikasi.php")
    Call<ResponNotifikasi> notif(
            @Field("username") String username
    );
    @FormUrlEncoded
    @POST("notifikasi_popup.php")
    Call<ResponNotifikasi> notifikasi_popup(
            @Field("username") String username
    );
};