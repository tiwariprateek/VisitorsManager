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
import com.google.firebase.auth.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var phonenumber: String
    val countrycode="+91"

    lateinit var display:ImageView
    var storedVerificationId:String?=null
    var counter:Long?=null
    lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var string: String
    var verificationId= ""
    private val auth=FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        //count()

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
               Toast.makeText(this@MainActivity,"Incorrect OTP",Toast.LENGTH_LONG).show()
//                if (e is FirebaseAuthInvalidCredentialsException) {
//                    Log.d("Phone number", "Exception")
//                } else if (e is FirebaseTooManyRequestsException) {
//                    Log.d("Phone number", "Exception2")
//                }
            }



            override fun onCodeSent(verification: String?, token: PhoneAuthProvider.ForceResendingToken?) {
               super.onCodeSent(verification,token)
                verificationId=verification.toString()
//                Log.d("Phone number", "Code sent:" + verificationId!!)
//                val otp = OTP.text.toString()
//                storedVerificationId = verificationId
//                Log.d("OTP", "Verification code is $verificationId")
//
//                val resendtoken = token
//                //val credential=PhoneAuthProvider.getCredential(verificationId!!,otp)
//
//            }

            }
        }
    }
fun authenticate(){
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
                    Toast.makeText(this@MainActivity, "Enter valid details", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this,"Enter a valid otp",Toast.LENGTH_LONG).show()
                    uploadtosuspicious_user()
                }
            }
    }
fun imagedisplay(){
    display=findViewById(R.id.DisplayView)
    display.setImageBitmap(BitmapFactory.decodeFile("/storage/emulated/0/photo.jpg"))
}
fun uploadtouser() {
    val profileImageURL=reference.downloadUrl.toString()
    val uid=FirebaseAuth.getInstance().uid?:""
    val ref=FirebaseDatabase.getInstance().getReference("/Visitors/$phonenumber/")
    phonenumber=PhoneNumber.text.toString()
    visitcounter=1
    val user=User(uid,phonenumber,profileImageURL,visitcounter)
    ref.setValue(user)
      .addOnSuccessListener {
          Log.d("Upload","Data is uploaded")
      }
}

    fun uploadtosuspicious_user(){
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
lateinit var reference:StorageReference
fun uploadimage(){
    val filename=UUID.randomUUID().toString()
    reference=FirebaseStorage.getInstance().getReference("/images/$filename")
    val abc=Uri.parse(intent.getStringExtra("Photo"))
    //val uri=Uri.parse(intent.extras.getString("Photo"))
    reference.putFile(abc)
    uploadtouser()
}
class User(val uid:String,val phonenumber:String,val profileImageURL:String,val visitcounter:Int){}
var visitcounter:Int = 1
    var final:Int=1
    fun checkuser(){
    val uid=FirebaseAuth.getInstance().uid
    phonenumber=PhoneNumber.text.toString()
    val path= "/Visitors/$uid/phonenumber"
    val ref=FirebaseDatabase.getInstance().getReference("/Visitors/$phonenumber")
    ref.addListenerForSingleValueEvent(object :ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {
                   }
        override fun onDataChange(p0: DataSnapshot) {
            val binary=p0.exists()
//            val visit=p0.child("visitcounter").value.toString()
//            var visitint=visit.toInt()
            if(binary==true) {
                val visit=p0.child("visitcounter").value.toString()
                var visitint=visit.toInt()
                final = visitint + 1
                ref.child("visitcounter").setValue(visitint)
                val i = Intent(this@MainActivity, Main2Activity::class.java)
                i.putExtra("visitcount", final.toString())
                startActivity(i)
            }
            otpverification()
                Log.d("Binary", "This Worked")

        }
    })
}

    }