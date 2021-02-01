@file:Suppress("PrivatePropertyName")

package hu.daniel.abtestingdemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class ABTestingDemoFirebaseMessagingService: FirebaseMessagingService() {

    private val CHANNEL_ID = "128973"
    private val NOTIFICATION_ID = 23765

    override fun onMessageReceived(message: RemoteMessage) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.run {
                createNotificationChannel(NotificationChannel(CHANNEL_ID, "CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH))
            }
        }

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }

    override fun onNewToken(token: String) {
        Log.e("FIREBASE_ID_NEW", token)
    }
}