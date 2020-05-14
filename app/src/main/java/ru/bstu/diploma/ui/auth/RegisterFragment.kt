package ru.bstu.diploma.ui.auth

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import dmax.dialog.SpotsDialog
import ru.bstu.diploma.R
import ru.bstu.diploma.databinding.FragmentRegisterBinding
import ru.bstu.diploma.models.data.User
import ru.bstu.diploma.ui.main.MainActivity
import ru.bstu.diploma.utils.FirestoreUtil

class RegisterFragment: Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentRegisterBinding

    private val user = User("", "", "")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.groups,
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = spinnerAdapter
        binding.spinner.onItemSelectedListener = this

        binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.spinner.isEnabled = !isChecked
            user.isProfessor = isChecked
        }

        binding.btnRegister.setOnClickListener {
            handleRegistration(
                binding.etRegisterFirstName.text.toString().trim(),
                binding.etRegisterLastName.text.toString().trim(),
                binding.etRegisterEmail.text.toString().trim(),
                binding.etRegisterPassword.text.toString().trim()
            )

        }

        binding.etRegisterFirstName.doOnTextChanged { charSequence: CharSequence?, i: Int, i1: Int, i2: Int ->
            binding.wrRegisterFirstName.error = null
        }
        binding.etRegisterLastName.doOnTextChanged { charSequence: CharSequence?, i: Int, i1: Int, i2: Int ->
            binding.wrRegisterLastName.error = null
        }
        binding.etRegisterEmail.doOnTextChanged { charSequence: CharSequence?, i: Int, i1: Int, i2: Int ->
            binding.wrRegisterEmail.error = null
        }
        binding.etRegisterPassword.doOnTextChanged { charSequence: CharSequence?, i: Int, i1: Int, i2: Int ->
            binding.wrRegisterPassword.error = null
        }

        return binding.root
    }

    private fun handleRegistration(firstName: String, lastName: String, email: String, password: String) {
        if(TextUtils.isEmpty(firstName)){
            binding.wrRegisterFirstName.error = getString(R.string.error_first_name_empty)
            return
        }else binding.wrRegisterFirstName.error = null

        if(TextUtils.isEmpty(lastName)){
            binding.wrRegisterLastName.error = getString(R.string.error_last_name_empty)
            return
        }else binding.wrRegisterLastName.error = null

        if(user.group == null || binding.spinner.selectedItem == getString(R.string.spinner_groups_hint)){
            (binding.spinner.selectedView as TextView).error = ""
            return
        }

        if(TextUtils.isEmpty(email)){
            binding.wrRegisterEmail.error = getString(R.string.error_email_empty)
            return
        }else binding.wrRegisterEmail.error = null

        if(TextUtils.isEmpty(password)){
            binding.wrRegisterPassword.error = getString(R.string.error_password_empty)
            return
        }else binding.wrRegisterPassword.error = null

        with(user){
            this.firstName = firstName
            this.lastName = lastName
            nickName = "$firstName $lastName"
            about = ""
            avatar = ""
        }

        val dialog: AlertDialog = SpotsDialog.Builder()
            .setContext(context)
            .setMessage(getString(R.string.alert_account_creation))
            .build().apply { show() }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    FirestoreUtil.initCurrentUserIfFirstTime(user){
                        dialog.dismiss()
                        startActivity(Intent(context, MainActivity::class.java))
                        activity?.finish()
                    }
                }
                else {
                    dialog.dismiss()
                    Toast.makeText(context, getString(R.string.error_register_or_login), Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedGroup = parent?.getItemAtPosition(position).toString()
        user.group = selectedGroup
    }


}