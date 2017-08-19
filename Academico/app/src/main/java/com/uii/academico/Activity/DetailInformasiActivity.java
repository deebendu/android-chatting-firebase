package com.uii.academico.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.uii.academico.R;
import com.uii.academico.Volley.MySingleton;

public class DetailInformasiActivity extends AppCompatActivity {

    ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_informasi);

        // tangkap URL dari intent FragmentInformasi
        String extraUrlImage = getIntent().getExtras().getString("urlGambarInfo");
        String extraTitle = getIntent().getExtras().getString("titleInfo");

        final NetworkImageView gambarDetailInfo = (NetworkImageView) findViewById(R.id.IV_detailInfo);
        final NetworkImageView BGDetailInfo = (NetworkImageView) findViewById(R.id.IV_BGdetailInfo);

        TextView titleInfo = (TextView) findViewById(R.id.TextTitleInfo);
        titleInfo.setText(extraTitle);


        imageLoader = MySingleton.getInstance(this).getImageLoader();

        // Cargo image loader (progress bar loading muncul ketika image blm terambil)
        String imageUrl = extraUrlImage;

        if(imageLoader !=null && imageUrl!=null) {
            imageLoader.get(imageUrl, new ImageLoader.ImageListener() {


                @Override
                public void onErrorResponse(VolleyError error) {

//                    progressImageLoad.setVisibility(View.GONE);

                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
//                        gambarInformasi.setVisibility(View.VISIBLE);
                        gambarDetailInfo.setImageBitmap(response.getBitmap());
                        BGDetailInfo.setImageBitmap(response.getBitmap());
//                        progressImageLoad.setVisibility(View.GONE);
//                        System.out.println("Done loading " + imageUrl);
                    }

                }
            });


            gambarDetailInfo.setImageUrl(imageUrl, imageLoader);
            BGDetailInfo.setImageUrl(imageUrl, imageLoader);
        }
    }
}
