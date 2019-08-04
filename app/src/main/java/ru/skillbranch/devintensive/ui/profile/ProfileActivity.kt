package ru.skillbranch.devintensive.ui.profile

import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.hideKeyboard
import ru.skillbranch.devintensive.models.Bender
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.viewmodels.ProfileViewModel
import java.lang.IllegalStateException

class ProfileActivity : AppCompatActivity()
{
    companion object
    {
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
    }

    private lateinit var viewModel: ProfileViewModel
    var isEditMode = false
    lateinit var viewFields: Map<String, TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO set custom Theme this before super and SetContentView

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews(savedInstanceState)
        initViewModel()
        Log.d("M_MainActivity", "onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.d("M_MainActivity", "onStart")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("M_MainActivity", "onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("M_MainActivity", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("M_MainActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("M_MainActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("M_MainActivity", "onDestroy")
    }


    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_EDIT_MODE, isEditMode)
    }

    private fun initViewModel()
    {
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.getProfileData().observe(this, Observer { updateUI(it) })
        viewModel.getTheme().observe(this, Observer { updateTheme(it) })
    }

    private fun updateTheme(mode: Int) {
        delegate.setLocalNightMode(mode)
        Log.d("M_ProfileActivity", "updatedTheme")
    }

    private fun updateUI(profile: Profile)
    {
        profile.toMap().also{
            for((k,v) in viewFields)
            {
                v.text = it[k].toString()
            }
        }
    }

    private fun initViews(savedInstanceState: Bundle?)
    {
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
            if(isEditMode) saveProfileInfo()
            isEditMode = !isEditMode
            showCurrentMode(isEditMode)
        }

        btn_switch_theme.setOnClickListener{
            viewModel.switchTheme()
        }
    }

    private fun showCurrentMode(isEdit: Boolean)
    {
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
            val filter: ColorFilter? = if(isEdit)
            {
                PorterDuffColorFilter(resources.getColor(R.color.color_accent, theme), PorterDuff.Mode.SRC_IN)
            } else null

            val icon = if(isEdit)
            {
                resources.getDrawable(R.drawable.ic_save_black_24dp, theme)
            }
            else
            {
                resources.getDrawable(R.drawable.ic_edit_black_24dp, theme)
            }

            background.colorFilter = filter
            setImageDrawable(icon)
        }

    }

    private fun saveProfileInfo()
    {
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