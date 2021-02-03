package com.kuba.dogwalk.data.local.dog

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dog: Dog)

    @Delete
    suspend fun delete(dog: Dog)

    @Transaction
    @Query("SELECT * FROM dog LIMIT 1")
    fun observeDog(): LiveData<Dog>

    @Query("UPDATE dog SET name=:name WHERE id=0")
    suspend fun updateName(name: String)

    @Query("UPDATE dog SET breed=:breed WHERE id=0")
    suspend fun updateBreed(breed: String)

    @Query("UPDATE dog SET weight=:weight WHERE id=0")
    suspend fun updateWeight(weight: Double)

    @Query("UPDATE dog SET gender=:gender WHERE id=0")
    suspend fun updateGender(gender: Gender)

    @Query("UPDATE dog SET birthDate=:birthDate WHERE id=0")
    suspend fun updateBirthDate(birthDate: Long)

    @Query("UPDATE dog SET goal=:goal WHERE id=0")
    suspend fun updateDogGoal(goal: Goal)

    @Query("UPDATE dog SET activity=:activity WHERE id=0")
    suspend fun updateActivity(activity: Activity)

    @Query("UPDATE dog SET calories=:calories WHERE id=0")
    suspend fun updateCalories(calories: Int)

    @Query("UPDATE dog SET photo=:photo WHERE id=0")
    suspend fun updatePhoto(photo: String)
}