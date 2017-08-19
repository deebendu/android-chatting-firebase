package com.uii.academico.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.uii.academico.R;
import com.uii.academico.Utility.BlurBuilder;
import com.uii.academico.Utility.Utils;
import com.uii.academico.Volley.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BuatJadwalActivity extends AppCompatActivity {

    String namaBimbingan, idBimbingan, idSayaDosen, fotoMhs;

    LinearLayout ll_waktu, ll_tanggal, ll_lokasi;
    RelativeLayout rl_jadwalTersimpan;

    // EditText dan TextView Tersimpan
    TextView sWaktu, sTanggal, sLokasi, et_setWaktu, et_setTanggal, et_setLokasi;

    Button btn_simpan, btn_riwayatBimbingan;

    ProgressBar progressBar;

    int tgl, bln, thn;
    int jam, menit;
    String sebutanWaktu, lokasi;

    Calendar kalender = Calendar.getInstance();

    // Variable untuk di POST kan ke DB
    String cWaktu, cTanggal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_jadwal);

        // Tangkap nama dan id dari Extra Intent sebelumnya
        namaBimbingan = getIntent().getExtras().getString("nama");
        idBimbingan = getIntent().getExtras().getString("id");
        fotoMhs = getIntent().getExtras().getString("foto");

        // Ambil dari Shared Preference
        idSayaDosen = Utils.sharedPreferences.getString("id_user","");


        // PANGGIL SEMUA KOMPONEN

        ll_waktu = (LinearLayout) findViewById(R.id.LL_set_waktu);
        ll_tanggal = (LinearLayout) findViewById(R.id.LL_set_tanggal);
        ll_lokasi = (LinearLayout) findViewById(R.id.LL_set_lokasi);

        rl_jadwalTersimpan = (RelativeLayout) findViewById(R.id.RL_jadwal_tersimpan);
        rl_jadwalTersimpan.setVisibility(View.GONE);

        et_setWaktu = (TextView) findViewById(R.id.ET_waktu);
        et_setTanggal = (TextView) findViewById(R.id.ET_tanggal);
        et_setLokasi = (TextView) findViewById(R.id.ET_lokasi);

        btn_simpan = (Button) findViewById(R.id.BT_simpanJadwal);
        btn_riwayatBimbingan = (Button) findViewById(R.id.BT_riwayat_bimbingan);

        sWaktu = (TextView) findViewById(R.id.ET_sWaktu);
        sTanggal = (TextView) findViewById(R.id.ET_sTanggal);
        sLokasi = (TextView) findViewById(R.id.ET_sLokasi);

        progressBar = (ProgressBar) findViewById(R.id.PB_progressSimpan);
        progressBar.setVisibility(View.GONE);


//      ON CLICK LISTENER

        ll_waktu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(BuatJadwalActivity.this, timePicker, 0, 0, true).show();
            }
        });

        ll_tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(BuatJadwalActivity.this, datePicker, kalender.get(Calendar.YEAR), kalender.get(Calendar.MONTH), kalender.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ll_lokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLokasi();
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                btn_simpan.setVisibility(View.GONE);

                if (cWaktu == null || cTanggal == null || lokasi == null){

                    Toast.makeText(BuatJadwalActivity.this,"Isi semua form terlebih dahulu", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btn_simpan.setVisibility(View.VISIBLE);

                }else {

                    RequestBuatJadwal(idBimbingan, idSayaDosen, cWaktu, cTanggal, lokasi);

//                    Toast.makeText(BuatJadwalActivity.this, "waktu "+cWaktu+" tanggal "+ll_tanggal,Toast.LENGTH_SHORT).show();

                }

            }
        });

        btn_riwayatBimbingan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keRiwayat = new Intent(BuatJadwalActivity.this, RiwayatBimbinganActivity.class);

                // bawa id dan nama ke class chat
                keRiwayat.putExtra("nama",namaBimbingan);
                keRiwayat.putExtra("id",idBimbingan);
                keRiwayat.putExtra("foto", fotoMhs);
                startActivity(keRiwayat);
            }
        });



        TextView Nama = (TextView) findViewById(R.id.TV_namaMhsBuatJadwal);
        TextView NIM = (TextView) findViewById(R.id.TV_nimMhsBuatJadwal);
        final NetworkImageView framefotoMhs = (NetworkImageView) findViewById(R.id.fotoProfilMhs);

        Nama.setText(namaBimbingan);
        NIM.setText(idBimbingan);

        ImageLoader imageLoader = MySingleton.getInstance(this).getImageLoader();

        // Cargo image loader (progress bar loading muncul ketika image blm terambil)

        if (imageLoader != null && fotoMhs != null) {
            imageLoader.get(fotoMhs, new ImageLoader.ImageListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        //fotoProfil.setVisibility(View.VISIBLE);
                        framefotoMhs.setImageBitmap(response.getBitmap());
                        //progressBar.setVisibility(View.GONE);
                    }

                }
            });

            // SET Foto Profil Mahasiswa di RiwayatActivity
            framefotoMhs.setImageUrl(fotoMhs, imageLoader);
        }

    }


