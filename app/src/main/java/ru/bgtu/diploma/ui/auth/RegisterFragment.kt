package ru.bgtu.diploma.ui.auth

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import dmax.dialog.SpotsDialog
import ru.bgtu.diploma.databinding.FragmentRegisterBinding
import ru.bgtu.diploma.models.data.User
import ru.bgtu.diploma.ui.MainActivity
import ru.bgtu.diploma.utils.FirestoreUtil

class RegisterFragment: Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        val user = User("-1", "Petr", "Petrov")

        binding.btnRegister.setOnClickListener {
            val dialog: AlertDialog = SpotsDialog.Builder()
                .setContext(context)
                .setMessage("Создание аккаунта...")
                .build().apply { show() }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword("test3@mail.ru", "12345678")
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        FirestoreUtil.initCurrentUserIfFirstTime(user){
                            dialog.dismiss()
                            startActivity(Intent(context, MainActivity::class.java))
                            activity?.finish()
                        }
                    }
                    else{
                        dialog.dismiss()
                        Toast.makeText(context, "Что-то пошло не так. Проверьте введенные данные.", Toast.LENGTH_LONG).show()
                    }
                }


        }

        return binding.root

    }
}