package com.uii.academico.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.uii.academico.Model.DaftarBimbinganObject;
import com.uii.academico.R;
import com.uii.academico.Volley.MySingleton;

import java.util.List;

/**
 * Created by Fakhrus on 11/12/16.
 */
public class AdapterRiwayatTatapMuka extends BaseAdapter {

    Context activity;
    List<DaftarBimbinganObject> itemDaftarBimbingan;

    ImageLoader imageLoader;

    public AdapterRiwayatTatapMuka(Context activity, List<DaftarBimbinganObject> itemDaftarBimbingan) {
        this.activity = activity;
        this.itemDaftarBimbingan = itemDaftarBimbingan;
    }

    @Override
    public int getCount() { return itemDaftarBimbingan.size(); }

    @Override
    public Object getItem(int position) {
        return itemDaftarBimbingan.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //inflate
        View v = View.inflate(activity, R.layout.item_hasil_bimbingan, null);

        imageLoader = MySingleton.getInstance(activity).getImageLoader();


        //panggil ID
        TextView waktu = (TextView) v.findViewById(R.id.waktu_bimbingan);
        TextView tanggal = (TextView) v.findViewById(R.id.tanggal_bimbingan);
        TextView lokasi = (TextView) v.findViewById(R.id.lokasi_bimbingan);
        ImageView warna = (ImageView) v.findViewById(R.id.indikatorWarna);
        ImageView icon = (ImageView) v.findViewById(R.id.iconIndikator);
        TextView topiknya = (TextView) v.findViewById(R.id.TV_topik_bimbingan);

        TextView nama = (TextView) v.findViewById(R.id.nama_mhs_agenda);
        TextView nim = (TextView) v.findViewById(R.id.nim_mhs_agenda);
        final NetworkImageView foto = (NetworkImageView) v.findViewById(R.id.profilAgendaBimbingan);

        final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.PB_loadProfilAgenda);

        //ambil data tiap baris
        final DaftarBimbinganObject daftarBimbinganObject = itemDaftarBimbingan.get(position);

        // pasang teksnya
        waktu.setText(daftarBimbinganObject.getWaktu());
        tanggal.setText(daftarBimbinganObject.getTanggal());
        lokasi.setText(daftarBimbinganObject.getLokasi());
        topiknya.setText(daftarBimbinganObject.getTopiknya());
        nama.setText(daftarBimbinganObject.getNama_mhs());
        nim.setText(daftarBimbinganObject.getId_mhs());

        // Cargo image loader (progress bar loading muncul ketika image blm terambil)
        String imageUrl = daftarBimbinganObject.getProfil_mhs();

        if(imageLoader !=null && imageUrl!=null) {
            imageLoader.get(imageUrl, new ImageLoader.ImageListener() {


                @Override
                public void onErrorResponse(VolleyError error) {

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        foto.setVisibility(View.VISIBLE);
                        foto.setImageBitmap(response.getBitmap());
                        progressBar.setVisibility(View.GONE);
//                        System.out.println("Done loading " + imageUrl);
                    }

                }
            });


            foto.setImageUrl(imageUrl, imageLoader);
        }

//        String indikatorDosen = agenda.getKonfirm_dosen();
//        String indikatorMhs = agenda.getKonfirm_mhs();

//        if (indikatorMhs.equals("1") && indikatorDosen.equals("0")){
        warna.setBackgroundColor(Color.parseColor("#2ecc71"));
        icon.setImageResource(R.drawable.confirmed);

//        }


        //tampilkan view nya
        return v;
    }

}
