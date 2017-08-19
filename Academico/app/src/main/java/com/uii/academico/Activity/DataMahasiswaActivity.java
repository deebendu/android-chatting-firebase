package com.uii.academico.Activity;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.uii.academico.Adapter.AdapterBimbingan;
import com.uii.academico.Model.BimbinganObject;
import com.uii.academico.R;
import com.uii.academico.Utility.BlurBuilder;
import com.uii.academico.Utility.Utils;
import com.uii.academico.Volley.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataMahasiswaActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    AdapterBimbingan adapter;
    SwipeRefreshLayout swipeRefreshLayout;

    SearchView searchView;

    String idSaya, status, idJurusan;

    private List<BimbinganObject> itemBimbingan = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_mahasiswa);

        ImageView bgDataMhs = (ImageView) findViewById(R.id.backgroundDataMhs);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ulil2);
        Bitmap blurredBitmap = BlurBuilder.blur(this, bitmap);
        bgDataMhs.setImageBitmap(blurredBitmap);

        Utils.init(this);

        idSaya = Utils.sharedPreferences.getString("id_user", "");
        status = Utils.sharedPreferences.getString("status", "");
        idJurusan = Utils.sharedPreferences.getString("id_jurusan", "");

        Log.e("STATUS ", status);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_data_mahasiswa);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                RequestDataBimbingan(idSaya, status, idJurusan);
            }
        });


        //panggil listview
        ListView listBimbingan = (ListView) findViewById(R.id.list_mahasiswa_bimbingan);

        //hapus border listview
        listBimbingan.setDivider(null);

        //inisialisasi adapter dan set adapter
        adapter = new AdapterBimbingan(this, itemBimbingan, "Data Akademik");
        listBimbingan.setAdapter(adapter);

//        //fungsi klik masing2 item
//        listBimbingan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                //lakukan sesuatu
//                Toast.makeText(DataMahasiswaActivity.this, "USER : " + itemBimbingan.get(position).getNama(), Toast.LENGTH_SHORT).show();
//                Intent keDataAkademik = new Intent(DataMahasiswaActivity.this, DataAkademikActivity.class);
//
//
//
//                // bawa id dan nama ke class chat
//                keDataAkademik.putExtra("id", itemBimbingan.get(position).getId());
//                keDataAkademik.putExtra("nama", itemBimbingan.get(position).getNama());
//                keDataAkademik.putExtra("foto", itemBimbingan.get(position).getUrlFoto());
//
//                startActivity(keDataAkademik);
//            }
//        });

    }


    public void RequestDataBimbingan(final String idSaya, final String status, final String idJurusan) {


        StringRequest postRequest = new StringRequest(Request.Method.POST, Utils.BIMBINGAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("RESPONSE ", response);

//                        swipeRefreshLayout.setRefreshing(true);

                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            itemBimbingan.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = jsonArray.getJSONObject(i);

                                // Membuat objek setiap kontak
                                BimbinganObject bimbingan = new BimbinganObject();

                                bimbingan.setNama(obj.getString("nama"));
                                bimbingan.setId(obj.getString("noinduk"));
                                bimbingan.setUrlFoto(obj.getString("foto"));

                                //menambahkan list kontak
                                itemBimbingan.add(bimbingan);


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
//                            Toast.makeText(DataMahasiswaActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(DataMahasiswaActivity.this, "Terjadi Kesalahan Input", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(DataMahasiswaActivity.this, "Tidak Terhubung \n Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();

                        // tutup refresh
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                // the POST parameters:
                params.put("id", idSaya);
                params.put("status", status);
                params.put("id_jurusan", idJurusan);

                Log.e("LOG POST ", " id " + idJurusan + "status " + status + "jurusan " + idJurusan);


                return params;
            }
        };

        MySingleton.getInstance(DataMahasiswaActivity.this).addToRequestQueue(postRequest);
    }

    @Override
    public void onRefresh() {
        RequestDataBimbingan(idSaya, status, idJurusan);
    }


    // ==========  Cari Mahasiswa  ===========
    private void setupSearchView() {

        searchView.setIconifiedByDefault(true);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();

            // Try to use the "applications" global search provider
            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
//            for (SearchableInfo inf : searchables) {
//                if (inf.getSuggestAuthority() != null
//                        && inf.getSuggestAuthority().startsWith("applications")) {
//                    info = inf;
//                }
//            }
            searchView.setSearchableInfo(info);
        }

        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.getFilter().filter(query.toString());
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText.toString());
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        setupSearchView();

        return true;

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
