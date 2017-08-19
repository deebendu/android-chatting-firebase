package com.uii.academico.Model;

import java.lang.annotation.Target;

/**
 * Created by Fakhrus on 5/17/16.
 */
public class DataAkademikObject {

    String idDataAkademik, semester, ip, sks, kp, ta, cuti, btaq, kkn, cept, kegiatan, validasi_dosen, komentar;

    public String getIdDataAkademik() {
        return idDataAkademik;
    }

    public void setIdDataAkademik(String idDataAkademik) {
        this.idDataAkademik = idDataAkademik;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSks() {
        return sks;
    }

    public void setSks(String sks) {
        this.sks = sks;
    }

    public String getKp() {

        String KPnya;

        if(kp.equals("1")){
            KPnya = "Sudah";
        }else{
            KPnya = "Belum";
        }

        return KPnya;
    }

    public void setKp(String kp) {
        this.kp = kp;
    }

    public String getTa() {

        String TAnya;

        if(ta.equals("1")){
            TAnya = "Sudah";
        }else{
            TAnya = "Belum";
        }

        return TAnya;
    }

    public void setTa(String ta) {
        this.ta = ta;
    }

    public String getCuti() {

        String Cutinya;

        if(cuti.equals("1")){
            Cutinya = "Ambil";
        }else{
            Cutinya = "Tidak";
        }

        return Cutinya;
    }

    public void setCuti(String cuti) {
        this.cuti = cuti;
    }


    public String getBtaq() {
        String BTAQnya;

        if(btaq.equals("1")){
            BTAQnya = "Lulus";
        }else{
            BTAQnya = "Belum";
        }

        return BTAQnya;
    }

    public void setBtaq(String btaq) {
        this.btaq = btaq;
    }

    public String getKkn() {
        String KKNnya;

        if(kkn.equals("1")){
            KKNnya = "Sudah";
        }else{
            KKNnya = "Belum";
        }

        return KKNnya;
    }

    public void setKkn(String kkn) {
        this.kkn = kkn;
    }

    public String getCept() {
        String CEPTnya;

        if(cept.equals("1")){
            CEPTnya = "Lulus";
        }else{
            CEPTnya = "Belum";
        }

        return CEPTnya;
    }

    public void setCept(String cept) {
        this.cept = cept;
    }

    public String getKegiatan() {
        return kegiatan;
    }

    public void setKegiatan(String kegiatan) {
        this.kegiatan = kegiatan;
    }

    public String getValidasi_dosen() {
        return validasi_dosen;
    }

    public void setValidasi_dosen(String validasi_dosen) {
        this.validasi_dosen = validasi_dosen;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }
}
