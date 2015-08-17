package ru.audiogid.krsk.stolby.notification;

import java.util.HashMap;

import ru.audiogid.krsk.stolby.audio.AudioActivity;
import ru.audiogid.krsk.stolby.R;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationUtils {

    private static NotificationUtils instance;

    private Context mContext;

    private NotificationManager mNotificationManager; // Системная утилита,
                                                      // упарляющая
                                                      // уведомлениями

    private int lastId = 0; // постоянно увеличивающееся поле, уникальный номер
                            // каждого уведомления

    private HashMap<Integer, Notification> mNotifications; // массив
                                                           // ключ-значение на
                                                           // все отображаемые
                                                           // пользователю
                                                           // уведомления

    @SuppressLint("UseSparseArrays")
    private NotificationUtils(final Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifications = new HashMap<Integer, Notification>();
    }

    public static NotificationUtils getInstance(final Context context) {
        if (instance == null) {
            instance = new NotificationUtils(context);
        } else {
            instance.mContext = context;
        }
        return instance;
    }

    @SuppressWarnings("deprecation")
    public int createInfoNotification(final String message) {
        Intent notificationIntent = new Intent(mContext,
                ProximityActivity.class);
        // Intent notificationIntent = new Intent();// по клику на уведомлении
        // откроется HomeActivity
        NotificationCompat.Builder nb = new NotificationCompat.Builder(mContext)
                // NotificationCompat.Builder nb = new
                // NotificationBuilder(context) //для версии Android > 3.0
                .setSmallIcon(R.drawable.logo)
                // иконка уведомления
                .setAutoCancel(true)
                // уведомление закроется по клику на него
                .setTicker(message)
                // текст, который отобразится вверху статус-бара при создании
                // уведомления
                .setContentText(message)
                // Основной текст уведомления
                .setContentIntent(
                        PendingIntent.getActivity(mContext, 0,
                                notificationIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT))
                .setWhen(System.currentTimeMillis()) // отображаемое время
                                                     // уведомления
                .setContentTitle("Столбы") // заголовок уведомления
                .setDefaults(Notification.DEFAULT_ALL); // звук, вибро и диодный
                                                        // индикатор
                                                        // выставляются по
                                                        // умолчанию

        Notification notification = nb.getNotification(); // генерируем
                                                          // уведомление
        mNotificationManager.cancelAll();
        mNotificationManager.notify(lastId, notification); // отображаем его
                                                           // пользователю.
        mNotifications.put(lastId, notification); // теперь мы можем обращаться
                                                  // к нему по id
        return lastId++;
    }

    @SuppressWarnings("deprecation")
    public int createProximityNotification(final String title,
            final String audio) {
        Intent notificationIntent = createAudioIntent(title, audio);
        // Intent notificationIntent = new Intent();// по клику на уведомлении
        // откроется HomeActivity
        NotificationCompat.Builder nb = new NotificationCompat.Builder(mContext)
                // NotificationCompat.Builder nb = new
                // NotificationBuilder(context) //для версии Android > 3.0
                .setSmallIcon(R.drawable.logo)
                // иконка уведомления
                .setAutoCancel(true)
                // уведомление закроется по клику на него
                .setTicker(title)
                // текст, который отобразится вверху статус-бара при создании
                // уведомления
                .setContentText("")
                // Основной текст уведомления
                .setContentIntent(
                        PendingIntent.getActivity(mContext, 0,
                                notificationIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT))
                .setWhen(System.currentTimeMillis()) // отображаемое время
                                                     // уведомления
                .setContentTitle(title) // заголовок уведомления
                .setDefaults(Notification.DEFAULT_ALL); // звук, вибро и диодный
                                                        // индикатор
                                                        // выставляются по
                                                        // умолчанию

        Notification notification = nb.getNotification(); // генерируем
                                                          // уведомление
        mNotificationManager.cancelAll();
        mNotificationManager.notify(lastId, notification); // отображаем его
                                                           // пользователю.
        mNotifications.put(lastId, notification); // теперь мы можем обращаться
                                                  // к нему по id
        return lastId++;
    }

    private Intent createAudioIntent(final String title, final String audio) {
        Intent intent = new Intent(mContext, AudioActivity.class);
        intent.putExtra("point_title", title);
        intent.putExtra("point_audio", audio);
        return intent;
    }
}
