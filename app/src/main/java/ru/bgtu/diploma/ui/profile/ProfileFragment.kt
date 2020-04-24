package ru.bgtu.diploma.ui.profile

import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_profile.*
import ru.bgtu.diploma.R
import ru.bgtu.diploma.models.Profile
import ru.bgtu.diploma.utils.Utils
import ru.bgtu.diploma.utils.Utils.convertSpToPx
import ru.bgtu.diploma.viewmodels.ProfileViewModel

class ProfileFragment : Fragment() {

    companion object {
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
    }

    private lateinit var viewModel: ProfileViewModel
    var isEditMode = false
    var isAvatarSet = true
    lateinit var viewFields: Map<String, TextView>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.setTheme(R.style.AppTheme)
        val v: View = inflater.inflate(R.layout.fragment_profile, container, false)
        return v
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

    private fun initViews(savedInstanceState: Bundle?) {
        viewFields = mapOf(
            "nickName" to tv_nick_name,
            "rank" to tv_rank,
            "rating" to tv_rating,
            "respect" to tv_respect,
            "firstName" to et_first_name,
            "lastName" to et_last_name,
            "about" to et_about,
            "repository" to et_repository
        )

        isEditMode = savedInstanceState?.getBoolean(IS_EDIT_MODE,false) ?: false
        showCurrentMode(isEditMode)

        btn_edit.setOnClickListener{
            viewModel.onRepoEditCompleted(wr_repository.isErrorEnabled)

            if(isEditMode) saveProfileInfo()
            isEditMode = !isEditMode
            showCurrentMode(isEditMode)

        }

        btn_switch_theme.setOnClickListener{
            viewModel.switchTheme()
        }

        et_repository.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?)
            {
                viewModel.onRepositoryChanged(p0.toString())
            }
        })
    }

    private fun showCurrentMode(isEdit: Boolean) {
        val info = viewFields.filter { setOf("firstName","lastName","about","repository").contains(it.key) }
        for((_,v) in info)
        {
            v as EditText
            v.isFocusable = isEdit
            v.isFocusableInTouchMode = isEdit
            v.isEnabled = isEdit
            v.background.alpha = if(isEdit) 255 else 0
        }

        wr_about.isCounterEnabled = isEdit
        ic_eye.visibility = if(isEdit) View.GONE else View.VISIBLE

        with(btn_edit)
        {
            val filter: ColorFilter? = if(isEdit) {
                PorterDuffColorFilter(resources.getColor(R.color.color_accent, requireNotNull(activity).theme), PorterDuff.Mode.SRC_IN)
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
        viewModel.getRepositoryError().observe(viewLifecycleOwner, Observer { updateRepoError(it) })
        viewModel.getIsRepoError().observe(viewLifecycleOwner, Observer { updateRepository(it) })
    }

    private fun updateRepository(isError: Boolean) {
        if (isError) et_repository.text.clear()
    }

    private fun updateRepoError(isError: Boolean) {
        wr_repository.isErrorEnabled = isError
        wr_repository.error = if (isError) "Невалидный адрес репозитория" else null
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
            iv_avatar.setImageBitmap(avatar)
        } ?: iv_avatar.setImageResource(R.drawable.avatar_default)

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

        canvas.drawText(text,ic_avatar.layoutParams.width/2f,
            ic_avatar.layoutParams.height/2f + textBounds.height()/2f, paint)


        return bitmap
    }

    private fun saveProfileInfo() {
        Profile(
            firstName = et_first_name.text.toString(),
            lastName = et_last_name.text.toString(),
            about = et_about.text.toString(),
            repository = et_repository.text.toString()
        ).apply {
            viewModel.saveProfileData(this)
        }
    }



}
