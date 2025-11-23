package com.ELayang.Desa.API;

import com.ELayang.Desa.DataModel.Akun.ResponFotoProfil;
import com.ELayang.Desa.DataModel.Akun.ResponUpdate;
import com.ELayang.Desa.DataModel.Lupa_Password.ResponPassword1;
import com.ELayang.Desa.DataModel.Lupa_Password.ResponPassword2;
import com.ELayang.Desa.DataModel.Akun.ResponLogin;
import com.ELayang.Desa.DataModel.Notifikasi.ResponNotifikasi;
//import com.ELayang.Desa.DataModel.Notifikasi.ResponNotifikasiAspirasi;
import com.ELayang.Desa.DataModel.Notifikasi.ResponPopup;
import com.ELayang.Desa.DataModel.Register.ResponDelete;
import com.ELayang.Desa.DataModel.Register.ResponOTP;
import com.ELayang.Desa.DataModel.Register.ResponRegister1;
import com.ELayang.Desa.DataModel.Register.ResponRegister2;
import com.ELayang.Desa.DataModel.Register.ResponRegister3;
import com.ELayang.Desa.DataModel.StatusDasboardRespon;
import com.ELayang.Desa.DataModel.Surat.ResponDomisili;
import com.ELayang.Desa.DataModel.Surat.ResponSkck;
import com.ELayang.Desa.DataModel.ResponSurat;
import com.ELayang.Desa.DataModel.RiwayatSurat.ResponDiajukan;
import com.ELayang.Desa.DataModel.RiwayatSurat.ResponSelesai;
import com.ELayang.Desa.DataModel.Surat.ResponSkk;
import com.ELayang.Desa.DataModel.Surat.ResponSkm;
import com.ELayang.Desa.DataModel.Surat.ResponSktm;
import com.ELayang.Desa.DataModel.Surat.ResponSuratijin;
import com.ELayang.Desa.DataModel.Surat.ResponSkb;
import com.ELayang.Desa.DataModel.AspirasiResponse;
import com.ELayang.Desa.DataModel.Surat.ResponBedaNama;
import com.ELayang.Desa.DataModel.Surat.ResponSuratUsaha;

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
import retrofit2.http.Headers;



public interface APIRequestData {
    //buat ngambil data dari API/webservice retrieve.php
    @GET("Retrieve.php")
    Call<ResponLogin> ardRetrieveData();

//    @POST("ambil_notifikasi_aspirasi.php")
//    @FormUrlEncoded
//    Call<ResponNotifikasi> getNotifikasi(@Field("username") String username);

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

    @FormUrlEncoded
    @POST("suratproses.php")
    Call<ResponDiajukan> getRiwayatSurat(@Field("username") String username);


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

//    @FormUrlEncoded
//    @POST("ambil_notifikasi_aspirasi.php")
//    Call<ResponNotifikasiAspirasi> getNotifikasiAspirasi(
//            @Field("username") String username
//    );
//
//    @FormUrlEncoded
//    @POST("DatabaseMobile/kirim_notifikasi_user.php")
//    Call<ResponNotifikasiAspirasi> kirimNotifikasiUser(
//            @Field("id") int id,
//            @Field("status") String status,
//            @Field("tanggapan") String tanggapan
//    );

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
    @POST("surat/skm.php")
    Call<ResponSkm> uploadSKM(
            @Part("nama") RequestBody nama,
            @Part("alamat") RequestBody alamat,
            @Part("jenis_kelamin") RequestBody jk,
            @Part("tempat_tanggal_lahir") RequestBody ttl,
            @Part("pekerjaan") RequestBody pekerjaan,
            @Part("agama") RequestBody agama,
            @Part("kewarganegaraan") RequestBody kewarganegaraan,
            @Part("keterangan") RequestBody keterangan,
            @Part("username") RequestBody username,
            @Part MultipartBody.Part file
    );

    @Multipart
    @POST("surat/skb_kirim.php")
    Call<ResponSkb> addSuratBerkelakuanBaik(
            @Part("nama") RequestBody nama,
            @Part("nik") RequestBody nik,
            @Part("agama") RequestBody agama,
            @Part("tempat_tanggal_lahir") RequestBody ttl,
            @Part("pendidikan") RequestBody pendidikan,
            @Part("alamat_lengkap") RequestBody alamat,
            @Part("kode_surat") RequestBody kode_surat,
            @Part("id_pejabat_desa") RequestBody id_pejabat_desa,
            @Part("username") RequestBody username,
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
            @Part("nama") RequestBody nama,
            @Part("tempat_tanggal_lahir") RequestBody ttl,
            @Part("asal_sekolah") RequestBody asalSekolah,
            @Part("keperluan") RequestBody keperluan,
            @Part("nama_orangtua") RequestBody namaOrtu,
            @Part("nik_orangtua") RequestBody nikOrtu,
            @Part("alamat_orangtua") RequestBody alamatOrtu,
            @Part("tempat_tanggal_lahir_orangtua") RequestBody ttlOrtu,
            @Part("pekerjaan_orangtua") RequestBody pekerjaanOrtu,
            @Part("username") RequestBody username,
            @Part MultipartBody.Part file
    );

    // APIRequestData.java