//  ========= BUAT TIME PICKER =========

    TimePickerDialog.OnTimeSetListener timePicker = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            jam = hourOfDay;
            menit = minute;

            if (3<=jam && jam<=10){
                sebutanWaktu = "Pagi";
            }else if (11<=jam && jam<=14){
                sebutanWaktu = "Siang";
            }else if (15<=jam && jam<=18){
                sebutanWaktu = "Sore";
            }else{
                sebutanWaktu = "Malam";
            }

            if (menit < 10) {

                if (jam < 10){
                    // cWaktu = convert/sesuaikan format waktu di DB
                    cWaktu = "0" + jam + ":0" + menit + "  " + sebutanWaktu;
                    et_setWaktu.setText("0" + jam + ":0" + menit + "  " + sebutanWaktu);
                    sWaktu.setText("0" + jam + ":0" + menit + "  " + sebutanWaktu);
                }else {
                    cWaktu = jam + ":0" + menit + "  " +sebutanWaktu;
                    et_setWaktu.setText(jam + ":0" + menit + "  " + sebutanWaktu);
                    sWaktu.setText(jam + ":0" + menit + "  " + sebutanWaktu);
                }

            } else {
                if (jam < 10){
                    cWaktu = "0" + jam + ":" + menit + "  " + sebutanWaktu;
                    et_setWaktu.setText("0" + jam + ":" + menit + "  " + sebutanWaktu);
                    sWaktu.setText("0" + jam + ":" + menit + "  " + sebutanWaktu);
                }else {
                    cWaktu = "" + jam + ":" + menit + "  " + sebutanWaktu;
                    et_setWaktu.setText(jam + ":" + menit + "  " + sebutanWaktu);
                    sWaktu.setText(jam + ":" + menit + "  " + sebutanWaktu);
                }
            }

        }
    };


//  ======== BUAT DATE PICKER ========

    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            tgl = dayOfMonth;
            bln = monthOfYear + 1;
            thn = year;

            //Sesuaikan format tanggal di DB
            cTanggal = tgl + "-" + bln + "-" + thn;

            et_setTanggal.setText(tgl + " - " + bln + " - " + thn);
            sTanggal.setText(tgl + " - " + bln + " - " + thn);


        }
    };



    public void setLokasi() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Lokasi");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint("ketik lokasi bimbingan...");
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        lokasi = input.getText().toString();
                        sLokasi.setText(input.getText().toString());
                        et_setLokasi.setText(input.getText().toString());

                    }
                });

        alertDialog.setNegativeButton("Batal",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }


    public void RequestBuatJadwal (final String id_Mhs, final String id_Dosen, final String waktu, final String tanggal, final String lokasinya) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.BUATJADWAL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject res = new JSONObject(response);
                            boolean jsonResponse = res.getBoolean("error");


                            if (!jsonResponse){

                                rl_jadwalTersimpan.setVisibility(View.VISIBLE);
                                Toast.makeText(BuatJadwalActivity.this, "Berhasil Tersimpan", Toast.LENGTH_SHORT).show();

                                // Supaya setelah submit ke DB tidak submit lagi (clear inputan)
                                // kosongkan EditText pada BuatJadwalActivity

                                et_setWaktu.setText(null);
                                et_setTanggal.setText(null);
                                et_setLokasi.setText(null);
                                cWaktu = null;
                                cTanggal = null;
                                lokasi = null;

                            }else{
                                Toast.makeText(BuatJadwalActivity.this, "Gagal menyimpan jadwal", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            Toast.makeText(BuatJadwalActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);
                        btn_simpan.setVisibility(View.VISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        progressBar.setVisibility(View.GONE);
                        btn_simpan.setVisibility(View.VISIBLE);
                        Toast.makeText(BuatJadwalActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                // the POST parameters:
                params.put("id_mhs", id_Mhs);
                params.put("id_dosen", id_Dosen);
                params.put("waktu", waktu);
                params.put("tanggal", tanggal);
                params.put("lokasi", lokasinya);

                Log.e("MHS ", id_Mhs);
                Log.e("DOSEN ", id_Dosen);
                Log.e("WAKTU ", waktu);
                Log.e("TANGGAL ", tanggal);
                Log.e("LOKASI ", lokasinya);

                return params;
            }
        };


        //Mengatasi Bug : post terkirim dua kali atau lebih
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(this).addToRequestQueue(postRequest);
    }


}
