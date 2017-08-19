package com.uii.academico.FCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

//import com.google.android.gms.gcm.GcmListenerService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.uii.academico.Activity.ChatActivity;
import com.uii.academico.Activity.ChatGroupActivity;
import com.uii.academico.Activity.LihatMahasiswaBimbinganActivity;
import com.uii.academico.Activity.MainActivity;
import com.uii.academico.Activity.RiwayatBimbinganActivity;
import com.uii.academico.R;

import java.util.Map;


public class FcmMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage message){
//        String from = message.getFrom();
        Map data = message.getData();

        String tipePushNotif = data.get("tipe_pushNotif").toString();


        if (tipePushNotif.equals("personal")) {                           // PERSONAL
            String idPengirim = data.get("id_pengirim").toString();
            String pengirim = data.get("nama_pengirim").toString();
            String pesan = data.get("text_message").toString();
            String waktu = data.get("waktu_pesan").toString();
            String statusPengirim = data.get("status_pengirim").toString();
            String fotoPengirim = data.get("foto_pengirim").toString(); // foto pengirim

            Log.i("COBA LIHAT ", data.toString());

            // Method menampilkan Pesan Notifikasi
            bawaNotifKeChatPersonal(idPengirim, pengirim, pesan, statusPengirim, fotoPengirim);

            // Intent untuk Broadcast ke semua activity aplikasi (ditangkap ChatActivity)
            // TANPA melalui klik Notifikasi langsung ke ChatActivity
            Intent sendIntent = new Intent(tipePushNotif);

            sendIntent.putExtra("pesannya", pesan);
            sendIntent.putExtra("idPengirim", idPengirim);
            sendIntent.putExtra("namaPengirim", pengirim);
            sendIntent.putExtra("waktu", waktu);

            LocalBroadcastManager.getInstance(this).sendBroadcast(sendIntent);



        }else if (tipePushNotif.equals("group")){                        // GROUP

            String idPengirim = data.get("id_pengirim").toString();
            String idChatroom = data.get("id_chatroom").toString();
            String pengirim = data.get("nama_pengirim").toString();
            String namaGroup = data.get("nama_group").toString();
            String pesan = data.get("pesan").toString();
            String waktu = data.get("waktu_pesan").toString();
            String fotoMember = data.get("foto_member").toString();

            Log.i("COBA LIHAT ", data.toString());

            // Method menampilkan Pesan Notifikasi
            bawaNotifKeChatGroup(idChatroom, namaGroup, pengirim, pesan);

            // Intent untuk Broadcast ke semua activity aplikasi (ditangkap ChatActivity)
            // TANPA melalui klik Notifikasi langsung ke ChatGroupActivity
            Intent sendIntent = new Intent(tipePushNotif);

            sendIntent.putExtra("id_pengirim", idPengirim);
            sendIntent.putExtra("pesannya", pesan);
            sendIntent.putExtra("namaPengirim", pengirim);
            sendIntent.putExtra("namaGroup", namaGroup);
            sendIntent.putExtra("waktu", waktu);
            sendIntent.putExtra("fotoMember", fotoMember);


            LocalBroadcastManager.getInstance(this).sendBroadcast(sendIntent);



        }else if (tipePushNotif.equals("informasi")){


            String info = data.get("info").toString(); // notifikasi informasi jurusan


            bawaNotifKeMainMenu(info);

        }else if (tipePushNotif.equals("jadwalbimbingan")){


            String info = data.get("info").toString(); // notifikasi jadwal bimbingan bagi mahasiswa


            bawaNotifKeRiwayatBimbingan(info);

        }else if (tipePushNotif.equals("submittopik")){


            String info = data.get("info").toString(); // notifikasi jadwal bimbingan bagi mahasiswa


            bawaNotifKeLihatMahasiswaBimbingan(info);

        }


    }





    // Item2 yang diambil dari GCM akan di tampilkan di Notifikasi
    private void bawaNotifKeChatPersonal(String idPengirim, String pengirim, String message, String statusPengirim, String fotoProfil) {

        // Kirim ke Chat Group Activity MELALUI klik notifikasi
        Intent intent = new Intent(this, ChatActivity.class);

        // Samakan putExtra dengan yang ada di Chat Activity
        intent.putExtra("idKontak",idPengirim);
        intent.putExtra("kirim ke", pengirim);
        intent.putExtra("statusKontak", statusPengirim); // mengetahui status pengirim agar bisa balas pesan ke pengirim
        intent.putExtra("fotoProfil", fotoProfil);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int requestCode = 0;//Your request code
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);


        //Build notification
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.academico)
                .setContentTitle (pengirim + " - Academico")
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        //Vibration
        notifBuilder.setVibrate(new long[]{1000, 1000});
        //Sound
        notifBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notifBuilder.build()); //0 = ID of notification
    }

    private void bawaNotifKeChatGroup(String idChatroom, String namaChatroom, String namaPengirim, String message) {

        // Kirim ke Chat Activity MELALUI klik notifikasi
        Intent intent = new Intent(this, ChatGroupActivity.class);

        // Samakan putExtra dengan yang ada di Chat Activity
        intent.putExtra("idObrolan",idChatroom);                // ID Chatroom
        intent.putExtra("kirim ke", namaChatroom);              // Nama Chatroom
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int requestCode = 0;//Your request code
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);


        //Build notification
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.academico)
                .setContentTitle (namaPengirim + " - " + namaChatroom)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        //Vibration
        notifBuilder.setVibrate(new long[]{1000, 1000});
        //Sound
        notifBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notifBuilder.build()); //0 = ID of notification
    }



    // Item2 yang diambil dari GCM akan di tampilkan di Notifikasi
    private void bawaNotifKeMainMenu(String info) {

        Intent intent = new Intent(this, MainActivity.class);

        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int requestCode = 0;//Your request code
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);


        //Build notification
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.academico)
                .setContentTitle ("Academico")
                .setContentText(info)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        //Vibration
        notifBuilder.setVibrate(new long[]{1000, 1000});
        //Sound
        notifBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notifBuilder.build()); //0 = ID of notification
    }

    // Item2 yang diambil dari GCM akan di tampilkan di Notifikasi
    private void bawaNotifKeRiwayatBimbingan(String info) {

        Intent intent = new Intent(this, RiwayatBimbinganActivity.class);

        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int requestCode = 0;//Your request code
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);


        //Build notification
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.academico)
                .setContentTitle ("Academico")
                .setContentText(info)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        //Vibration
        notifBuilder.setVibrate(new long[]{1000, 1000});
        //Sound
        notifBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notifBuilder.build()); //0 = ID of notification
    }



    // Item2 yang diambil dari GCM akan di tampilkan di Notifikasi
    private void bawaNotifKeLihatMahasiswaBimbingan(String info) {

        Intent intent = new Intent(this, LihatMahasiswaBimbinganActivity.class);

        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int requestCode = 0;//Your request code
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);


        //Build notification
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.academico)
                .setContentTitle ("Academico")
                .setContentText(info)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        //Vibration
        notifBuilder.setVibrate(new long[]{1000, 1000});
        //Sound
        notifBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notifBuilder.build()); //0 = ID of notification
    }



}