    @Multipart
    @POST("surat/surat_beda_nama.php")
    Call<ResponBedaNama> bedaNama(
            @Part("username") RequestBody username,
            @Part("kode_surat") RequestBody kode_surat,
            @Part("nama") RequestBody nama,
            @Part("nama_baru") RequestBody nama_baru,
            @Part("nik") RequestBody nik,
            @Part("alamat") RequestBody alamat,
            @Part("tempat_tanggal_lahir") RequestBody tempat_tanggal_lahir, // KEY SELARAS
            @Part("pekerjaan") RequestBody pekerjaan,
            @Part("keterangan") RequestBody keterangan,
            @Part MultipartBody.Part file // Key 'file' selaras dengan $_FILES['file']
    );


    @Multipart
    @POST("surat/surat_domisili.php")
    Call<ResponDomisili> kirimSuratDomisili(
            @Part("nama") RequestBody nama,
            @Part("nik") RequestBody nik,
            @Part("tempat_tanggal_lahir") RequestBody ttl,
            @Part("alamat") RequestBody alamat,
            @Part("jenis_kelamin") RequestBody jk,
            @Part("pekerjaan") RequestBody pekerjaan,
            @Part("agama") RequestBody agama,
            @Part("status_perkawinan") RequestBody status,
            @Part("keterangan") RequestBody keterangan,
            @Part("username") RequestBody username,
            @Part MultipartBody.Part file  // field tetap 'file' di PHP
    );

    @FormUrlEncoded
    @POST("update_akun.php")
    Call<ResponUpdate> updateAkun(
            @Field("username") String username,
            @Field("nama") String nama,
            @Field("email") String email,
            @Field("profile_image_base64") String base64Image
    );


    @GET("ambil_foto_profil.php")
    Call<ResponFotoProfil> getProfile(@Query("username") String username);

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

    @Multipart
    @POST("surat/skk.php")
    Call<ResponSkk> kirimSkk(
            @Part("username") RequestBody username,
            @Part("nama") RequestBody nama,
            @Part("agama") RequestBody agama,
            @Part("jenis_kelamin") RequestBody jenis_kelamin,
            @Part("tempat_tanggal_lahir") RequestBody ttl,
            @Part("alamat") RequestBody alamat,
            @Part("kewarganegaraan") RequestBody kewarganegaraan,
            @Part("keterangan") RequestBody keterangan,
            @Part("kode_surat") RequestBody kode_surat,
            @Part MultipartBody.Part file

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
//    @Multipart
//    @POST("updatesurat/sktm.php")
//    Call<ResponSktm> updatesktm(
//            @Part("no_pengajuan") RequestBody no_pengajuan,
//            @Part("kode") RequestBody kode,
//
//            //bapak
//            @Part("nama_bapak") RequestBody nama_bapak,
//            @Part("tempat_tanggal_lahir_bapak") RequestBody tempat_tanggal_lahir_bapak,
//            @Part("pekerjaan_bapak") RequestBody pekerjaan_bapak,
//            @Part("alamat_bapak") RequestBody alamat_bapak,
//
//            //ibu
//            @Part("nama_ibu") RequestBody nama_ibu,
//            @Part("tempat_tanggal_lahir_ibu") RequestBody tempat_tanggal_lahir_ibu,
//            @Part("pekerjaan_ibu") RequestBody pekerjaan_ibu,
//            @Part("alamat_ibu") RequestBody alamat_ibu,
//
//            //anak
//            @Part("nama") RequestBody nama_anak,
//            @Part("tempat_tanggal_lahir_anak") RequestBody tempat_tanggal_lahir_anak,
//            @Part("jenis_kelamin_anak") RequestBody jenis_kelamin_anak,
//            @Part("alamat") RequestBody alamat,
//            @Part MultipartBody.Part file
//    );

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

    @Multipart
    @POST("surat/surat_usaha.php")
    Call<ResponSuratUsaha> suratUsaha(
            @Part("username") RequestBody username,
            @Part("nama") RequestBody nama,
            @Part("alamat") RequestBody alamat,
            @Part("keterangan_usaha") RequestBody keterangan_usaha,
            @Part("tempat_tanggal_lahir") RequestBody ttl,
            @Part("kode_surat") RequestBody kodeSurat,
            @Part MultipartBody.Part file   // OPSIONAL
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

    @Multipart
    @POST("surat/skb_kirim.php")
    Call<ResponSkb> kirimskb(
            @Part("nama") RequestBody nama,
            @Part("nik") RequestBody nik,
            @Part("agama") RequestBody agama,
            @Part("tempat_tanggal_lahir") RequestBody ttl,
            @Part("pendidikan") RequestBody pendidikan,
            @Part("alamat") RequestBody alamat,
            @Part MultipartBody.Part file,          // opsional
            @Part("keperluan") RequestBody keperluan, // baru ditambahkan
            @Part("kode_surat") RequestBody kode_surat,
            @Part("id_pejabat_desa") RequestBody id_pejabat_desa,
            @Part("username") RequestBody username
    );

    @FormUrlEncoded
    @POST("notifikasi.php")
    Call<ResponNotifikasi> getNotifikasi(
            @Field("username") String username
    );

    @FormUrlEncoded
    @POST("notifikasi_popup.php")
    Call<ResponPopup> getPopupNotifikasi(
            @Field("username") String username
    );
}