package com.kuba.dogwalk.ui.mywalk

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuba.dogwalk.data.local.myWalk.MyWalk
import com.kuba.dogwalk.other.Constants
import com.kuba.dogwalk.other.Constants.MY_WALK_ITEM_DELETED_SUCCESSFULLY_MESSAGE
import com.kuba.dogwalk.other.Constants.WALK_SAVE_SUCCESSFULLY_MESSAGE
import com.kuba.dogwalk.other.Event
import com.kuba.dogwalk.other.Resource
import com.kuba.dogwalk.repositories.DogWalkRepository
import kotlinx.coroutines.launch

class MyWalkViewModel @ViewModelInject constructor(
    private val repository: DogWalkRepository
) : ViewModel() {

    val myWalkItems = repository.observeAllMyWalkItem()

    private val _myWalkItem = MutableLiveData<MyWalk>()
    val myWalkItem: LiveData<MyWalk> = _myWalkItem

    private val _insertOrDeleteMyWalkStatus = MutableLiveData<Event<Resource<MyWalk>>>()
    val insertOrDeleteMyWalkStatus: LiveData<Event<Resource<MyWalk>>> =
        _insertOrDeleteMyWalkStatus


    fun deleteMyWalkItem(myWalk: MyWalk) = viewModelScope.launch {
        repository.deleteMyWalk(myWalk)
        _insertOrDeleteMyWalkStatus.postValue(
            Event(
                Resource.success(
                    MY_WALK_ITEM_DELETED_SUCCESSFULLY_MESSAGE,
                    myWalk
                )
            )
        )
    }

    fun insertMyWalkItemIntoDb(myWalk: MyWalk) = viewModelScope.launch {
        repository.insertMyWalk(myWalk)
        _insertOrDeleteMyWalkStatus.postValue(
            Event(
                Resource.success(
                    WALK_SAVE_SUCCESSFULLY_MESSAGE,
                    myWalk
                )
            )
        )
    }

    fun getMyWalkItem(id: Int) = viewModelScope.launch {
        _myWalkItem.postValue(repository.observeMyWalkItem(id))
    }
}