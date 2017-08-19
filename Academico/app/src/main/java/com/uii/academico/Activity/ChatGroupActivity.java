package com.uii.academico.Activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.uii.academico.Adapter.AdapterChatGroup;
import com.uii.academico.Adapter.AdapterMember;
import com.uii.academico.Model.ChatGroupObject;
import com.uii.academico.Model.ChatObject;
import com.uii.academico.Model.MemberObject;
import com.uii.academico.R;
import com.uii.academico.Utility.Utils;
import com.uii.academico.Volley.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatGroupActivity extends AppCompatActivity {

    EditText kolomPesan;
    Button kirim;
    private String pesan, idSaya, namaSaya, idObrolan, namaObrolan, statusSaya, fotoSaya;

    BroadcastReceiver terimaBroadcast;
    List<ChatGroupObject> itemChatGroup = new ArrayList<>();
    List<MemberObject> itemMember = new ArrayList<>();

    ListView daftarChatBubble;

    LinearLayout ll_progress_pesanGroup;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ll_progress_pesanGroup = (LinearLayout) findViewById(R.id.LL_untuk_progres_pesanGroup);
        progressBar = (ProgressBar) findViewById(R.id.PB_lihat_pesanGroup);
        progressBar.setVisibility(View.VISIBLE);


        // Ambil dari penyimpanan SharedPreferences (dikirim ke penerima sbg identitas pengirim)
        namaSaya = Utils.sharedPreferences.getString("nama", "");
        idSaya = Utils.sharedPreferences.getString("id_user", "");
        statusSaya = Utils.sharedPreferences.getString("status", "");
        fotoSaya = Utils.sharedPreferences.getString("foto", "");

        // Tangkap string dari Intent (utk mengetahui penerimanya)
        idObrolan = getIntent().getExtras().getString("idObrolan");
        namaObrolan = getIntent().getExtras().getString("kirim ke");
//        statusPenerima = getIntent().getExtras().getString("statusObrolan", "");

        Log.i("MANA ERRORNYA ? ", "" + idObrolan + " " + namaObrolan + " pengirim " + statusSaya);
        Log.e("MEMBER ", "" + itemMember);

        // Panggil Method
        LihatPesan(idSaya, idObrolan);
        LihatMember (idObrolan);



        // Tampilkan sebagai title chat group

        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.titlebar_custom_groupchat, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                // dengan parameter seperti berikut :
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);

        abar.setCustomView(viewActionBar, params);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        TextView namaTitle = (TextView) findViewById(R.id.namaTitlebarChatGroup);

        namaTitle.setText(namaObrolan);

        ImageView lihatMember = (ImageView) findViewById(R.id.IV_lihatAnggota);
        lihatMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LihatAnggotaGroup();
            }
        });



        daftarChatBubble = (ListView) findViewById(R.id.list_pesanGroup);

        //hapus border listview
        daftarChatBubble.setDivider(null);


        kolomPesan = (EditText) findViewById(R.id.ET_pesanGroup);

        kirim = (Button) findViewById(R.id.BT_kirimChatGroup);
        kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//              Menampilkan waktu pesan sesuai dengan waktu android dalam pesan saya
                Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String waktuAndroid = df.format(c.getTime());

                //ambil teks
                pesan = kolomPesan.getText().toString();

                // pesan tidak kosong
                if (!pesan.matches("")) {
                    KirimPesan(idSaya, idObrolan, namaSaya, pesan);

                    TampilkanPesan(idSaya, pesan, "Saya", fotoSaya, waktuAndroid);

                    // kosongkan kolom ketik pesan setelah klik kirim
                    kolomPesan.setText(null);
                } else {
                    Toast.makeText(ChatGroupActivity.this, "Ketik Pesan Dulu", Toast.LENGTH_SHORT).show();
                }
            }
        });



        // broadcast dari class Push Receiver Service
        terimaBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // ITEM yang Masuk di CHAT BUBBLE

                String idPengirim = intent.getStringExtra("id_pengirim");
                String pesan = intent.getStringExtra("pesannya");
                String namaPengirim = intent.getStringExtra("namaPengirim");
                String fotoMemberGroup = intent.getStringExtra("fotoMember");
                String waktuPesan = intent.getStringExtra("waktuPesan");

                TampilkanPesan(idPengirim, pesan, namaPengirim, fotoMemberGroup, waktuPesan);

