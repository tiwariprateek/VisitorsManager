package com.example.visitorsmanager

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.camerakit.CameraKitView
import id.zelory.compressor.Compressor
import java.io.File
import java.io.FileOutputStream

class PhotoActivity : AppCompatActivity() {
    lateinit var cameraKitView: CameraKitView
    lateinit var photobutton: Button
    var imageView: ImageView? = null
    var button: Button? = null
    lateinit var compressedimage: Bitmap
    lateinit var savedphoto: File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        photobutton = findViewById(R.id.photobutton)
        cameraKitView = findViewById(R.id.camera)
        imageView = findViewById(R.id.DisplayView)
        button = findViewById(R.id.Selectphotobutton)
        photobutton.setOnClickListener(photoonclicklistner)
    }

    override fun onStart() {
        cameraKitView.onStart()
        super.onStart()
    }

    override fun onResume() {
        cameraKitView.onResume()
        super.onResume()
    }

    override fun onPause() {
        cameraKitView.onPause()
        super.onPause()
    }

    override fun onStop() {
        cameraKitView.onStop()
        super.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    var photoselected: Uri? = null
    private val photoonclicklistner = object : View.OnClickListener {
        override fun onClick(v: View?) {
            cameraKitView.captureImage(object : CameraKitView.ImageCallback {
                override fun onImage(cameraKitView: CameraKitView, capturedimage: ByteArray) {
                    val loaction = filesDir
                    savedphoto = File(Environment.getExternalStorageDirectory(), "photo.jpg")

                    try {
                        val outputStream = FileOutputStream(savedphoto.path)
                        outputStream.write(capturedimage)
                        outputStream.close()
                        Log.e("Error", "Did it worked ($savedphoto)+($capturedimage)")
                    } catch (e: java.io.IOException) {
                        e.printStackTrace()
                        Log.e("error", "This didn't worked")
                    }
                    start()
                }
            })
        }
    }
    fun imagedisplay() {
        val compressedFile=File(Environment.getExternalStorageDirectory(),"Compressedimage")
        val compress=Compressor(this).setDestinationDirectoryPath(compressedFile.path).compressToFile(savedphoto)
        imageView?.setImageBitmap(BitmapFactory.decodeFile("${savedphoto.path}"))
    }
    fun start() {
        imagedisplay()
        val compressedFile=File(Environment.getExternalStorageDirectory(),"Compressedimage")
        val compress=Compressor(this).setDestinationDirectoryPath(compressedFile.path).compressToFile(savedphoto)
        val photouri=Uri.fromFile(compress)
        val i2 = Intent(this, MainActivity::class.java)
        i2.putExtra("Photo",photouri.toString())
        startActivity(i2)
        finish()
    }
}


