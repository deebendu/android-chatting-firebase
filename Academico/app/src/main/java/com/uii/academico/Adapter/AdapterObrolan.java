package com.uii.academico.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.uii.academico.Model.ObrolanObject;
import com.uii.academico.R;
import com.uii.academico.Volley.MySingleton;

import java.util.List;

/**
 * Created by Fakhrus on 5/27/16.
 */
public class AdapterObrolan extends BaseAdapter {

    Context activity;
    List<ObrolanObject> itemObrolan;

    public AdapterObrolan(Context activity, List<ObrolanObject> itemObrolan) {
        this.activity = activity;
        this.itemObrolan = itemObrolan;
    }

    @Override
    public int getCount() {
        return itemObrolan.size();
    }

    @Override
    public Object getItem(int position) {
        return itemObrolan.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = View.inflate(activity, R.layout.item_obrolan, null);

        ImageLoader imageLoader = MySingleton.getInstance(activity).getImageLoader();

        //panggil ID
        TextView namaChatRoom = (TextView) v.findViewById(R.id.namaChatroom);
        TextView pesanTerakhir = (TextView) v.findViewById(R.id.pesanTerakhir);

        final NetworkImageView fotoObrolan = (NetworkImageView) v.findViewById(R.id.IV_fotoObrolan);
        final ProgressBar progressObrolan = (ProgressBar) v.findViewById(R.id.PB_progresObrolan);
        final RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.RL_warnaObrolan);

        fotoObrolan.setVisibility(View.GONE);
        progressObrolan.setVisibility(View.VISIBLE);

        //ambil data tiap baris
        ObrolanObject dataObrolan = itemObrolan.get(position);

        // pasang teksnya
        namaChatRoom.setText(dataObrolan.getNamaObrolan());
        pesanTerakhir.setText(dataObrolan.getPesanTerakhir());

        // Cargo image loader (progress bar loading muncul ketika image blm terambil)
        String imageUrl = dataObrolan.getUrlFotoObrolan();

        if(imageLoader !=null && imageUrl!=null) {
            imageLoader.get(imageUrl, new ImageLoader.ImageListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    progressObrolan.setVisibility(View.GONE);

                }
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        fotoObrolan.setVisibility(View.VISIBLE);
                        fotoObrolan.setImageBitmap(response.getBitmap());
                        progressObrolan.setVisibility(View.GONE);
                    }

                }
            });

            fotoObrolan.setImageUrl(imageUrl, imageLoader);
        }

        if (dataObrolan.getTipeObrolan().equals("chat_dpa") || dataObrolan.getTipeObrolan().equals("chat_kajur")){
            relativeLayout.setBackgroundColor(Color.parseColor("#ffb007"));
        } else if (dataObrolan.getTipeObrolan().equals("chat_mhs")){
            relativeLayout.setBackgroundColor(Color.parseColor("#64b5f6"));
        }else {
            // default warna biru tua
        }

        return v;
    }
}
