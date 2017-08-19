package com.uii.academico.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.uii.academico.Adapter.AdapterRiwayatBimbingan;
import com.uii.academico.Model.RiwayatObject;
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

public class RiwayatBimbinganActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private AdapterRiwayatBimbingan adapter;
    private List<RiwayatObject> itemRiwayat = new ArrayList<>();

    TextView Nama, NIM;

    String status;
    String namaBimbingan, idBimbingan, urlfotoMhs, idDosen;
    NetworkImageView fotoMhs;

    SwipeRefreshLayout swipeRefreshLayout;
    ProgressDialog progressSubmit, progressKonfirm, progressHapus;

    LinearLayout belumAdaData;

    Button bantuan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_bimbingan);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_riwayat);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                RequestRiwayatBimbingan(idBimbingan);
            }
        });

        status = Utils.sharedPreferences.getString("status", "");

        Nama = (TextView) findViewById(R.id.TV_namaRiwayat);
        NIM = (TextView) findViewById(R.id.TV_nimRiwayat);
        fotoMhs = (NetworkImageView) findViewById(R.id.fotoProfilMhs);

        belumAdaData = (LinearLayout) findViewById(R.id.belumAdaData_jadwalBimbingan);
        belumAdaData.setVisibility(View.GONE);

        // Filter status untuk menampilkan data

        if (status.equals("dosen")) {

            // Tangkap nama dan id dari Extra Intent sebelumnya
            namaBimbingan = getIntent().getExtras().getString("nama");
            idBimbingan = getIntent().getExtras().getString("id");
            urlfotoMhs = getIntent().getExtras().getString("foto");

        } else if (status.equals("mahasiswa")) {
            // Tangkap nama dan id dari SharedPreferences
            idBimbingan = Utils.sharedPreferences.getString("id_user", "");
            namaBimbingan = Utils.sharedPreferences.getString("nama", "");
            urlfotoMhs = Utils.sharedPreferences.getString("foto", "");
            idDosen = Utils.sharedPreferences.getString("id_parent", "");
        }

        Nama.setText(namaBimbingan);
        NIM.setText(idBimbingan);

        ImageLoader imageLoader = MySingleton.getInstance(this).getImageLoader();

        // Cargo image loader (progress bar loading muncul ketika image blm terambil)

        if (imageLoader != null && urlfotoMhs != null) {
            imageLoader.get(urlfotoMhs, new ImageLoader.ImageListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        //fotoProfil.setVisibility(View.VISIBLE);
                        fotoMhs.setImageBitmap(response.getBitmap());
                        //progressBar.setVisibility(View.GONE);
                    }

                }
            });

            // SET Foto Profil Mahasiswa di RiwayatActivity
            fotoMhs.setImageUrl(urlfotoMhs, imageLoader);
        }

        bantuan = (Button) findViewById(R.id.bantuan);
        bantuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PetunjukKonfirmasi();
            }
        });


        // Panggil listview riwayat bimbingan
        ListView listRiwayat = (ListView) findViewById(R.id.list_riwayat_bimbingan);

        // adapter riwayat (konteks, data)
        adapter = new AdapterRiwayatBimbingan(RiwayatBimbinganActivity.this, itemRiwayat);
        listRiwayat.setAdapter(adapter);

        //hapus border listview
        listRiwayat.setDivider(null);

        // jika list item di klik
        listRiwayat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Setiap kali list di klik panggil method berikut :
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
                }else if (status.equals("mahasiswa") && itemRiwayat.get(position).getKonfirm_mhs().equals("1")
                        && itemRiwayat.get(position).getKonfirm_dosen().equals("0")){

                    EditTopikBimbingan(position);    // jika mahasiswa, dapat ubat topik sebelum jadwal di konfirmasi dosen
                    return true;
                }

                return false;
            }
        });

    }

    public void RequestRiwayatBimbingan(final String id_mhs) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.RIWAYATBIMBINGAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("RESPONSE ", response);

                        if (response.equals("[]")){
//                            Toast.makeText(DataAkademikActivity.this, "Belum Ada Data", Toast.LENGTH_SHORT).show();
                            belumAdaData.setVisibility(View.VISIBLE);
                        }

                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            // Agar data tidak terduplikasi
                            itemRiwayat.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = jsonArray.getJSONObject(i);

                                // Membuat objek setiap kontak
                                RiwayatObject riwayat = new RiwayatObject();

                                riwayat.setWaktu(obj.getString("waktu"));
                                riwayat.setTanggal(obj.getString("tanggal"));
                                riwayat.setLokasi(obj.getString("lokasi"));
                                riwayat.setKonfirm_mhs(obj.getString("konfirm_mhs"));
                                riwayat.setKonfirm_dosen(obj.getString("konfirm_dosen"));
                                riwayat.setId_riwayat(obj.getString("id_riwayat"));
                                riwayat.setTopiknya(obj.getString("topiknya"));
                                riwayat.setHasil_bimbingan(obj.getString("hasil_bimbingan"));

                                //menambahkan dokumen kedalam array dokumen
                                itemRiwayat.add(riwayat);

                                belumAdaData.setVisibility(View.GONE);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RiwayatBimbinganActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(RiwayatBimbinganActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

                        // tutup refresh
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                // the POST parameters:
                params.put("id_mhs", id_mhs);

                return params;
            }
        };

        MySingleton.getInstance(RiwayatBimbinganActivity.this).addToRequestQueue(postRequest);
    }


