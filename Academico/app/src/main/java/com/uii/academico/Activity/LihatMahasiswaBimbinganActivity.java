package com.uii.academico.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.uii.academico.Adapter.AdapterDaftarBimbingan;
import com.uii.academico.Model.DaftarBimbinganObject;
import com.uii.academico.R;
import com.uii.academico.Utility.Utils;
import com.uii.academico.Volley.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LihatMahasiswaBimbinganActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private AdapterDaftarBimbingan adapter;
    private List<DaftarBimbinganObject> itemDaftarBimbingan = new ArrayList<>();

//    TextView Nama, NIM;

    String status, idSaya;
//    String namaBimbingan, idBimbingan, urlfotoMhs;
//    NetworkImageView fotoMhs;

    SwipeRefreshLayout swipeRefreshLayout;
    ProgressDialog progressKonfirm, progressHapus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_mahasiswa_bimbingan);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_riwayat);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                RequestAgendaBimbingan(idSaya, status);
            }
        });

        status = Utils.sharedPreferences.getString("status", "");
        idSaya = Utils.sharedPreferences.getString("id_user", "");


        // Panggil listview riwayat bimbingan
        ListView listRiwayat = (ListView) findViewById(R.id.daftar_bimbingan);

        // adapter riwayat (konteks, data)
        adapter = new AdapterDaftarBimbingan(LihatMahasiswaBimbinganActivity.this, itemDaftarBimbingan);
        listRiwayat.setAdapter(adapter);

        //hapus border listview
        listRiwayat.setDivider(null);

        // jika list item di klik
        listRiwayat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Setiap kali list di klik panggil method berikut :

                // ambil id mhs sbg id bimbingan
