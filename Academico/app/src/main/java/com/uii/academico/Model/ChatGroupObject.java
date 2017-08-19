package com.uii.academico.Model;

/**
 * Created by Fakhrus on 4/26/16.
 */
public class ChatGroupObject {

    // Tipe untuk menentukan pesan dikirim (kanan) atau diterima (kiri)

    String idPengirim, pesan, namaPengirim, fotoProfil, waktuPesan;


    public ChatGroupObject(String idPengirim, String pesan, String namaPengirim, String fotoProfil, String waktuPesan) {
        this.idPengirim = idPengirim;
        this.pesan = pesan;
        this.namaPengirim = namaPengirim;
        this.fotoProfil = fotoProfil;
        this.waktuPesan = waktuPesan;
    }

    public String getIdPengirim() {
        return idPengirim;
    }

    public String getPesan() {
        return pesan;
    }

    public String getNamaPengirim() {
        return namaPengirim;
    }

    public String getFotoProfil() {
        return fotoProfil;
    }

    public String getWaktuPesan() {
        return waktuPesan;
    }
}
