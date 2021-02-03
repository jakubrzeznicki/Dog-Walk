package com.kuba.dogwalk.repositories

import androidx.lifecycle.LiveData
import com.kuba.dogwalk.data.local.dog.*
import com.kuba.dogwalk.data.local.myWalk.MyWalk
import com.kuba.dogwalk.data.local.myWalk.MyWalkDao
import com.kuba.dogwalk.data.local.notification.Notification
import com.kuba.dogwalk.data.local.notification.NotificationDao
import javax.inject.Inject

class DefaultDogWalkRepository @Inject constructor(
    private val myWalkDao: MyWalkDao,
    private val notificationDao: NotificationDao,
    private val dogDao: DogDao
) : DogWalkRepository {
    override suspend fun insertMyWalk(myWalk: MyWalk) {
        myWalkDao.insert(myWalk)
    }

    override suspend fun deleteMyWalk(myWalk: MyWalk) {
        myWalkDao.delete(myWalk)
    }

    override fun observeAllMyWalkItem(): LiveData<List<MyWalk>> = myWalkDao.observeAllMyWalkItem()
    override suspend fun observeMyWalkItem(id: Int): MyWalk {
        return myWalkDao.observeMyWalkItem(id)
    }

    override suspend fun insertNotification(notification: Notification) {
        notificationDao.insert(notification)
    }

    override suspend fun deleteNotification(notification: Notification) {
        notificationDao.delete(notification)
    }


    override fun observeCustomNotifications(custom: Boolean): LiveData<List<Notification>> =
        notificationDao.observeCustomNotifications(custom)

    override fun observeWalkNotifications(custom: Boolean): LiveData<Notification> =
        notificationDao.observeWalkNotifications(custom)

    override suspend fun updateWalkNotificationEnable(enable: Boolean, id: Int) {
        notificationDao.updateWalkNotificationEnable(enable, id)
    }

    override suspend fun updateCustomNotificationEnable(enable: Boolean, id: Int) {
        notificationDao.updateCustomNotificationEnable(enable, id)
    }

    override suspend fun insertDog(dog: Dog) {
        dogDao.insert(dog)
    }

    override suspend fun deleteDog(dog: Dog) {
        dogDao.delete(dog)
    }

    override fun observeDog(): LiveData<Dog> =
        dogDao.observeDog()

    override suspend fun updateDogName(name: String) {
        dogDao.updateName(name)
    }

    override suspend fun updateDogBreed(breed: String) {
        dogDao.updateBreed(breed)
    }

    override suspend fun updateDogWeight(weight: Double) {
        dogDao.updateWeight(weight)
    }

    override suspend fun updateDogGender(gender: Gender) {
        dogDao.updateGender(gender)
    }

    override suspend fun updateDogPhoto(photo: String) {
        dogDao.updatePhoto(photo)
    }

    override suspend fun updateDogBirthDate(birthDate: Long) {
        dogDao.updateBirthDate(birthDate)
    }

    override suspend fun updateDogGaol(goal: Goal) {
        dogDao.updateDogGoal(goal)
    }

    override suspend fun updateDogActivity(activity: Activity) {
        dogDao.updateActivity(activity)
    }

    override suspend fun updateDogCalories(calories: Int) {
        dogDao.updateCalories(calories)
    }

}