//                idBimbingan = itemDaftarBimbingan.get(position).getId_mhs();
                KonfirmasiAlertDialog(position);
            }
        });

        // jika list ditekan lama
        listRiwayat.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (status.equals("dosen")){    // jika dosen, dapat hapus riwayat
                    HapusRiwayat(position);
                    return true;
                }

                return false;
            }
        });

    }

    public void RequestAgendaBimbingan(final String idSaya, final String status) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.LIHATDAFTARBIMBINGAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("DAFTAR BIMBINGAN ", response);

                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            // Agar data tidak terduplikasi
                            itemDaftarBimbingan.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = jsonArray.getJSONObject(i);

                                // Membuat objek setiap kontak
                                DaftarBimbinganObject daftarBimbinganObject = new DaftarBimbinganObject();

                                daftarBimbinganObject.setId_agenda(obj.getString("id_jadwal"));
                                daftarBimbinganObject.setWaktu(obj.getString("waktu_bimbingan"));
                                daftarBimbinganObject.setTanggal(obj.getString("tanggal_bimbingan"));
                                daftarBimbinganObject.setLokasi(obj.getString("lokasi_bimbingan"));
                                daftarBimbinganObject.setTopiknya(obj.getString("topik_bimbingan"));

                                daftarBimbinganObject.setNama_mhs(obj.getString("nama_mhs"));
                                daftarBimbinganObject.setId_mhs(obj.getString("nim_mhs"));
                                daftarBimbinganObject.setProfil_mhs(obj.getString("foto_mhs"));



                                //menambahkan dokumen kedalam array dokumen
                                itemDaftarBimbingan.add(daftarBimbinganObject);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LihatMahasiswaBimbinganActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_SHORT).show();
                        }

                        //lapor ke adapter jika respon selesai (ada perubahan)
                        adapter.notifyDataSetChanged();

                        // tutup refresh
                        swipeRefreshLayout.setRefreshing(false);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(LihatMahasiswaBimbinganActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

                        // tutup refresh
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                // the POST parameters:
                params.put("id_user", idSaya);
                params.put("status", status);

                return params;
            }
        };

        MySingleton.getInstance(LihatMahasiswaBimbinganActivity.this).addToRequestQueue(postRequest);
    }



    // ================================== KONFIRMASI JADWAL BIMBINGAN (Dosen & Mahasiswa) ======================================

    private void KonfirmasiAlertDialog(final int position) {


//        String indikatorDosen = itemDaftarBimbingan.get(position).getKonfirm_dosen();
//        String indikatorMhs = itemDaftarBimbingan.get(position).getKonfirm_mhs();


//        if (indikatorMhs.equals("1") && indikatorDosen.equals("0")) {

            // JIKA INDIKATOR BERWARNA KUNING

            if (status.equals("dosen")) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Konfirmasi");
                builder.setMessage("Apakah anda ingin mengkonfirmasi jadwal bimbingan ?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        progressKonfirm = new ProgressDialog(LihatMahasiswaBimbinganActivity.this);
                        progressKonfirm.setTitle("Progress Konfirmasi");
                        progressKonfirm.setMessage("Tunggu Sebentar ...");
                        progressKonfirm.show();

                        PostKonfirmasiBimbingan(itemDaftarBimbingan.get(position).getId_agenda(), "1", "1");

                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
//        }


    }




// ===================================== KIRIM KONFIRMASI BIMBINGAN BAGI DOSEN ===================================

    public void PostKonfirmasiBimbingan(final String id_riwayat, final String indikator_mhs, final String indikator_dosen) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.KONFIRMASIBIMBINGAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("RESPONSE ", response);

                        try {


                            JSONObject obj = new JSONObject(response);
                            String konfirmasi = obj.getString("konfirmasi");

                            // hilangkan loading progress konfirmasi
                            progressKonfirm.dismiss();

                            // reload jadwal bimbingan
                            swipeRefreshLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefreshLayout.setRefreshing(true);
                                    RequestAgendaBimbingan(idSaya, status);
                                }
                            });


                            Toast.makeText(LihatMahasiswaBimbinganActivity.this, "Konfirmasi " + konfirmasi, Toast.LENGTH_SHORT).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LihatMahasiswaBimbinganActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_SHORT).show();
                        }

                        //lapor ke adapter jika respon selesai (ada perubahan)
                        adapter.notifyDataSetChanged();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                        progressKonfirm.dismiss();
                        Toast.makeText(LihatMahasiswaBimbinganActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                // the POST parameters:
                params.put("id_riwayat", id_riwayat);
                params.put("konfirm_mhs", indikator_mhs);
                params.put("konfirm_dosen", indikator_dosen);

                return params;
            }
        };

        MySingleton.getInstance(LihatMahasiswaBimbinganActivity.this).addToRequestQueue(postRequest);
    }




    // ================================== HAPUS RIWAYAT BIMBINGAN (Dosen) ======================================

    private void HapusRiwayat(final int position) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hapus Konfirmasi Bimbingan");
        builder.setMessage("Apakah anda ingin menghapus konfirmasi bimbingan ini ?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                progressHapus = new ProgressDialog(LihatMahasiswaBimbinganActivity.this);
                progressHapus.setTitle("Progress Hapus");
                progressHapus.setMessage("Tunggu Sebentar ...");
                progressHapus.show();

                // input id data akademik ke method PostValidasi
                PostHapusRiwayat(itemDaftarBimbingan.get(position).getId_agenda());

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();


    }


    // ===================================== PROSES HAPUS RIWAYAT BIMBINGAN ===================================

    public void PostHapusRiwayat(final String id_riwayatBimbingan) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.HAPUSRIWAYATBIMBINGAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("RESPONSE ", response);

                        try {

                            JSONObject obj = new JSONObject(response);
                            String konfirmasi = obj.getString("hapusRiwayatBimbingan");

                            // hilangkan loading progress konfirmasi
                            progressHapus.dismiss();

                            // reload jadwal bimbingan
                            swipeRefreshLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefreshLayout.setRefreshing(true);
                                    RequestAgendaBimbingan(idSaya, status);
                                }
                            });


                            Toast.makeText(LihatMahasiswaBimbinganActivity.this, "Konfirmasi Bimbingan di batalkan", Toast.LENGTH_SHORT).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LihatMahasiswaBimbinganActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_SHORT).show();
                        }

                        //lapor ke adapter jika respon selesai (ada perubahan)
                        adapter.notifyDataSetChanged();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                        progressHapus.dismiss();
                        Toast.makeText(LihatMahasiswaBimbinganActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                // the POST parameters:
                params.put("id_riwayatBimbingan", id_riwayatBimbingan);

                return params;
            }
        };

        MySingleton.getInstance(LihatMahasiswaBimbinganActivity.this).addToRequestQueue(postRequest);
    }


    @Override
    public void onRefresh() {
        RequestAgendaBimbingan(idSaya, status);
    }
}