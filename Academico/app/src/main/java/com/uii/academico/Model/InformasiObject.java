package com.uii.academico.Model;

import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by Fakhrus on 4/10/16.
 */
public class InformasiObject {

    private String idInfo, topik, deskripsi, tanggal, gambarInformasi;

    public void setIdInfo(String idInfo) {
        this.idInfo = idInfo;
    }

    public void setTopik(String topik) {
        this.topik = topik;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public void setGambarInformasi(String gambarInformasi) {
        this.gambarInformasi = gambarInformasi;
    }

    public String getIdInfo() {
        return idInfo;
    }

    public String getTopik() {
        return topik;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getGambarInformasi() {
        return gambarInformasi;
    }
}
