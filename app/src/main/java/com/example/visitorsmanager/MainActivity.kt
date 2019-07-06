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
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var display:ImageView
    var storedVerificationId:String?=null
    var counter:Long?=null
    lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var string: String
    var verificationId= ""
//    var resendtoken:MediaSession.Token?=null
    private val auth=FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        //count()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //button?.alpha=0f
        //querydatabase()
        //count()
        button2.setOnClickListener {
            startActivity(Intent(this,PhotoActivity::class.java))
        }
        submit.setOnClickListener {
            authenticate()
            //otpverification()
            //verifysignin()



            //activity()
        }
        send_otp.setOnClickListener {
            otpverification()
            imagedisplay()

        }

        }
    fun activity(){
        val intent = Intent(this@MainActivity, Main2Activity::class.java)
        startActivity(intent)
    }



    private fun otpverification(){
        verifyFunction()
    val phonenumber=PhoneNumber.text.toString()

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
    private fun verifysignin(){
        val otp=OTP.text.toString()
        val credential=PhoneAuthProvider.getCredential(storedVerificationId!!,otp)
        callbacks.onVerificationCompleted(credential)
     }
    private fun verifyFunction() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                Log.d("Phone number", "onVerificationCompleted:$credential")
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
                    uploadtouser()
                    Toast.makeText(this@MainActivity, "Enter valid details", Toast.LENGTH_SHORT).show()
                    //val intent=Intent(this@MainActivity,Main2Activity::class.java)
                    //startActivity(intent)
                    count()
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
fun uploadtouser(){
    uploadimage()
    val uid=FirebaseAuth.getInstance().uid?:""
    val ref=FirebaseDatabase.getInstance().getReference("/Visitors/$uid/")
    val phonenumber=PhoneNumber.text.toString()
  ref.setValue(phonenumber)
      .addOnSuccessListener {
          Toast.makeText(this,"Sucess",Toast.LENGTH_SHORT).show()
      }
}
    fun fetchdata() {
        val ref = FirebaseDatabase.getInstance().getReference("/Visitors/")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Fail", "Failure has occured")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val value = p0.getValue(String::class.java)
                Log.d("Value", "Values is $value")
            }
        })
    }
    fun uploadtosuspicious_user(){
        val uid=FirebaseAuth.getInstance().uid?:""
        val ref=FirebaseDatabase.getInstance().getReference("/suspicious_user/$uid")
        val phonenumber=PhoneNumber.text.toString()
        ref.setValue(phonenumber)
            .addOnSuccessListener {
                Toast.makeText(this,"Sucess",Toast.LENGTH_SHORT).show()
            }
    }
fun count(){
    val ref=FirebaseDatabase.getInstance().getReference()


    ref.addListenerForSingleValueEvent(object :ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(p0: DataSnapshot) {
            for (snap in p0.children) {
                counter=snap.childrenCount
                Log.d("Count ${snap.key}", "Number of users ($counter)")
                val intent=Intent(this@MainActivity,Main2Activity::class.java)
                string=counter.toString()
                Log.d("Prateek1","Value of counter $string")
                intent.putExtra("Numbers",string)
                startActivity(intent)
            }
        }

    })
}
fun querydatabase(){
    val phonenumber=PhoneNumber.text.toString()
    val uid=FirebaseAuth.getInstance().uid?:""
    val ref=FirebaseDatabase.getInstance().getReference("/Visitors")
    val query=ref.orderByChild("$uid")
    if(query.equals(phonenumber)){
        Log.d("Prateek","The user exists")
    }
}

fun uploadimage(){
    val filename=UUID.randomUUID().toString()
    val ref=FirebaseStorage.getInstance().getReference("/images/$filename")
    val uri=Uri.parse(intent.extras.getString("Photo"))
    ref.putFile(uri)
}

    }