// ====================================== SET TOPIK BIMBINGAN BAGI MAHASISWA ===================================

    public void SetTopikBimbingan(final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Ajukan Topik Bimbingan");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint("ketik di sini ...");
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String isiTopik = input.getText().toString();

                        if (isiTopik.isEmpty()) {
                            Toast.makeText(RiwayatBimbinganActivity.this, "Isi Topik Bimbingan Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                        } else {
                            progressSubmit = new ProgressDialog(RiwayatBimbinganActivity.this);
                            progressSubmit.setTitle("Submit Topik");
                            progressSubmit.setMessage("Tunggu Sebentar ...");
                            progressSubmit.show();

                            SubmitTopikBimbingan(idDosen, itemRiwayat.get(position).getId_riwayat(), "1", "0", isiTopik);
                        }
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


    // ====================================== SET HASIL BIMBINGAN BAGI DOSEN ===================================

    public void SetHasilBimbingan(final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Keterangan Hasil Bimbingan");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint("ketik keterangan di sini ...");
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String isiKeterangan = input.getText().toString();

                        if (isiKeterangan.isEmpty()) {
                            Toast.makeText(RiwayatBimbinganActivity.this, "Isi Hasil Bimbingan Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                        } else {
                            progressSubmit = new ProgressDialog(RiwayatBimbinganActivity.this);
                            progressSubmit.setTitle("Submit Hasil Bimbingan");
                            progressSubmit.setMessage("Tunggu Sebentar ...");
                            progressSubmit.show();

                            PostKonfirmasiBimbingan(idDosen, itemRiwayat.get(position).getId_riwayat(), "1", "1", isiKeterangan);
                        }
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


    // ================================== KONFIRMASI JADWAL BIMBINGAN (Dosen & Mahasiswa) ======================================

    private void KonfirmasiAlertDialog(final int position) {


        String indikatorDosen = itemRiwayat.get(position).getKonfirm_dosen();
        String indikatorMhs = itemRiwayat.get(position).getKonfirm_mhs();


        if (indikatorMhs.equals("0") && indikatorDosen.equals("0")) {

            // JIKA INDIKATOR BERWARNA BIRU

            if (status.equals("mahasiswa")) {

                SetTopikBimbingan(position);

            } else if (status.equals("dosen")) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
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


        } else if (indikatorMhs.equals("1") && indikatorDosen.equals("0")) {

            // JIKA INDIKATOR BERWARNA KUNING

            if (status.equals("dosen")) {

                SetHasilBimbingan(position);

//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("Konfirmasi");
//                builder.setMessage("Apakah anda ingin mengkonfirmasi jadwal bimbingan ?");
//                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//
//                        progressKonfirm = new ProgressDialog(RiwayatBimbinganActivity.this);
//                        progressKonfirm.setTitle("Progress Konfirmasi");
//                        progressKonfirm.setMessage("Tunggu Sebentar ...");
//                        progressKonfirm.show();
//
//                        PostKonfirmasiBimbingan(itemRiwayat.get(position).getId_riwayat(), "1", "1", hasil_bimbingan);
//
//                        dialog.dismiss();
//                    }
//                });
//                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        //TODO
//                        dialog.dismiss();
//                    }
//                });
//                AlertDialog dialog = builder.create();
//                dialog.show();

            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Info");
                builder.setMessage("Topik bimbingan dan konfirmasi telah diajukan ke dosen");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }

        } else {

            // JIKA INDIKATOR BERWARNA HIJAU

//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Info");
//            builder.setMessage("Bimbingan tatap muka sudah dilakukan");
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    //TODO
//                    dialog.dismiss();
//                }
//            });
//            AlertDialog dialog = builder.create();
//            dialog.show();

            Intent keHasilBimbingan = new Intent(RiwayatBimbinganActivity.this, HasilBimbinganActivity.class);

            // bawa id dan nama ke class chat
            keHasilBimbingan.putExtra("tanggal", itemRiwayat.get(position).getTanggal());
            keHasilBimbingan.putExtra("waktu", itemRiwayat.get(position).getWaktu());
            keHasilBimbingan.putExtra("lokasi", itemRiwayat.get(position).getLokasi());
            keHasilBimbingan.putExtra("topik", itemRiwayat.get(position).getTopiknya());
            keHasilBimbingan.putExtra("hasil_bimbingan", itemRiwayat.get(position).getHasil_bimbingan());
            keHasilBimbingan.putExtra("id_riwayat", itemRiwayat.get(position).getId_riwayat());
            keHasilBimbingan.putExtra("status_user", status);

            startActivity(keHasilBimbingan);
        }


    }


    private void PetunjukKonfirmasi() {

        // custom dialog
        final Dialog dialog = new Dialog(RiwayatBimbinganActivity.this);
        dialog.setContentView(R.layout.petunjuk_konfirmasi);
        dialog.setTitle("Petunjuk Konfirmasi");

        dialog.show();
    }


// ===================================== KIRIM KONFIRMASI BIMBINGAN BAGI DOSEN ===================================

    public void PostKonfirmasiBimbingan(String idDosen, final String id_riwayat, final String indikator_mhs, final String indikator_dosen, final String hasil_bimbingan) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.KONFIRMASIHASILBIMBINGAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("RESPONSE ", response);

                        try {


                            JSONObject obj = new JSONObject(response);
                            String konfirmasi = obj.getString("konfirmasi");

                            // hilangkan loading progress konfirmasi
                            progressSubmit.dismiss();

                            // reload jadwal bimbingan
                            swipeRefreshLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefreshLayout.setRefreshing(true);
                                    RequestRiwayatBimbingan(idBimbingan);
                                }
                            });


                            Toast.makeText(RiwayatBimbinganActivity.this, "Konfirmasi " + konfirmasi, Toast.LENGTH_LONG).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RiwayatBimbinganActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_LONG).show();
                            // hilangkan loading progress konfirmasi
                            progressSubmit.dismiss();
                        }

                        //lapor ke adapter jika respon selesai (ada perubahan)
                        adapter.notifyDataSetChanged();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                        progressSubmit.dismiss();
                        Toast.makeText(RiwayatBimbinganActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

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
                params.put("hasil_bimbingan", hasil_bimbingan);

                return params;
            }
        };

        MySingleton.getInstance(RiwayatBimbinganActivity.this).addToRequestQueue(postRequest);
    }


    // ===================================== SUBMIT TOPIK BIMBINGAN BAGI MAHASISWA ===================================

    public void SubmitTopikBimbingan(final String idDosen, final String id_riwayat, final String indikator_mhs, final String indikator_dosen, final String topik_bimbingan) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.KONFIRMASITOPIKBIMBINGAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {


                            JSONObject obj = new JSONObject(response);
                            String konfirmasi = obj.getString("konfirmasi");

                            // hilangkan loading submit
                            progressSubmit.dismiss();

                            // reload jadwal bimbingan
                            swipeRefreshLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefreshLayout.setRefreshing(true);
                                    RequestRiwayatBimbingan(idBimbingan);
                                }
                            });

                            Toast.makeText(RiwayatBimbinganActivity.this, "Submit " + konfirmasi, Toast.LENGTH_LONG).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RiwayatBimbinganActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_LONG).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                        // hilangkan loading submit
                        progressSubmit.dismiss();
                        Toast.makeText(RiwayatBimbinganActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                // the POST parameters:
                params.put("id_dosen", idDosen);
                params.put("id_riwayat", id_riwayat);
                params.put("konfirm_mhs", indikator_mhs);
                params.put("konfirm_dosen", indikator_dosen);
                params.put("topiknya", topik_bimbingan);

                return params;
            }
        };

        MySingleton.getInstance(RiwayatBimbinganActivity.this).addToRequestQueue(postRequest);
    }


    // ================================== HAPUS RIWAYAT BIMBINGAN (Dosen) ======================================

    private void HapusRiwayat(final int position) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hapus Riwayat Bimbingan");
        builder.setMessage("Apakah anda ingin menghapus riwayat bimbingan ini ?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                progressHapus = new ProgressDialog(RiwayatBimbinganActivity.this);
                progressHapus.setTitle("Progress Hapus");
                progressHapus.setMessage("Tunggu Sebentar ...");
                progressHapus.show();

                // input id data akademik ke method PostValidasi
                PostHapusRiwayat(itemRiwayat.get(position).getId_riwayat());

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
                                    RequestRiwayatBimbingan(idBimbingan);
                                }
                            });


                            Toast.makeText(RiwayatBimbinganActivity.this, "Riwayat Bimbingan  " + konfirmasi + " dihapus", Toast.LENGTH_LONG).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RiwayatBimbinganActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(RiwayatBimbinganActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

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

        MySingleton.getInstance(RiwayatBimbinganActivity.this).addToRequestQueue(postRequest);
    }

    // ====================================== EDIT TOPIK BIMBINGAN BAGI MAHASISWA ===================================

    public void EditTopikBimbingan(final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Ubah Topik Bimbingan");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint("ketik di sini ...");
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String isiTopik = input.getText().toString();

                        if (isiTopik.isEmpty()) {
                            Toast.makeText(RiwayatBimbinganActivity.this, "Isi Topik Bimbingan Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                        } else {
                            progressSubmit = new ProgressDialog(RiwayatBimbinganActivity.this);
                            progressSubmit.setTitle("Ubah Topik Bimbingan");
                            progressSubmit.setMessage("Tunggu Sebentar ...");
                            progressSubmit.show();

                            SubmitTopikBimbingan(idDosen, itemRiwayat.get(position).getId_riwayat(), "1", "0", isiTopik);
                        }
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

    @Override
    public void onRefresh() {
        RequestRiwayatBimbingan(idBimbingan);
    }

    @Override
    protected void onResume() {
        super.onResume();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                RequestRiwayatBimbingan(idBimbingan);
            }
        });
    }
}