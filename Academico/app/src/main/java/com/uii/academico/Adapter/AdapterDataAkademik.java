package com.uii.academico.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.uii.academico.Model.DataAkademikObject;
import com.uii.academico.Model.RiwayatObject;
import com.uii.academico.R;

import java.util.List;

/**
 * Created by Fakhrus on 4/10/16.
 */
public class AdapterDataAkademik extends BaseAdapter {

    Context activity;
    List<DataAkademikObject> itemDataAkademik;


    public AdapterDataAkademik(Context activity, List<DataAkademikObject> itemDataAkademik) {
        this.activity = activity;
        this.itemDataAkademik= itemDataAkademik;
    }

    @Override
    public int getCount() { return itemDataAkademik.size(); }

    @Override
    public Object getItem(int position) {
        return itemDataAkademik.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //inflate
        View v = View.inflate(activity, R.layout.item_data_akademik, null);

        //panggil ID
        TextView sem = (TextView) v.findViewById(R.id.data_Semester);
        TextView ip = (TextView) v.findViewById(R.id.data_IP);
        TextView sks = (TextView) v.findViewById(R.id.data_SKS);
        TextView kp = (TextView) v.findViewById(R.id.data_KP);
        TextView ta = (TextView) v.findViewById(R.id.data_TA);
        TextView cuti = (TextView) v.findViewById(R.id.data_Cuti);
        TextView btaq = (TextView) v.findViewById(R.id.data_BTAQ);
        TextView kkn = (TextView) v.findViewById(R.id.data_KKN);
        TextView cept = (TextView) v.findViewById(R.id.data_CEPT);
        TextView kegiatan = (TextView) v.findViewById(R.id.data_Kegiatan);
        ImageView icon = (ImageView) v.findViewById(R.id.valid);

        //Inisialisasi objek dan ambil data tiap baris
        DataAkademikObject dataAkademik = itemDataAkademik.get(position);

        // pasang teksnya
        sem.setText(dataAkademik.getSemester());
        ip.setText(dataAkademik.getIp());
        sks.setText(dataAkademik.getSks());
        kp.setText(dataAkademik.getKp());
        ta.setText(dataAkademik.getTa());
        cuti.setText(dataAkademik.getCuti());
        btaq.setText(dataAkademik.getBtaq());
        kkn.setText(dataAkademik.getKkn());
        cept.setText(dataAkademik.getCept());
        kegiatan.setText(dataAkademik.getKegiatan());

        // Indikator Valid
        String indikatorValid = dataAkademik.getValidasi_dosen();

        if (indikatorValid.equals("1")){
            icon.setVisibility(View.VISIBLE);

        }else {
            icon.setVisibility(View.GONE);

        }

        //tampilkan view nya
        return v;
    }
}
