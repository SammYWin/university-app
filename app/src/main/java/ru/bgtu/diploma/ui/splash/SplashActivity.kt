package ru.bgtu.diploma.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import ru.bgtu.diploma.ui.MainActivity
import ru.bgtu.diploma.ui.auth.AuthActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(FirebaseAuth.getInstance().currentUser == null){
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
        else{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}