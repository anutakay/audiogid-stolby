package ru.audiogid.krsk.stolby.notification;

import java.util.HashMap;

import ru.audiogid.krsk.stolby.audio.AudioActivity;

import com.example.audiogid.R;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationUtils {
	
	private static NotificationUtils instance;
	
	private Context context;
	private NotificationManager manager; // Системная утилита, упарляющая уведомлениями
	private int lastId = 0; //постоянно увеличивающееся поле, уникальный номер каждого уведомления
	private HashMap<Integer, Notification> notifications; //массив ключ-значение на все отображаемые пользователю уведомления

	@SuppressLint("UseSparseArrays")
	private NotificationUtils(Context context){
	    this.context = context;
	    manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    notifications = new HashMap<Integer, Notification>();
	  }
	
	public static NotificationUtils getInstance(Context context){
	    if(instance == null){
	        instance = new NotificationUtils(context);
	    } else{
	        instance.context = context;
	    }
	    return instance;
	  }
	
	@SuppressWarnings("deprecation")
	public int createInfoNotification(String message){
	    Intent notificationIntent = new Intent(context, ProximityActivity.class);
		// Intent notificationIntent = new Intent();// по клику на уведомлении откроется HomeActivity
	    NotificationCompat.Builder nb = new NotificationCompat.Builder(context)
	//NotificationCompat.Builder nb = new NotificationBuilder(context) //для версии Android > 3.0
	        .setSmallIcon(R.drawable.ic_launcher) //иконка уведомления
	        .setAutoCancel(true) //уведомление закроется по клику на него
	        .setTicker(message) //текст, который отобразится вверху статус-бара при создании уведомления
	        .setContentText(message) // Основной текст уведомления
	        .setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT))
	        .setWhen(System.currentTimeMillis()) //отображаемое время уведомления
	        .setContentTitle("Столбы") //заголовок уведомления
	        .setDefaults(Notification.DEFAULT_ALL); // звук, вибро и диодный индикатор выставляются по умолчанию

	        Notification notification = nb.getNotification(); //генерируем уведомление
	        manager.cancelAll();
	        manager.notify(lastId, notification); // отображаем его пользователю.
	        notifications.put(lastId, notification); //теперь мы можем обращаться к нему по id
	        return lastId++;
	  }
	
	@SuppressWarnings("deprecation")
	public int createProximityNotification(final String title, final String audio){
	    Intent notificationIntent = createAudioIntent(title, audio);
		// Intent notificationIntent = new Intent();// по клику на уведомлении откроется HomeActivity
	    NotificationCompat.Builder nb = new NotificationCompat.Builder(context)
	//NotificationCompat.Builder nb = new NotificationBuilder(context) //для версии Android > 3.0
	        .setSmallIcon(R.drawable.ic_launcher) //иконка уведомления
	        .setAutoCancel(true) //уведомление закроется по клику на него
	        .setTicker(title) //текст, который отобразится вверху статус-бара при создании уведомления
	        .setContentText("") // Основной текст уведомления
	        .setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT))
	        .setWhen(System.currentTimeMillis()) //отображаемое время уведомления
	        .setContentTitle(title) //заголовок уведомления
	        .setDefaults(Notification.DEFAULT_ALL); // звук, вибро и диодный индикатор выставляются по умолчанию

	        Notification notification = nb.getNotification(); //генерируем уведомление
	        manager.cancelAll();
	        manager.notify(lastId, notification); // отображаем его пользователю.
	        notifications.put(lastId, notification); //теперь мы можем обращаться к нему по id
	        return lastId++;
	  }
	
	private Intent createAudioIntent(final String title, final String audio) {
		Intent intent = new Intent(context, AudioActivity.class);
	    intent.putExtra("point_title", title);
	    intent.putExtra("point_audio", audio);
	    return intent;
	}
}
