package com.arobit.serviceexample

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.*

class MainActivity : AppCompatActivity() {

    var myDownload: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val song_1_url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
        val song_1 = "SoundHelix-Song-2.mp3"

        val song_2_url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        val song_2 = "SoundHelix-Song-1.mp3"


        val fileList = listOfFile("/ServiceExample")
        if (fileList.size > 0) {
            Log.e("LISTOFFILE", fileList.toString())
        }

        for (i in 0 until fileList.size) {
            encryptFile("/ServiceExample/${fileList[i]}")
        }


        val songExist = File(
            Environment.getExternalStorageDirectory()
                .toString() + "/ServiceExample/SoundHelix-Song-2.mp3"
        )
        if (songExist.exists()) {
            download_image_1.setImageResource(R.drawable.ic_baseline_playlist_add_check_24)
        }

        download_button_1.setOnClickListener {

            val direct = File(
                Environment.getExternalStorageDirectory()
                    .toString() + "/ServiceExample"
            )
            if (!direct.exists()) {
                direct.mkdirs()
            }

            val request: DownloadManager.Request = DownloadManager.Request(
                Uri.parse(song_1_url)
            )
                .setTitle(song_1)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setAllowedOverMetered(true)
                .setDestinationInExternalPublicDir("/ServiceExample", song_1)

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


        val songExist_2 = File(
            Environment.getExternalStorageDirectory()
                .toString() + "/ServiceExample/SoundHelix-Song-1.mp3"
        )
        if (songExist_2.exists()) {
            download_image_2.setImageResource(R.drawable.ic_baseline_playlist_add_check_24)
        }

        download_button_2.setOnClickListener {

            val direct = File(
                Environment.getExternalStorageDirectory()
                    .toString() + "/ServiceExample"
            )
            if (!direct.exists()) {
                direct.mkdirs()
            }

            val request: DownloadManager.Request = DownloadManager.Request(
                Uri.parse(song_2_url)
            )
                .setTitle(song_2)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setAllowedOverMetered(true)
                .setDestinationInExternalPublicDir("/ServiceExample", song_2)

            val dm: DownloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            myDownload = dm.enqueue(request)

        }

        val br_2 = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id: Long = intent!!.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == myDownload) {
                    Toast.makeText(this@MainActivity, "Download complete", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        registerReceiver(br_2, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

    }

    fun listOfFile(dir: String): ArrayList<String> {
        val fileList = ArrayList<String>()
        val path = Environment.getExternalStorageDirectory().toString() + dir
        val directory = File(path)
        val files = directory.listFiles()
        for (i in files.indices) {
            fileList.add(files[i].name)
        }
        return fileList
    }

    @Throws(
        IOException::class,
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class
    )
    private fun encryptFile(filePath: String) {
        var read: Int
        val outfile = File(
            Environment.getExternalStorageDirectory()
                .toString() + filePath
        )
        val fis = FileInputStream(File(outfile.toString()))

        if (!outfile.exists()) outfile.createNewFile()
        val fos = FileOutputStream(outfile)
        val encipher: Cipher = Cipher.getInstance("AES")
        val kgen: KeyGenerator = KeyGenerator.getInstance("AES")
        //byte key[] = {0x00,0x32,0x22,0x11,0x00,0x00,0x00,0x00,0x00,0x23,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
        val skey: SecretKey = kgen.generateKey()
        encipher.init(Cipher.ENCRYPT_MODE, skey)
        val cis = CipherInputStream(fis, encipher)
        val buffer = ByteArray(1024) // buffer can read file line by line to increase speed
        while (cis.read(buffer).also { read = it } >= 0) {
            fos.write(buffer, 0, read)
            fos.flush()
        }
        fos.close()
        Toast.makeText(this, "File encrypted", Toast.LENGTH_SHORT).show()

        //call method for decrypt file.
        decryptFile(outfile.toString(), skey);

    }


    @Throws(
        IOException::class,
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class
    )
    private fun decryptFile(encryptFilePath: String, secretKey: SecretKey) {
        var read: Int
        val outfile = File(encryptFilePath)
        val decfile = File(encryptFilePath)
        if (!decfile.exists()) decfile.createNewFile()
        val decfos = FileOutputStream(decfile)
        val encfis = FileInputStream(outfile)
        val decipher = Cipher.getInstance("AES")
        decipher.init(Cipher.DECRYPT_MODE, secretKey)
        val cos = CipherOutputStream(decfos, decipher)
        val buffer = ByteArray(1024) // buffer can read file line by line to increase speed
        while (encfis.read(buffer).also { read = it } >= 0) {
            cos.write(buffer, 0, read)
            cos.flush()
        }
        Toast.makeText(this, "File decrypted", Toast.LENGTH_SHORT).show()
    }

}