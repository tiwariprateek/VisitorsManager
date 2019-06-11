package com.example.visitorsmanager

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    var storedVerificationId:String?=null
    private val auth=FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //button?.alpha=0f
        button2.setOnClickListener {
            startActivity(Intent(this,PhotoActivity::class.java))
        }
        submit.setOnClickListener {
            uploadtouser()

            //activity()
        }
        button.setOnClickListener {
            otpverification()

        }

        }
    fun activity(){
        val intent = Intent(this@MainActivity, Main2Activity::class.java)
        startActivity(intent)
    }


    private fun otpverification(){
    val phonenumber=PhoneNumber.text.toString()
    val otp=OTP.text.toString()
        if (phonenumber.isEmpty()||phonenumber.length!=13){
            Toast.makeText(this,"Enter valid details",Toast.LENGTH_SHORT).show()
        }
    PhoneAuthProvider.getInstance().verifyPhoneNumber(
        phonenumber,
        60,
        TimeUnit.SECONDS,
        this,
        callbacks
    )

    }
    val callbacks=object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("Phone number","onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException?) {
        Log.d("Phone number","Verification failed",e)
            if (e is FirebaseAuthInvalidCredentialsException){
            Log.d("Phone number","Exception")
            }else if (e is FirebaseTooManyRequestsException){
                Log.d("Phone number","Exception2")
            }
        }
        private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task: Task<AuthResult> ->
                        if (task.isSuccessful) {
                            activity()
                        }
                    }

        }


        override fun onCodeSent(verificationId: String?, token: PhoneAuthProvider.ForceResendingToken?) {
            Log.d("Phone number","Code sent:"+verificationId!!)
            val otp=OTP.text.toString()
            storedVerificationId=verificationId
            Log.d("OTP","Verification code is $storedVerificationId")

            val resendtoken=token
            //val credential=PhoneAuthProvider.getCredential(verificationId!!,otp)

        }

    }


fun uploadtouser(){
    val uid=FirebaseAuth.getInstance().uid?:""
    val ref=FirebaseDatabase.getInstance().getReference("/Visitors/$uid")
    val phonenumber=PhoneNumber.text.toString()
  ref.setValue(phonenumber)
      .addOnSuccessListener {
          Toast.makeText(this,"Sucess",Toast.LENGTH_SHORT).show()
      }
}




    }