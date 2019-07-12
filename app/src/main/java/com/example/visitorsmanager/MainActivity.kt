package com.example.visitorsmanager

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var phonenumber: String
    private val countrycode="+91"
    private lateinit var display:ImageView
    private lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var verificationId= ""
    private val auth=FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Selectphotobutton.setOnClickListener {
            val i3=Intent(this@MainActivity,PhotoActivity::class.java)
            startActivity(i3)
        }
        send_otp.setOnClickListener {
            checkuser()
            imagedisplay()

        }
        submit.setOnClickListener {
            authenticate()
        }

        }
    private fun otpverification(){
    verifyFunction()
    phonenumber=PhoneNumber.text.toString()
    val finalPhoneNumber=countrycode+phonenumber
    if (finalPhoneNumber.isEmpty()||finalPhoneNumber.length!=13){
            Toast.makeText(this,"Enter valid details",Toast.LENGTH_SHORT).show()
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
        finalPhoneNumber,
        60,
        TimeUnit.SECONDS,
        this,
        callbacks
    )
    }
    private fun verifyFunction() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onCodeAutoRetrievalTimeOut(p0: String?) {

            }

            override fun onVerificationFailed(e: FirebaseException?) {
               Toast.makeText(this@MainActivity,"OTP not received,Please try again after some time ",Toast.LENGTH_LONG).show()
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Log.d("Phone number", "Exception")
                } else if (e is FirebaseTooManyRequestsException) {
                    Log.d("Phone number", "Exception2")
                }
            }



            override fun onCodeSent(verification: String?, token: PhoneAuthProvider.ForceResendingToken?) {
               super.onCodeSent(verification,token)
                verificationId=verification.toString()
            }
        }
    }
private fun authenticate(){
    val otp=OTP.text.toString()
    val credential:PhoneAuthCredential=PhoneAuthProvider.getCredential(verificationId,otp)
    signInWithPhoneAuthCredential(credential)
}
    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    uploadimage()
                    checkuser()
                    Toast.makeText(this@MainActivity, "Please wait for authentication", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this,"Enter a valid otp",Toast.LENGTH_LONG).show()
                    uploadtosuspicioususer()
                }
            }
    }
private fun imagedisplay(){
    display=findViewById(R.id.DisplayView)
    display.setImageBitmap(BitmapFactory.decodeFile("/storage/emulated/0/photo.jpg"))
}
private fun uploadtouser() {
    val profileImageURL=reference.downloadUrl.toString()
    val uid=FirebaseAuth.getInstance().uid?:""
    val ref=FirebaseDatabase.getInstance().getReference("/Visitors/$phonenumber/")
    phonenumber=PhoneNumber.text.toString()
    visitcounter=1
    val user=User(uid,phonenumber,profileImageURL,visitcounter)
    ref.setValue(user)
}

    private fun uploadtosuspicioususer(){
        val profileImageURL=reference.downloadUrl.toString()
        val uid=FirebaseAuth.getInstance().uid?:""
        val ref=FirebaseDatabase.getInstance().getReference("/suspicious_user/$phonenumber")
        phonenumber=PhoneNumber.text.toString()
        visitcounter=1
        val user=User(uid, phonenumber, profileImageURL,visitcounter)
        ref.setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this,"Sucess",Toast.LENGTH_SHORT).show()
            }
    }
private lateinit var reference:StorageReference
private fun uploadimage(){
    val abc=Uri.parse(intent.getStringExtra("Photo"))
    if (abc==null){
        Toast.makeText(this@MainActivity,"Please Upload Image",Toast.LENGTH_SHORT).show()
    }
    val filename=UUID.randomUUID().toString()
    reference=FirebaseStorage.getInstance().getReference("/images/$filename")
    reference.putFile(abc)
    uploadtouser()
}
class User(val uid:String,val phonenumber:String,val profileImageURL:String,val visitcounter:Int){}
private var visitcounter:Int = 1
private fun checkuser(){
    val uid=FirebaseAuth.getInstance().uid
    phonenumber=PhoneNumber.text.toString()
    val ref=FirebaseDatabase.getInstance().getReference("/Visitors/$phonenumber")
    ref.addListenerForSingleValueEvent(object :ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {
                   }
        override fun onDataChange(p0: DataSnapshot) {
            val binary=p0.exists()
            if(binary==true) {
                val visit=p0.child("visitcounter").value.toString()
                var visitint=visit.toInt()
                visitint += 1
                ref.child("visitcounter").setValue(visitint)
                val finalvisit=p0.child("visitcounter").value.toString()
                val i = Intent(this@MainActivity, Main2Activity::class.java)
                i.putExtra("visitcount", finalvisit)
                startActivity(i)
            }
            else
                if (display==null){
                    Toast.makeText(this@MainActivity,"Please upload the image",Toast.LENGTH_SHORT).show()
                }
                else
                    otpverification()
        }
    })
}

    }