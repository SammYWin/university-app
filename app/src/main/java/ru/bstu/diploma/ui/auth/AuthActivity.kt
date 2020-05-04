package ru.bstu.diploma.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import ru.bstu.diploma.R
import ru.bstu.diploma.databinding.ActivityAuthBinding
import ru.bstu.diploma.ui.MainActivity
import ru.bstu.diploma.ui.adapters.AuthFragmentPagerAdapter

class AuthActivity : AppCompatActivity() {

    companion object {

        private const val RC_SIGN_IN = 1
    }

    private lateinit var binding: ActivityAuthBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)
        setSupportActionBar(binding.toolbar)

        binding.viewPager.adapter = AuthFragmentPagerAdapter(supportFragmentManager, lifecycle)

        TabLayoutMediator(binding.tabLayout, binding.viewPager){ tab, pos ->
            tab.text = when(pos) {
                0 -> getString(R.string.auth_login)
                1 -> getString(R.string.auth_register)
                else -> "???"
            }
       }.attach()
    }

    private fun createSignInIntent(){
        val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.AppTheme)
                .build()
            , RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){

            val response = IdpResponse.fromResultIntent(data)

            if(resultCode == Activity.RESULT_OK){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            else if(resultCode == Activity.RESULT_CANCELED){
                if(response == null) return

                when(response.error?.errorCode){
                    ErrorCodes.NO_NETWORK ->
                        Snackbar.make(binding.root, "Нет сети", Snackbar.LENGTH_LONG).show()
                    ErrorCodes.UNKNOWN_ERROR ->
                        Snackbar.make(binding.root, "Неизвестная ошибка", Snackbar.LENGTH_LONG).show()
                }
            }


        }
    }
}