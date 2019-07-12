package com.example.visitorsmanager

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.camerakit.CameraKitView
import id.zelory.compressor.Compressor
import java.io.File
import java.io.FileOutputStream

class PhotoActivity : AppCompatActivity() {
    private lateinit var cameraKitView: CameraKitView
    private lateinit var photobutton: Button
    private var imageView: ImageView? = null
    private var button: Button? = null
    private lateinit var savedphoto: File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        photobutton = findViewById(R.id.photobutton)
        cameraKitView = findViewById(R.id.camera)
        imageView = findViewById(R.id.DisplayView)
        button = findViewById(R.id.Selectphotobutton)
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            }
            else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),0)
            }
        }
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

    private val photoonclicklistner = View.OnClickListener {
        cameraKitView.captureImage { cameraKitView, capturedimage ->
            savedphoto = File(Environment.getExternalStorageDirectory(), "photo.jpg")
            try {
                val outputStream = FileOutputStream(savedphoto.path)
                outputStream.write(capturedimage)
                outputStream.close()
                start()
                Log.e("Error", "Did it worked ($savedphoto)+($capturedimage)")
            } catch (e: java.io.IOException) {
                e.printStackTrace()
                Log.e("error", "This didn't worked")
            }
        }
    }
    private fun start() {
        val compressedFile=File(Environment.getExternalStorageDirectory(),"Compressedimage")
        val compress=Compressor(this).setDestinationDirectoryPath(compressedFile.path).compressToFile(savedphoto)
        val photouri=Uri.fromFile(compress)
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("Photo",photouri.toString())
        startActivity(intent)
        finish()
    }
}


