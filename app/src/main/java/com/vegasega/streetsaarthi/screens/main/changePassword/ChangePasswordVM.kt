package com.vegasega.streetsaarthi.screens.main.changePassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vegasega.streetsaarthi.networking.ApiInterface
import com.vegasega.streetsaarthi.networking.CallHandler
import com.vegasega.streetsaarthi.networking.Repository
import com.google.gson.JsonElement
import com.vegasega.streetsaarthi.models.BaseResponseDC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ChangePasswordVM @Inject constructor(private val repository: Repository): ViewModel() {
    var isAgree = MutableLiveData<Boolean>(false)


    var itemUpdatePasswordResult = MutableLiveData<Boolean>(false)
    fun updatePassword(hashMap: RequestBody) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.passwordUpdate(hashMap)
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        itemUpdatePasswordResult.value = true
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