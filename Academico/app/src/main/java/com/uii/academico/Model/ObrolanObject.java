package com.uii.academico.Model;

/**
 * Created by Fakhrus on 5/27/16.
 */
public class ObrolanObject {

    //status pengirim hanya untuk history obrolan personal, group = null

    String idObrolan, namaObrolan, pesanTerakhir, tipeObrolan, urlFotoObrolan, statusPengirim;

    public String getIdObrolan() {
        return idObrolan;
    }

    public void setIdObrolan(String idObrolan) {
        this.idObrolan = idObrolan;
    }

    public String getNamaObrolan() {
        return namaObrolan;
    }

    public String getPesanTerakhir() {
        return pesanTerakhir;
    }

    public void setPesanTerakhir(String pesanTerakhir) {
        this.pesanTerakhir = pesanTerakhir;
    }

    public void setNamaObrolan(String namaObrolan) {
        this.namaObrolan = namaObrolan;
    }

    public String getTipeObrolan() {
        return tipeObrolan;
    }

    public void setTipeObrolan(String tipeObrolan) {
        this.tipeObrolan = tipeObrolan;
    }

    public String getUrlFotoObrolan() {
        return urlFotoObrolan;
    }

    public void setUrlFotoObrolan(String urlFotoObrolan) {
        this.urlFotoObrolan = urlFotoObrolan;
    }

    public String getStatusPengirim() {
        return statusPengirim;
    }

    public void setStatusPengirim(String statusPengirim) {
        this.statusPengirim = statusPengirim;
    }
}
