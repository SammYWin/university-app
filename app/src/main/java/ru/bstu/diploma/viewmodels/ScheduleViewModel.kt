package ru.bstu.diploma.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.bstu.diploma.network.ScheduleApi
import ru.bstu.diploma.repositories.PreferencesRepository

class ScheduleViewModel: ViewModel() {

    private val scheduleData = MutableLiveData<String>()
    fun getScheduleData(): LiveData<String> = scheduleData

    fun loadSchedule(namedata: String, period: String, group: String, teacher: String){
        viewModelScope.launch {
            val getScheduleDeferred = ScheduleApi.retrofitService
                .getSchedule(namedata, period, group, teacher)
            try {
                val schedule = getScheduleDeferred.await()
                val data = schedule.body()!!.string()
                scheduleData.value = data
                PreferencesRepository.saveSchedule(data)

            }catch (t: Throwable){
                scheduleData.value = "Ошибка"
            }
        }
    }
}