//                Toast.makeText(ChatGroupActivity.this, "Pesan Diterima : " + pesan, Toast.LENGTH_SHORT).show();

            }
        };

        // terima broadcast dengan filter id obrolan
        LocalBroadcastManager.getInstance(this).registerReceiver(terimaBroadcast, new IntentFilter("group"));

    } // TUTUP ON CREATE




    // Tampil pesan dari Notifikasi
    // tipe menentukan posisi pesan dari saya atau pesan dari orang lain
    public void TampilkanPesan(String idPengirim, String pesan, String namaPengirim, String fotoMember, String waktuPesan) {

        itemChatGroup.add(new ChatGroupObject(idPengirim, pesan, namaPengirim, fotoMember, waktuPesan));

        AdapterChatGroup adapterChatGroup = new AdapterChatGroup(ChatGroupActivity.this, R.layout.chat_bubble_group, itemChatGroup, idObrolan);

        daftarChatBubble.setAdapter(adapterChatGroup);
        adapterChatGroup.notifyDataSetChanged();
    }




    public void KirimPesan(final String id_saya, final String id_tujuan, final String nama_saya, final String pesan) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.KIRIMPESANGROUP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(ChatGroupActivity.this, "Pesan Terkirim : " + pesan, Toast.LENGTH_SHORT).show();
                        Toast.makeText(ChatGroupActivity.this, "Pesan Terkirim", Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        Toast.makeText(ChatGroupActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("id_pengirim", id_saya);
                params.put("nama_pengirim", nama_saya);
                params.put("id_chatroom", id_tujuan); // id chatroom
                params.put("pesan", pesan);

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


    public void LihatPesan(final String id_saya, final String id_tujuan) {


        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.LIHATPESANGROUP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            itemChatGroup.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = jsonArray.getJSONObject(i);

                                String pengirim = obj.getString("id_pengirim");
                                String namaPengirim = obj.getString("nama_user");
                                String pesan = obj.getString("pesan");
                                String fotoProfil = obj.getString("profil_user");
                                String tanggal = obj.getString("tanggal");

                                // Membuat objek setiap chat
                                TampilkanPesan(pengirim, pesan, namaPengirim, fotoProfil, tanggal);


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ChatGroupActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_SHORT).show();
                        }

                        ll_progress_pesanGroup.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        Toast.makeText(ChatGroupActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
                        ll_progress_pesanGroup.setVisibility(View.GONE);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("id_pengirim", id_saya);
                params.put("id_chatroom", id_tujuan);

                return params;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(postRequest);
    }

    public void LihatMember(final String id_chatroom) {


        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.LIHATMEMBER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            itemMember.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = jsonArray.getJSONObject(i);

                                MemberObject member = new MemberObject();

                                member.setIdMember(obj.getString("id_user"));
                                member.setNamaMember(obj.getString("nama_user"));
                                member.setProfilMember(obj.getString("profil_user"));

                                // Membuat objek setiap chat
                                itemMember.add(member);


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ChatGroupActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_LONG).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        Toast.makeText(ChatGroupActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("id_chatroom", id_chatroom);

                return params;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(postRequest);
    }

    private void LihatAnggotaGroup() {

        // custom dialog
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChatGroupActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.view_member, null);
        alertDialog.setView(view);
        alertDialog.setTitle("Anggota Group");


        ListView list_member = (ListView) view.findViewById(R.id.list_member);
        AdapterMember adapterMember = new AdapterMember(ChatGroupActivity.this, itemMember);

        //hapus border listview
        list_member.setDivider(null);

        list_member.setAdapter(adapterMember);




        alertDialog.show();
    }

}
