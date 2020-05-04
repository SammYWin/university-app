package ru.bstu.diploma.ui.profile

import android.content.DialogInterface
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*
import ru.bstu.diploma.R
import ru.bstu.diploma.models.Profile
import ru.bstu.diploma.utils.Utils
import ru.bstu.diploma.utils.Utils.convertSpToPx
import ru.bstu.diploma.viewmodels.ProfileViewModel
import ru.bstu.diploma.databinding.FragmentProfileBinding
import ru.bstu.diploma.models.data.User
import ru.bstu.diploma.repositories.PreferencesRepository
import ru.bstu.diploma.ui.auth.AuthActivity
import ru.bstu.diploma.utils.FirestoreUtil

class ProfileFragment : Fragment() {

    companion object {
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
    }

    private lateinit var viewModel: ProfileViewModel
    var isEditMode = false
    var isAvatarSet = true
    lateinit var viewFields: Map<String, TextView>
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(savedInstanceState)
        initViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_EDIT_MODE, isEditMode)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_logout, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_logout){
            handleLogout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleLogout(){
        AlertDialog.Builder(requireContext())
            .setTitle("Выход")
            .setMessage("Вы уверены?")
            .setPositiveButton("Выйти") { dialog, which ->
                FirebaseAuth.getInstance().signOut()
                PreferencesRepository.clearPreferences()
                startActivity(Intent(context, AuthActivity::class.java))
                activity?.finish()
            }
            .setNegativeButton("Отмена") { dialog, which ->
                dialog.cancel()
            }
            .create().show()
    }

    private fun initViews(savedInstanceState: Bundle?) {
        viewFields = mapOf(
            "firstName" to binding.etFirstName,
            "lastName" to binding.etLastName,
            "nickName" to binding.tvNickName,
            "about" to binding.etAbout,
            "group" to binding.tvGroup
        )

        isEditMode = savedInstanceState?.getBoolean(IS_EDIT_MODE,false) ?: false
        showCurrentMode(isEditMode)

        binding.btnEdit.setOnClickListener{
            if(isEditMode)
                saveProfileInfo()
            isEditMode = !isEditMode
            showCurrentMode(isEditMode)
        }

        binding.btnSwitchTheme.setOnClickListener{
            viewModel.switchTheme()
        }
    }

    private fun showCurrentMode(isEdit: Boolean) {
        val info = viewFields.filter { setOf("firstName","lastName","about").contains(it.key) }
        for((_,v) in info)
        {
            v as EditText
            v.isFocusable = isEdit
            v.isFocusableInTouchMode = isEdit
            v.isEnabled = isEdit
            v.background.alpha = if(isEdit) 255 else 0
        }

        binding.wrAbout.isCounterEnabled = isEdit

        with(binding.btnEdit)
        {
            val color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                resources.getColor(R.color.color_accent, requireNotNull(activity).theme)
            }else{
                resources.getColor(R.color.color_accent)
            }

            val filter: ColorFilter? = if(isEdit) {
                PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN
                )
            } else null

            val icon = if(isEdit) {
                resources.getDrawable(R.drawable.ic_save_black_24dp, requireNotNull(activity).theme)
            }
            else {
                resources.getDrawable(R.drawable.ic_edit_black_24dp, requireNotNull(activity).theme)
            }

            background.colorFilter = filter
            setImageDrawable(icon)
        }

    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        viewModel.getProfileData().observe(viewLifecycleOwner, Observer { updateUI(it) })
        viewModel.getTheme().observe(viewLifecycleOwner, Observer { updateTheme(it) })
    }


    private fun updateTheme(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
        Log.d("M_ProfileActivity", "updatedTheme")
    }

    private fun updateUI(profile: Profile) {
        profile.toMap().also{
            for((k,v) in viewFields)
            {
                v.text = it[k].toString()
            }
        }
        if(!isAvatarSet)
            updateDefaultAvatar(profile)
    }

    private fun updateDefaultAvatar(profile: Profile) {
        Utils.toInitials(profile.firstName, profile.lastName)?.let {
            val avatar = getTextAvatar(it)
            binding.ivAvatar.setImageBitmap(avatar)
        } ?: binding.ivAvatar.setImageResource(R.drawable.avatar_default)

    }

    private fun getTextAvatar(text: String): Bitmap{
        val color = TypedValue()
        requireNotNull(activity).theme.resolveAttribute(R.attr.colorAccent, color,true)

        val bitmap: Bitmap = Bitmap.createBitmap(ic_avatar.layoutParams.width,ic_avatar.layoutParams.height, Bitmap.Config.ARGB_8888)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = convertSpToPx(16)
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER

        val canvas = Canvas(bitmap)
        canvas.drawColor(color.data)

        val textBounds = Rect()
        paint.getTextBounds(text,0,text.length,textBounds)

        canvas.drawText(text,binding.icAvatar.layoutParams.width/2f,
            binding.icAvatar.layoutParams.height/2f + textBounds.height()/2f, paint)


        return bitmap
    }

    private fun saveProfileInfo() {
        binding.tvNickName.text = "${binding.etFirstName.text} ${binding.etLastName.text.toString()}"
        val profile = Profile(
            firstName = binding.etFirstName.text.toString(),
            lastName = binding.etLastName.text.toString(),
            nickName = binding.tvNickName.text.toString(),
            about = binding.etAbout.text.toString(),
            group = binding.tvGroup.text.toString()
        )
        val user = User()
        with(user){
            firstName = binding.etFirstName.text.toString()
            lastName = binding.etLastName.text.toString()
            nickName = binding.tvNickName.text.toString()
            about = binding.etAbout.text.toString()
            group = binding.tvGroup.text.toString()
        }

        viewModel.saveProfileData(profile, user)
    }



}
