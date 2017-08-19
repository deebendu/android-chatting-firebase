package com.uii.academico.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.uii.academico.Model.InformasiObject;
import com.uii.academico.R;
import com.uii.academico.Volley.MySingleton;

import java.util.List;

/**
 * Created by Fakhrus on 4/10/16.
 */
public class AdapterInformasi extends BaseAdapter {

    Context activity;
    List<InformasiObject> itemInformasi;

    ImageLoader imageLoader;

    public AdapterInformasi(Context activity, List<InformasiObject> itemInformasi) {
        this.activity = activity;
        this.itemInformasi = itemInformasi;
    }

    @Override
    public int getCount() { return itemInformasi.size(); }

    @Override
    public Object getItem(int position) {
        return itemInformasi.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //inflate
        View v = View.inflate(activity, R.layout.item_informasi, null);

        imageLoader = MySingleton.getInstance(activity).getImageLoader();

        //panggil ID
        TextView topik = (TextView) v.findViewById(R.id.topik);
        TextView tanggal = (TextView) v.findViewById(R.id.tanggal_informasi);
        TextView deskripsi = (TextView) v.findViewById(R.id.deskripsi);
        final NetworkImageView gambarInformasi = (NetworkImageView) v.findViewById(R.id.gambar_informasi);
        final ProgressBar progressImageLoad = (ProgressBar) v.findViewById(R.id.PB_progress_imageLoad);

        gambarInformasi.setVisibility(View.GONE);
        progressImageLoad.setVisibility(View.VISIBLE);

        //ambil data tiap baris
        InformasiObject informasi = itemInformasi.get(position);

        // pasang teksnya
        topik.setText(informasi.getTopik());
        tanggal.setText(informasi.getTanggal());
        deskripsi.setText(informasi.getDeskripsi());

        // Cargo image loader (progress bar loading muncul ketika image blm terambil)
        String imageUrl = informasi.getGambarInformasi();

        if(imageLoader !=null && imageUrl!=null) {
            imageLoader.get(imageUrl, new ImageLoader.ImageListener() {


                @Override
                public void onErrorResponse(VolleyError error) {

                    progressImageLoad.setVisibility(View.GONE);

                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        gambarInformasi.setVisibility(View.VISIBLE);
                        gambarInformasi.setImageBitmap(response.getBitmap());
                        progressImageLoad.setVisibility(View.GONE);
//                        System.out.println("Done loading " + imageUrl);
                    }

                }
            });


            gambarInformasi.setImageUrl(imageUrl, imageLoader);
        }

        //tampilkan view nya
        return v;
    }
}
