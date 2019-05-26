package com.example.visitorsmanager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Button
import com.camerakit.CameraKit
import com.camerakit.CameraKitView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private var cameraKitView: CameraKitView? = null
    private var photobutton: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        photobutton = findViewById(R.id.photobutton)
        cameraKitView = findViewById(R.id.camera)
        photobutton?.setOnClickListener(photoonclicklistner)
    }


    override fun onStart() {
        cameraKitView?.onStart()
        super.onStart()
    }

    override fun onResume() {
        cameraKitView?.onResume()
        super.onResume()
    }

    override fun onPause() {
        cameraKitView?.onPause()
        super.onPause()
    }

    override fun onStop() {
        cameraKitView?.onStop()
        super.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        cameraKitView?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private val photoonclicklistner = object : View.OnClickListener {
        override fun onClick(v: View?) {
            cameraKitView?.captureImage(object:CameraKitView.ImageCallback{
                override fun onImage(p0: CameraKitView?, p1: ByteArray?) {
                    val  loaction=filesDir
                    val savedphoto=File(Environment.getExternalStorageDirectory(),"photo.jpg")
             try {
                 val outputStream=FileOutputStream(savedphoto.path)
                 outputStream.write(p1)
                 outputStream.close()
                 Log.e("Error","Did it worked")
             }
             catch (e:java.io.IOException){
                 e.printStackTrace()
                 Log.e("error","This didn't worked")
             }
                }

            })
        }
    }
}