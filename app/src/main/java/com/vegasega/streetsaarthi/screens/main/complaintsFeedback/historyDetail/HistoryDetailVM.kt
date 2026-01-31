package com.vegasega.streetsaarthi.screens.main.complaintsFeedback.historyDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vegasega.streetsaarthi.networking.ApiInterface
import com.vegasega.streetsaarthi.networking.CallHandler
import com.vegasega.streetsaarthi.networking.Repository
import com.vegasega.streetsaarthi.models.BaseResponseDC
import com.vegasega.streetsaarthi.models.ItemChat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HistoryDetailVM @Inject constructor(private val repository: Repository): ViewModel() {
    val adapter by lazy { HistoryDetailAdapter() }

    var uploadMediaImage : String ?= null

    private var feedbackConversationLiveData = MutableLiveData<ItemChat>()
    val feedbackConversationLive : LiveData<ItemChat> get() = feedbackConversationLiveData
    fun feedbackConversationDetails(_id: String, page: String) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<ItemChat>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.feedbackConversationDetails(_id, page)
                override fun success(response: Response<ItemChat>) {
                    if (response.isSuccessful){
                        if(response.body()!!.data != null){
                            feedbackConversationLiveData.value = response.body()
                        }
                    }
                }
                override fun error(message: String) {
                    super.error(message)
                }
                override fun loading() {
                    super.loading()
                }
            }
        )
    }





    private var feedbackConversationLiveDataSecond = MutableLiveData<ItemChat>()
    val feedbackConversationLiveSecond : LiveData<ItemChat> get() = feedbackConversationLiveDataSecond
    fun feedbackConversationDetailsSecond( _id: String, page: String) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<ItemChat>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.feedbackConversationDetails(_id, page)
                override fun success(response: Response<ItemChat>) {
                    if (response.isSuccessful){
                        if(response.body()!!.data != null){
                            feedbackConversationLiveDataSecond.value = response.body()
                        }
                    }
                }
                override fun error(message: String) {
                    super.error(message)
                }
                override fun loading() {
                    super.loading()
                }
            }
        )
    }






    private var addFeedbackConversationLiveData = MutableLiveData<BaseResponseDC<Any>>()
    val addFeedbackConversationLive : LiveData<BaseResponseDC<Any>> get() = addFeedbackConversationLiveData
    fun addFeedbackConversationDetails(hashMap: RequestBody) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<Any>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.addFeedbackConversation(hashMap)
                override fun success(response: Response<BaseResponseDC<Any>>) {
                    if (response.isSuccessful){
                        addFeedbackConversationLiveData.value = response.body()
                    }
                }
                override fun error(message: String) {
                    super.error(message)
                }
                override fun loading() {
                    super.loading()
                }
            }
        )
    }

}