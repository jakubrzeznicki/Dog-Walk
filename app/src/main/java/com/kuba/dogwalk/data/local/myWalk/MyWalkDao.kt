package com.kuba.dogwalk.data.local.myWalk

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MyWalkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(myWalk: MyWalk)

    @Delete
    suspend fun delete(myWalk: MyWalk)

    @Query("SELECT * FROM my_walk_item ORDER BY timestamp DESC")
    fun observeAllMyWalkItem(): LiveData<List<MyWalk>>

    @Query("SELECT * FROM my_walk_item WHERE id=:id")
    suspend fun observeMyWalkItem(id: Int): MyWalk

}