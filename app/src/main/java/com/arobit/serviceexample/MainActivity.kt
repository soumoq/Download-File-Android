package com.arobit.serviceexample

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    var myDownload: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        download_button.setOnClickListener {

            val direct = File(
                Environment.getExternalStorageDirectory()
                    .toString() + "/ServiceExample"
            )

            if (!direct.exists()) {
                direct.mkdirs()
            }

            val request: DownloadManager.Request = DownloadManager.Request(
                Uri.parse("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3")
            )
                .setTitle("SoundHelix-Song-2.mp3")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setAllowedOverMetered(true)
                .setDestinationInExternalPublicDir("/ServiceExample","SoundHelix-Song-2.mp3")

            val dm: DownloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            myDownload = dm.enqueue(request)

        }

        val br = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id: Long = intent!!.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == myDownload) {
                    Toast.makeText(this@MainActivity, "Download complete", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        registerReceiver(br, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

    }
}