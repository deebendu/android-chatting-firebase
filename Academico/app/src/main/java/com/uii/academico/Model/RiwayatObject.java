package com.uii.academico.Model;

/**
 * Created by Fakhrus on 5/17/16.
 */
public class RiwayatObject {

    String waktu, tanggal, lokasi, konfirm_mhs, konfirm_dosen, id_riwayat, topiknya, hasil_bimbingan;

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getKonfirm_mhs() {
        return konfirm_mhs;
    }

    public void setKonfirm_mhs(String konfirm_mhs) {
        this.konfirm_mhs = konfirm_mhs;
    }

    public String getKonfirm_dosen() {
        return konfirm_dosen;
    }

    public void setKonfirm_dosen(String konfirm_dosen) {
        this.konfirm_dosen = konfirm_dosen;
    }

    public String getId_riwayat() {
        return id_riwayat;
    }

    public void setId_riwayat(String id_riwayat) {
        this.id_riwayat = id_riwayat;
    }

    public String getTopiknya() {

        String tampilTopik;

        if (topiknya.isEmpty()){
            tampilTopik = "Belum Ditentukan Mahasiswa";
        }else {
            tampilTopik = topiknya;
        }

        return tampilTopik;
    }

    public void setTopiknya(String topiknya) {
        this.topiknya = topiknya;
    }

    public String getHasil_bimbingan() {
        return hasil_bimbingan;
    }

    public void setHasil_bimbingan(String hasil_bimbingan) {
        this.hasil_bimbingan = hasil_bimbingan;
    }
}
