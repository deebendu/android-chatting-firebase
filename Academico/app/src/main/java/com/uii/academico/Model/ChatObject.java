package com.uii.academico.Model;

/**
 * Created by Fakhrus on 4/26/16.
 */
public class ChatObject {

    // Tipe untuk menentukan pesan dikirim (kanan) atau diterima (kiri)

    String pesan, pengirimPesan, namaPengirim, waktuPesan;

    public ChatObject(String pesan, String pengirimPesan, String namaPengirim, String waktuPesan) {
        this.pesan = pesan;
        this.pengirimPesan = pengirimPesan;
        this.namaPengirim = namaPengirim;
        this.waktuPesan = waktuPesan;
    }

    public String getPesan() {
        return pesan;
    }

    public String getPengirimPesan() {
        return pengirimPesan;
    }

    public String getNamaPengirim() {
        return namaPengirim;
    }

    public String getWaktuPesan() {
        return waktuPesan;
    }
}
