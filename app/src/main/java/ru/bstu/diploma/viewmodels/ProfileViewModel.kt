package ru.bstu.diploma.viewmodels

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

    init {
        Log.d("M_ProfileViewModel", "init view model")
        profileData.value = repository.getProfile()
        appTheme.value = repository.getAppTheme()
        FirestoreUtil.getCurrentUser { user ->
            val profile = Profile(
                user.firstName!!,
                user.lastName!!,
                user.nickName!!,
                user.about!!,
                user.group!!
            )
            profileData.value = profile
            repository.saveProfile(profile)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("M_ProfileViewModel", "view model cleared")
    }

    fun getProfileData() : LiveData<Profile> = profileData

    fun getTheme(): LiveData<Int> = appTheme

    fun saveProfileData(profile : Profile, user: User)
    {
        repository.saveProfile(profile)
        FirestoreUtil.updateCurrentUser(user)
        profileData.value = profile
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