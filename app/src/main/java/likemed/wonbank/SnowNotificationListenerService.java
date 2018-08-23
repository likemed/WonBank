package likemed.wonbank;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

public class SnowNotificationListenerService extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        handler = new Handler();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Bundle extras = sbn.getNotification().extras;
        // String title = extras.getString(NotificationCompat.EXTRA_TITLE);
        CharSequence text = extras.getCharSequence(NotificationCompat.EXTRA_TEXT);
        // CharSequence subText = extras.getCharSequence(NotificationCompat.EXTRA_SUB_TEXT);
        if (sbn.getPackageName().equals("com.shinhan.smartcaremgr") && text.toString().contains("입금")) {
            new ConnectThread(text.toString(), format.format(Long.valueOf(sbn.getPostTime()))).start();
        }
        // com.shinhan.smartcaremgr
    }

    @Override
    @TargetApi(Build.VERSION_CODES.N)
    public void onListenerDisconnected() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Notification listener disconnected - requesting rebind
            requestRebind(new ComponentName(this, NotificationListenerService.class));
        }
    }

    Handler handler;
    class ConnectThread extends Thread {
        String contents;
        String receivedDate;

        public ConnectThread(String tcontents, String treceivedDate) {
            contents = tcontents;
            receivedDate = treceivedDate;
        }
        public void run() {
            try {
                final String output = request(contents, receivedDate);
                handler.post(new Runnable() {
                    public void run() {
                        String response;
                        int count = Integer.parseInt(output);
                        if (count == -1) {
                            response = "전송 실패";
                        } else {
                            response = "전송 성공";
                        }
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            /* Create or update. */
                            NotificationChannel channel = new NotificationChannel("1", "WonBank", NotificationManager.IMPORTANCE_DEFAULT);
                            notificationManager.createNotificationChannel(channel);
                        }
                        Intent appcall = getPackageManager().getLaunchIntentForPackage("likemed.wonokok");
                        appcall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        appcall.putExtra("code", "bank");

                        PendingIntent pendingIntent = PendingIntent.getActivity(SnowNotificationListenerService.this, 0, appcall, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(SnowNotificationListenerService.this, "1");
                        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), android.R.drawable.star_on));
                        builder.setSmallIcon(android.R.drawable.star_on);
                        builder.setTicker("WonBank [" + response + "]");
                        builder.setContentTitle("WonBank");
                        builder.setContentText(contents + " [" + response + "]");
                        builder.setWhen(System.currentTimeMillis());
                        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                        builder.setContentIntent(pendingIntent);
                        builder.setAutoCancel(true);
                        /*builder.setNumber(count);*/
                        notificationManager.notify(0, builder.build());

                        Intent intent_badge = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
                        intent_badge.putExtra("badge_count_package_name", "likemed.wonokok");
                        intent_badge.putExtra("badge_count_class_name", "likemed.wonokok.MainActivity");
                        intent_badge.putExtra("badge_count", count);
                        sendBroadcast(intent_badge);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private String request(String contents, String receivedDate) {
            StringBuilder output = new StringBuilder();
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL("http://wonokok.dothome.co.kr/wonsms.php").openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    String urlParameters = new Uri.Builder()
                            .appendQueryParameter("id", "likemed")
                            .appendQueryParameter("no", "99")
                            .appendQueryParameter("date", receivedDate)
                            .appendQueryParameter("sms", contents)
                            .build()
                            .getEncodedQuery();
                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.writeBytes(urlParameters);
                    wr.flush();
                    wr.close();
                    if (conn.getResponseCode() == 200) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while (true) {
                            String line = reader.readLine();
                            if (line == null) {
                                break;
                            }
                            output.append(line);
                        }
                        reader.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception ex) {
                Log.e("WonBank", "Exception in processing response.", ex);
                ex.printStackTrace();
            }
            return output.toString();
        }
    }
}