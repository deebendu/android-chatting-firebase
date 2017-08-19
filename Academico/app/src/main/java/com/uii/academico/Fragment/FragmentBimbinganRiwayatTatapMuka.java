package com.uii.academico.Fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.uii.academico.Activity.HasilBimbinganActivity;
import com.uii.academico.Adapter.AdapterPilihanJadwal;
import com.uii.academico.Adapter.AdapterRiwayatTatapMuka;
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
public class FragmentBimbinganRiwayatTatapMuka extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private AdapterRiwayatTatapMuka adapter;
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

        View viewRiwayatTatapMuka = inflater.inflate(R.layout.fragment_bimbingan_riwayat_tatap_muka, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) viewRiwayatTatapMuka.findViewById(R.id.swipe_refresh_riwayatTatapMuka);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                RequestAgendaBimbingan(idSaya, status);
            }
        });




        // Panggil listview riwayat bimbingan
        ListView listRiwayatTatapMuka = (ListView) viewRiwayatTatapMuka.findViewById(R.id.daftar_riwayatTatapMuka);

        // adapter riwayat (konteks, data)
        adapter = new AdapterRiwayatTatapMuka(getContext(), itemDaftarBimbingan);
        listRiwayatTatapMuka.setAdapter(adapter);

        //hapus border listview
        listRiwayatTatapMuka.setDivider(null);

        // jika list item di klik
        listRiwayatTatapMuka.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent keHasilBimbingan = new Intent(getContext(), HasilBimbinganActivity.class);

                // bawa id dan nama ke class chat
                keHasilBimbingan.putExtra("tanggal", itemDaftarBimbingan.get(position).getTanggal());
                keHasilBimbingan.putExtra("waktu", itemDaftarBimbingan.get(position).getWaktu());
                keHasilBimbingan.putExtra("lokasi", itemDaftarBimbingan.get(position).getLokasi());
                keHasilBimbingan.putExtra("topik", itemDaftarBimbingan.get(position).getTopiknya());
                keHasilBimbingan.putExtra("hasil_bimbingan", itemDaftarBimbingan.get(position).getHasil_bimbingan());
                keHasilBimbingan.putExtra("id_riwayat", itemDaftarBimbingan.get(position).getId_agenda());
                keHasilBimbingan.putExtra("status_user", status);

                startActivity(keHasilBimbingan);
            }
        });

        // jika list ditekan lama
        listRiwayatTatapMuka.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (status.equals("dosen")){    // jika dosen, dapat hapus riwayat
                    HapusRiwayat(position);
                    return true;
                }

                return false;
            }
        });

        return viewRiwayatTatapMuka;
    }

    public void RequestAgendaBimbingan(final String idSaya, final String status) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.RIWAYATTATAPMUKA,
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
                                daftarBimbinganObject.setHasil_bimbingan(obj.getString("hasil_bimbingan"));

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



    // ================================== KONFIRMASI JADWAL BIMBINGAN (Dosen & Mahasiswa) ======================================

    private void KonfirmasiAlertDialog(final int position) {


//        String indikatorDosen = itemDaftarBimbingan.get(position).getKonfirm_dosen();
//        String indikatorMhs = itemDaftarBimbingan.get(position).getKonfirm_mhs();


//        if (indikatorMhs.equals("1") && indikatorDosen.equals("0")) {

        // JIKA INDIKATOR BERWARNA KUNING

        if (status.equals("dosen")) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Konfirmasi");
            builder.setMessage("Apakah anda ingin mengkonfirmasi jadwal bimbingan ?");
            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    progressKonfirm = new ProgressDialog(getContext());
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


                            Toast.makeText(getContext(), "Konfirmasi " + konfirmasi, Toast.LENGTH_SHORT).show();


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

                        progressKonfirm.dismiss();
                        Toast.makeText(getContext(), "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

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
