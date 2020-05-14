package ru.bstu.diploma.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import ru.bstu.diploma.ui.main.MainActivity
import ru.bstu.diploma.ui.auth.AuthActivity

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