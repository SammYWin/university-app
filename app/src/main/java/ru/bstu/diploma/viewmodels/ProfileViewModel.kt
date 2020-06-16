package ru.bstu.diploma.viewmodels

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.bstu.diploma.models.Profile
import ru.bstu.diploma.models.data.User
import ru.bstu.diploma.repositories.PreferencesRepository
import ru.bstu.diploma.utils.FirestoreUtil

class ProfileViewModel : ViewModel()
{
    private val repository : PreferencesRepository = PreferencesRepository
    private val profileData = MutableLiveData<Profile>()
    private val appTheme = MutableLiveData<Int>()
    var currentAvatar: Drawable? = null

    init {
        Log.d("M_ProfileViewModel", "viewModel Init")
        profileData.value = repository.getProfile()
        appTheme.value = repository.getAppTheme()
        FirestoreUtil.getCurrentUser { user ->
            val profile = Profile(
                user.firstName!!,
                user.lastName!!,
                user.nickName!!,
                user.avatar!!,
                user.about!!,
                user.group!!,
                user.isGroupLeader,
                user.isProfessor
            )
            if(!profileData.value!!.equals(profile)) {
                profileData.value = profile
            }
            repository.saveProfile(profile)
        }
    }

    fun getProfileData() : LiveData<Profile> = profileData

    fun getTheme(): LiveData<Int> = appTheme

    fun saveProfileData(profile : Profile, user: User)
    {
        if(!repository.getProfile().equals(profile)){
            repository.saveProfile(profile)
            profileData.value = profile
        }
        FirestoreUtil.updateCurrentUser(user)
    }

    fun switchTheme()
    {
        if(appTheme.value == AppCompatDelegate.MODE_NIGHT_YES){
            appTheme.value = AppCompatDelegate.MODE_NIGHT_NO
        }else{
            appTheme.value = AppCompatDelegate.MODE_NIGHT_YES
        }
        repository.saveAppTheme(appTheme.value!!)
    }
}