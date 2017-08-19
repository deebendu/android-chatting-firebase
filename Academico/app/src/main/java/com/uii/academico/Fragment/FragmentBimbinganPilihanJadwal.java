package com.uii.academico.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.uii.academico.Adapter.AdapterDaftarBimbingan;
import com.uii.academico.Adapter.AdapterPilihanJadwal;
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

/**
 * Created by Fakhrus on 11/12/16.
 */
public class FragmentBimbinganPilihanJadwal extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private AdapterPilihanJadwal adapter;
    private List<DaftarBimbinganObject> itemDaftarBimbingan = new ArrayList<>();

//    TextView Nama, NIM;

    String status, idSaya;
//    String namaBimbingan, idBimbingan, urlfotoMhs;
//    NetworkImageView fotoMhs;

    SwipeRefreshLayout swipeRefreshLayout;
    ProgressDialog progressKonfirm, progressHapus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        status = Utils.sharedPreferences.getString("status", "");
        idSaya = Utils.sharedPreferences.getString("id_user", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View viewPilihanJadwal = inflater.inflate(R.layout.fragment_bimbingan_pilihan_jadwal, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) viewPilihanJadwal.findViewById(R.id.swipe_refresh_pilihanTatapMuka);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                RequestAgendaBimbingan(idSaya, status);
            }
        });




        // Panggil listview riwayat bimbingan
        ListView listPilihan = (ListView) viewPilihanJadwal.findViewById(R.id.daftar_pilihanTatapMuka);

        // adapter riwayat (konteks, data)
        adapter = new AdapterPilihanJadwal(getContext(), itemDaftarBimbingan);
        listPilihan.setAdapter(adapter);

        //hapus border listview
        listPilihan.setDivider(null);

        // jika list item di klik
        listPilihan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (status.equals("dosen")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Info");
                    builder.setMessage("Menunggu topik bimbingan dan konfirmasi dari mahasiswa");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        // jika list ditekan lama
        listPilihan.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (status.equals("dosen")){    // jika dosen, dapat hapus riwayat
                    HapusRiwayat(position);
                    return true;
                }

                return false;
            }
        });

        return viewPilihanJadwal;
    }

    public void RequestAgendaBimbingan(final String idSaya, final String status) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.PILIHANJADWALTATAPMUKA,
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

                                daftarBimbinganObject.setNama_mhs(obj.getString("nama_mhs"));
                                daftarBimbinganObject.setId_mhs(obj.getString("nim_mhs"));
                                daftarBimbinganObject.setProfil_mhs(obj.getString("foto_mhs"));



                                //menambahkan dokumen kedalam array dokumen
                                itemDaftarBimbingan.add(daftarBimbinganObject);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Terjadi Kesalahan Input" + e, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

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

        MySingleton.getInstance(getContext()).addToRequestQueue(postRequest);
    }







    // ================================== HAPUS RIWAYAT BIMBINGAN (Dosen) ======================================

    private void HapusRiwayat(final int position) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Hapus Konfirmasi Bimbingan");
        builder.setMessage("Apakah anda ingin menghapus konfirmasi bimbingan ini ?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                progressHapus = new ProgressDialog(getContext());
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


                            Toast.makeText(getContext(), "Konfirmasi Bimbingan di batalkan", Toast.LENGTH_SHORT).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Terjadi Kesalahan Input", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

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

        MySingleton.getInstance(getContext()).addToRequestQueue(postRequest);
    }


    @Override
    public void onRefresh() {
        RequestAgendaBimbingan(idSaya, status);
    }
}
