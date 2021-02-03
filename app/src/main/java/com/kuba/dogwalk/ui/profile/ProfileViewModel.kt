package com.kuba.dogwalk.ui.profile

import android.graphics.Bitmap
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuba.dogwalk.data.local.dog.Activity
import com.kuba.dogwalk.data.local.dog.Dog
import com.kuba.dogwalk.data.local.dog.Gender
import com.kuba.dogwalk.data.local.dog.Goal
import com.kuba.dogwalk.other.Constants.PROFILE_BIRTH_DATE_BIGGER_THAN_CURRENT_DATE_MESSAGE
import com.kuba.dogwalk.other.Constants.PROFILE_EMPTY_FIELD_MESSAGE
import com.kuba.dogwalk.other.Constants.PROFILE_SUCCESSFULLY_UPDATE
import com.kuba.dogwalk.other.Constants.PROFILE_TOO_LONG_TEXT_MESSAGE
import com.kuba.dogwalk.other.Constants.PROFILE_WEIGHT_EQUAL_TO_ZERO_MESSAGE
import com.kuba.dogwalk.other.Constants.PROFILE_WEIGHT_LESS_THAN_ZERO_MESSAGE
import com.kuba.dogwalk.other.Event
import com.kuba.dogwalk.other.Resource
import com.kuba.dogwalk.repositories.DogWalkRepository
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class ProfileViewModel @ViewModelInject constructor(
    private val repository: DogWalkRepository
) : ViewModel() {

    val dog = repository.observeDog()

    private val _updateDogStatus = MutableLiveData<Event<Resource<Dog>>>()
    val updateDogStatus: LiveData<Event<Resource<Dog>>> =
        _updateDogStatus

    fun deleteDog(dog: Dog) = viewModelScope.launch {
        repository.deleteDog(dog)
    }

    fun insertDog(dog: Dog) = viewModelScope.launch {
        repository.insertDog(dog)
    }

    fun updateDogName(name: String) = viewModelScope.launch {
        if (name.isEmpty()) {
            _updateDogStatus.postValue(
                Event(
                    Resource.error(
                        PROFILE_EMPTY_FIELD_MESSAGE,
                        null
                    )
                )
            )
            return@launch
        }

        if (name.length > 40) {
            _updateDogStatus.postValue(
                Event(
                    Resource.error(
                        PROFILE_TOO_LONG_TEXT_MESSAGE,
                        null
                    )
                )
            )
            return@launch
        }

        repository.updateDogName(name)
        _updateDogStatus.postValue(
            Event(
                Resource.success(
                    PROFILE_SUCCESSFULLY_UPDATE,
                    null
                )
            )
        )
    }

    fun updateDogBreed(breed: String) = viewModelScope.launch {
        if (breed.isEmpty()) {
            _updateDogStatus.postValue(
                Event(
                    Resource.error(
                        PROFILE_EMPTY_FIELD_MESSAGE,
                        null
                    )
                )
            )
            return@launch
        }

        if (breed.length > 40) {
            _updateDogStatus.postValue(
                Event(
                    Resource.error(
                        PROFILE_TOO_LONG_TEXT_MESSAGE,
                        null
                    )
                )
            )
            return@launch
        }

        repository.updateDogBreed(breed)
        _updateDogStatus.postValue(
            Event(
                Resource.success(
                    PROFILE_SUCCESSFULLY_UPDATE,
                    null
                )
            )
        )
    }

    fun updateDogWeight(weight: String) = viewModelScope.launch {
        if (weight.isEmpty()) {
            _updateDogStatus.postValue(
                Event(
                    Resource.error(
                        PROFILE_EMPTY_FIELD_MESSAGE,
                        null
                    )
                )
            )
            return@launch
        }
        if (weight.toDouble() == 0.0) {
            _updateDogStatus.postValue(
                Event(
                    Resource.error(
                        PROFILE_WEIGHT_EQUAL_TO_ZERO_MESSAGE,
                        null
                    )
                )
            )
            return@launch
        }

        if (weight.toDouble() < 0.0) {
            _updateDogStatus.postValue(
                Event(
                    Resource.error(
                        PROFILE_WEIGHT_LESS_THAN_ZERO_MESSAGE,
                        null
                    )
                )
            )
            return@launch
        }

        repository.updateDogWeight(weight.toDoubleOrNull() ?: 0.0)
        _updateDogStatus.postValue(
            Event(
                Resource.success(
                    PROFILE_SUCCESSFULLY_UPDATE,
                    null
                )
            )
        )
    }

    fun updateDogBirthDate(birthDate: Long) = viewModelScope.launch {
        if (birthDate > Calendar.getInstance().timeInMillis) {
            _updateDogStatus.postValue(
                Event(
                    Resource.error(
                        PROFILE_BIRTH_DATE_BIGGER_THAN_CURRENT_DATE_MESSAGE,
                        null
                    )
                )
            )
            return@launch
        }
        repository.updateDogBirthDate(birthDate)
        _updateDogStatus.postValue(
            Event(
                Resource.success(
                    PROFILE_SUCCESSFULLY_UPDATE,
                    null
                )
            )
        )
    }

    fun updateDogGender(gender: Gender) = viewModelScope.launch {
        repository.updateDogGender(gender)
        _updateDogStatus.postValue(
            Event(
                Resource.success(
                    PROFILE_SUCCESSFULLY_UPDATE,
                    null
                )
            )
        )
    }

    fun updateDogPhoto(photo: String) = viewModelScope.launch {
        repository.updateDogPhoto(photo)
        _updateDogStatus.postValue(
            Event(
                Resource.success(
                    PROFILE_SUCCESSFULLY_UPDATE,
                    null
                )
            )
        )
    }

    fun updateDogCaloriesGoal(goal: Goal) = viewModelScope.launch {
        repository.updateDogGaol(goal)
    }

    fun updateDogActivity(activity: Activity) = viewModelScope.launch {
        repository.updateDogActivity(activity)
        _updateDogStatus.postValue(
            Event(
                Resource.success(
                    PROFILE_SUCCESSFULLY_UPDATE,
                    null
                )
            )
        )
    }

    fun updateDogCalories(calories: Int) = viewModelScope.launch {
        repository.updateDogCalories(calories)
    }

}