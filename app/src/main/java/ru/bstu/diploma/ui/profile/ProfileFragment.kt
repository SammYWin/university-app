package ru.bstu.diploma.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import ru.bstu.diploma.R
import ru.bstu.diploma.models.Profile
import ru.bstu.diploma.utils.Utils
import ru.bstu.diploma.viewmodels.ProfileViewModel
import ru.bstu.diploma.databinding.FragmentProfileBinding
import ru.bstu.diploma.glide.GlideApp
import ru.bstu.diploma.models.data.User
import ru.bstu.diploma.repositories.PreferencesRepository
import ru.bstu.diploma.ui.auth.AuthActivity
import ru.bstu.diploma.utils.StorageUtil
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    companion object {
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
        const val RC_SELECT_IMAGE = 2
    }

    private lateinit var viewModel: ProfileViewModel
    private var isEditMode = false
    lateinit var viewFields: Map<String, TextView>
    private lateinit var binding: FragmentProfileBinding
    private lateinit var selectedImageBytes: ByteArray
    private var avatarPath = ""
    private var isAvatarSet = false
    private var avatarChanged = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        binding.apply {
            binding.fab.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), RC_SELECT_IMAGE)
            }
        }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK
            && data != null && data.data != null){
            val selectedImagePath = data.data
            val selectedImageBmp = when {
                Build.VERSION.SDK_INT >= 29 -> {
                    val soure = ImageDecoder.createSource(requireActivity().contentResolver, selectedImagePath!!)
                    ImageDecoder.decodeBitmap(soure)
                }
                else -> {
                    MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImagePath)
                }
            }
            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()

            GlideApp.with(this)
                .load(selectedImageBytes)
                .placeholder(R.drawable.avatar_default)
                .into(binding.ivAvatar)

            avatarChanged = true
            if(!isAvatarSet) isAvatarSet = true

            if(::selectedImageBytes.isInitialized){
                StorageUtil.uploadProfileAvatar(selectedImageBytes){ imagePath ->
                    avatarPath = imagePath
                    saveProfileInfo()
                }
            }
        }
    }

    private fun handleLogout(){
        AlertDialog.Builder(requireContext())
            .setTitle("Выход")
            .setMessage("Вы уверены?")
            .setPositiveButton("Выйти") { dialog, which ->
                FirebaseAuth.getInstance().signOut()
                PreferencesRepository.clearProfilePreferences()
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

        avatarPath = profile.avatar
        if(avatarPath == "")
            updateDefaultAvatar(profile)
        else{
            //selectedImageBytes = profile.avatar.toByteArray()
            GlideApp.with(this)
                .load(StorageUtil.pathToReference(avatarPath))
                .placeholder(R.drawable.avatar_default)
                .into(binding.ivAvatar)
        }
    }

    private fun updateDefaultAvatar(profile: Profile) {
        Utils.toInitials(profile.firstName, profile.lastName)?.let {
            binding.ivAvatar.setInitials(it)
            binding.ivAvatar.setImageDrawable(null)
        } ?: binding.ivAvatar.setImageResource(R.drawable.avatar_default)

    }

    private fun saveProfileInfo() {
        binding.tvNickName.text = getString(R.string.profile_nickname, binding.etFirstName.text.trim(), binding.etLastName.text.trim())

        val profile = Profile(
            firstName = binding.etFirstName.text.trim().toString(),
            lastName = binding.etLastName.text.trim().toString(),
            nickName = binding.tvNickName.text.toString(),
            avatar = avatarPath,
            about = binding.etAbout.text.trim().toString(),
            group = binding.tvGroup.text.toString()
        )
        val user = User()
        with(user){
            firstName = binding.etFirstName.text.trim().toString()
            lastName = binding.etLastName.text.trim().toString()
            nickName = binding.tvNickName.text.toString()
            avatar = avatarPath
            about = binding.etAbout.text.trim().toString()
            group = binding.tvGroup.text.toString()
        }

        viewModel.saveProfileData(profile, user)
    }

}
