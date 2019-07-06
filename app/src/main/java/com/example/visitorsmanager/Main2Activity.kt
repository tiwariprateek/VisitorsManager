package com.example.visitorsmanager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import com.camerakit.CameraKitView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.activity_photo.*
import java.io.File
import java.io.FileOutputStream

class Main2Activity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
            //var number=intent.extras.getString("Number")
            val i=intent.getStringExtra("Numbers")
            textView.text=("You are visitor number $i")
            Log.v("Prateek","Value of counter $i")
//            var i=intent
//            var name=i.getStringExtra("Number")
//            textView.text=name
    //countUser()
    }

fun countUser(){
    val ref=FirebaseDatabase.getInstance().getReference()
    ref.addListenerForSingleValueEvent(object :ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(p0: DataSnapshot) {
            for (snap in p0.children) {
                val counter = snap.childrenCount
                Log.v("Count", "Number of users ($counter)")
               textView.text= counter.toString()
            }

        }
    })
}
}