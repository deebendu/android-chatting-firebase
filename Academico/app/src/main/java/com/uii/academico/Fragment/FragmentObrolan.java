package com.uii.academico.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.uii.academico.Activity.ChatActivity;
import com.uii.academico.Activity.ChatGroupActivity;
import com.uii.academico.Adapter.AdapterObrolan;
import com.uii.academico.Model.ObrolanObject;
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
 * Created by Fakhrus on 4/10/16.
 */
public class FragmentObrolan extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    AdapterObrolan adapter;
    SwipeRefreshLayout swipeRefreshLayout, swipeRefreshPetunjuk;

    String idSaya, idParent, status;

    private List<ObrolanObject> itemObrolan = new ArrayList<>();

    LinearLayout ll_petunjuk_obrolan;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.init(getContext());

        idSaya = Utils.sharedPreferences.getString("id_user", "");
        idParent = Utils.sharedPreferences.getString("id_parent", "");
        status = Utils.sharedPreferences.getString("status", "");

        Log.e("ID SAYA", idSaya);
        Log.e("ID PARENT", idParent);
        Log.e("status", status);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View viewObrolan = inflater.inflate(R.layout.fragment_obrolan, container, false);


        // Petunjuk obrolan

        swipeRefreshPetunjuk = (SwipeRefreshLayout) viewObrolan.findViewById(R.id.swipe_refresh_petunjuk_obrolan);
        swipeRefreshPetunjuk.setOnRefreshListener(this);

        swipeRefreshPetunjuk.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                RequestDataObrolan(idSaya, idParent, status);
            }
        });

        ll_petunjuk_obrolan = (LinearLayout) viewObrolan.findViewById(R.id.petunjuk_obrolan);


        // Refresh obrolan

        swipeRefreshLayout = (SwipeRefreshLayout) viewObrolan.findViewById(R.id.swipe_refresh_obrolan);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                RequestDataObrolan(idSaya, idParent, status);
            }
        });

        //panggil listview
        ListView listObrolan = (ListView) viewObrolan.findViewById(R.id.list_obrolan);

        //hapus border listview
        listObrolan.setDivider(null);

        //inisialisasi adapter dan set adapter
        adapter = new AdapterObrolan(getContext(), itemObrolan);
        listObrolan.setAdapter(adapter);

        //fungsi klik masing2 item
        listObrolan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //lakukan sesuatu
                if (itemObrolan.get(position).getTipeObrolan().equals("group")){

                    Toast.makeText(getContext(), "Room : " + itemObrolan.get(position).getNamaObrolan(), Toast.LENGTH_SHORT).show();
                    Intent keChatGroup = new Intent(getActivity(), ChatGroupActivity.class);

                    // bawa id dan nama ke class chat
                    keChatGroup.putExtra("idObrolan", itemObrolan.get(position).getIdObrolan());
                    keChatGroup.putExtra("statusObrolan", itemObrolan.get(position).getTipeObrolan());
                    keChatGroup.putExtra("kirim ke", itemObrolan.get(position).getNamaObrolan());
                    startActivity(keChatGroup);

                }else {
                    Toast.makeText(getContext(), "Room : " + itemObrolan.get(position).getNamaObrolan(), Toast.LENGTH_SHORT).show();
                    Intent keChat = new Intent(getActivity(), ChatActivity.class);

                    // bawa id dan nama ke class chat
                    keChat.putExtra("idKontak", itemObrolan.get(position).getIdObrolan());
                    keChat.putExtra("statusKontak", itemObrolan.get(position).getStatusPengirim()); // status pengirim obrolan = status kontak pada kontak
                    keChat.putExtra("kirim ke", itemObrolan.get(position).getNamaObrolan());
                    keChat.putExtra("fotoProfil", itemObrolan.get(position).getUrlFotoObrolan());
                    startActivity(keChat);

                }
            }
        });


        return viewObrolan;
    }


    public void RequestDataObrolan(final String idSaya, final String idParent, final String Status) {


        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.OBROLAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("RESPONSE ", response);


                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            itemObrolan.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = jsonArray.getJSONObject(i);

                                // Membuat objek setiap obrolan
                                ObrolanObject obrolan = new ObrolanObject();

                                obrolan.setIdObrolan(obj.getString("id_chatroom"));
                                obrolan.setNamaObrolan(obj.getString("nama_chatroom"));
                                obrolan.setPesanTerakhir(obj.getString("pesan_terakhir"));
                                obrolan.setTipeObrolan(obj.getString("tipe_chatroom"));
                                obrolan.setStatusPengirim(obj.getString("statusPengirim"));
                                obrolan.setUrlFotoObrolan(obj.getString("foto"));

                                //menambahkan list obrolan
                                itemObrolan.add(obrolan);


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Terjadi Kesalahan Input: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        //lapor ke adapter jika respon selesai (ada perubahan)
                        adapter.notifyDataSetChanged();

                        // tutup refresh
                        swipeRefreshLayout.setRefreshing(false);

                        swipeRefreshPetunjuk.setRefreshing(false);
                        swipeRefreshPetunjuk.setVisibility(View.GONE);
                        ll_petunjuk_obrolan.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getContext(), "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

                        // tutup refresh
                        swipeRefreshLayout.setRefreshing(false);
                        swipeRefreshPetunjuk.setRefreshing(false);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                // the POST parameters:
                params.put("id_user", idSaya);
                params.put("id_parent", idParent);
                params.put("status", Status);

                Log.e("ID SAYA", idSaya);
                Log.e("ID PARENT", idParent);
                Log.e("status", Status);

                return params;
            }
        };

        MySingleton.getInstance(getContext()).addToRequestQueue(postRequest);

    }

    @Override
    public void onRefresh() {
        RequestDataObrolan(idSaya, idParent, status);
    }
}
