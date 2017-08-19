package com.uii.academico.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.uii.academico.Activity.ChatActivity;
import com.uii.academico.Model.KontakObject;
import com.uii.academico.R;
import com.uii.academico.Volley.MySingleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fakhrus on 4/14/16.
 */

public class AdapterKontak extends BaseAdapter {

    Context activity;
    List<KontakObject> itemKontak;
    List<KontakObject> searchKontak;


    public AdapterKontak( Context activity, List<KontakObject> itemKontak) {
        this.activity = activity;
        this.itemKontak = itemKontak;
    }

    @Override
    public int getCount() { return itemKontak.size(); }

    @Override
    public Object getItem(int position) {
        return itemKontak.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //inflate
        View v = View.inflate(activity, R.layout.item_kontak, null);

        ImageLoader imageLoader = MySingleton.getInstance(activity).getImageLoader();

        //panggil ID
        TextView nama = (TextView) v.findViewById(R.id.nama);
        TextView id_user = (TextView) v.findViewById(R.id.nim);

        final NetworkImageView fotoKontak = (NetworkImageView) v.findViewById(R.id.IV_fotoKontak);
        final ProgressBar progressKontak = (ProgressBar) v.findViewById(R.id.PB_progresKontak);

//        =========== event klik agar setiap item list dapat di klik tidak melalui listview OnItemClickListener (problem posisi data search )========

        LinearLayout layoutItemKontak = (LinearLayout) v.findViewById(R.id.layout_item_kontak);
        layoutItemKontak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lakukan sesuatu
                Toast.makeText(activity, "USER : " + itemKontak.get(position).getNama(), Toast.LENGTH_SHORT).show();
                Intent keChat = new Intent(activity, ChatActivity.class);

                // bawa id dan nama ke class chat
                keChat.putExtra("idKontak", itemKontak.get(position).getId());
                keChat.putExtra("statusKontak", itemKontak.get(position).getStatus());
                keChat.putExtra("kirim ke", itemKontak.get(position).getNama());
                keChat.putExtra("fotoProfil", itemKontak.get(position).getUrlFotoKontak());
                activity.startActivity(keChat);
            }
        });

        fotoKontak.setVisibility(View.GONE);
        progressKontak.setVisibility(View.VISIBLE);

        //ambil data tiap baris
        KontakObject dataKontak = itemKontak.get(position);

        // pasang teksnya
        nama.setText(dataKontak.getNama());
        id_user.setText(dataKontak.getId());

        // Cargo image loader (progress bar loading muncul ketika image blm terambil)
        String imageUrl = dataKontak.getUrlFotoKontak();

        if(imageLoader !=null && imageUrl!=null) {
            imageLoader.get(imageUrl, new ImageLoader.ImageListener() {


                @Override
                public void onErrorResponse(VolleyError error) {

                    progressKontak.setVisibility(View.GONE);

                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        fotoKontak.setVisibility(View.VISIBLE);
                        fotoKontak.setImageBitmap(response.getBitmap());
                        progressKontak.setVisibility(View.GONE);
                    }

                }
            });


            fotoKontak.setImageUrl(imageUrl, imageLoader);
        }

        //tampilkan view nya
        return v;
    }


    //    =============================== PENCARIAN MAHASISWA ================================
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<KontakObject> results = new ArrayList<KontakObject>();
                if (searchKontak == null)
                    searchKontak = itemKontak;
                if (constraint != null) {
                    if (searchKontak != null && searchKontak.size() > 0) {
                        for (final KontakObject ko : searchKontak) {
                            if (ko.getId().toLowerCase()
                                    .contains(constraint.toString())){
                                results.add(ko);
                            }else if (ko.getNama().toLowerCase().contains(constraint.toString())){
                                results.add(ko);
                            }

                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                itemKontak = (ArrayList<KontakObject>) results.values;
                notifyDataSetChanged();
            }
        };
    }


}


