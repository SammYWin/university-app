package ru.bstu.diploma.ui.auth

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import dmax.dialog.SpotsDialog
import ru.bstu.diploma.R
import ru.bstu.diploma.databinding.FragmentLoginBinding
import ru.bstu.diploma.ui.MainActivity


class LoginFragment: Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.etLoginEmail.doOnTextChanged { charSequence: CharSequence?, i: Int, i1: Int, i2: Int ->
            binding.wrLoginEmail.error = null
        }
        binding.etLoginPassword.doOnTextChanged { charSequence: CharSequence?, i: Int, i1: Int, i2: Int ->
            binding.wrLoginPassword.error = null
        }

        binding.btnLogin.setOnClickListener {
            handleLogin(binding.etLoginEmail.text.toString().trim(), binding.etLoginPassword.text.toString().trim())
        }

        return binding.root
    }

    private fun handleLogin(email: String, password: String){
        if(TextUtils.isEmpty(email)){
            binding.wrLoginEmail.error = getString(R.string.error_email_empty)
            return
        }else binding.wrLoginEmail.error = null

        if(TextUtils.isEmpty(password)){
            binding.wrLoginPassword.error = getString(R.string.error_password_empty)
            return
        }else binding.wrLoginPassword.error = null

        val dialog: AlertDialog = SpotsDialog.Builder()
            .setContext(context)
            .setMessage(getString(R.string.alert_login_process))
            .build().apply { show() }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {task ->
                if(task.isSuccessful) {
                    dialog.dismiss()
                    startActivity(Intent(context, MainActivity::class.java))
                    activity?.finish()
                }else {
                    dialog.dismiss()
                    Toast.makeText(context, getString(R.string.error_register_or_login), Toast.LENGTH_LONG).show()
                }
            }
    }
}