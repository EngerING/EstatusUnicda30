package com.example.estatusunicda30.core.model.notify

import  android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.estatusunicda30.R

object AppNotifier {
    private const val CHANNEL_VOTES = "votes"

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = context.getSystemService(NotificationManager::class.java)
            val ch = NotificationChannel(
                CHANNEL_VOTES,
                "Votos",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            mgr.createNotificationChannel(ch)
        }
    }

    fun sendVoteSuccess(context: Context) {
        ensureChannel(context)
        val notif = NotificationCompat.Builder(context, CHANNEL_VOTES)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // pon aquí tu ícono (ic_stat_vote)
            .setContentTitle("Voto enviado")
            .setContentText("Gracias por participar. Tu voto se guardó correctamente.")
            .setAutoCancel(true)
            .build()

        val canPost =
            Build.VERSION.SDK_INT < 33 ||
                    ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

        if (canPost) NotificationManagerCompat.from(context).notify(1001, notif)
    }
}