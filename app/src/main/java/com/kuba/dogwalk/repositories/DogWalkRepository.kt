package com.kuba.dogwalk.repositories

import androidx.lifecycle.LiveData
import com.kuba.dogwalk.data.local.dog.Activity
import com.kuba.dogwalk.data.local.dog.Dog
import com.kuba.dogwalk.data.local.dog.Gender
import com.kuba.dogwalk.data.local.dog.Goal
import com.kuba.dogwalk.data.local.myWalk.MyWalk
import com.kuba.dogwalk.data.local.notification.Notification

interface DogWalkRepository {
    suspend fun insertMyWalk(myWalk: MyWalk)
    suspend fun deleteMyWalk(myWalk: MyWalk)
    fun observeAllMyWalkItem(): LiveData<List<MyWalk>>
    suspend fun observeMyWalkItem(id: Int): MyWalk

    suspend fun insertNotification(notification: Notification)
    suspend fun deleteNotification(notification: Notification)
    fun observeCustomNotifications(custom: Boolean): LiveData<List<Notification>>
    fun observeWalkNotifications(custom: Boolean): LiveData<Notification>
    suspend fun updateWalkNotificationEnable(enable: Boolean, id: Int)
    suspend fun updateCustomNotificationEnable(enable: Boolean, id: Int)

    suspend fun insertDog(dog: Dog)
    suspend fun deleteDog(dog: Dog)
    fun observeDog(): LiveData<Dog>
    suspend fun updateDogName(name: String)
    suspend fun updateDogBreed(breed: String)
    suspend fun updateDogWeight(weight: Double)
    suspend fun updateDogGender(gender: Gender)
    suspend fun updateDogPhoto(photo: String)
    suspend fun updateDogBirthDate(birthDate: Long)
    suspend fun updateDogGaol(goal: Goal)
    suspend fun updateDogActivity(activity: Activity)
    suspend fun updateDogCalories(calories: Int)
}