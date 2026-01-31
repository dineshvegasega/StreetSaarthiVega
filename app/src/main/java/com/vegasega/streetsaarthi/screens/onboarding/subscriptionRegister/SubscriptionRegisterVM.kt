package com.vegasega.streetsaarthi.screens.onboarding.subscriptionRegister

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.vegasega.streetsaarthi.R
import com.vegasega.streetsaarthi.datastore.DataStoreKeys
import com.vegasega.streetsaarthi.datastore.DataStoreUtil.saveObject
import com.vegasega.streetsaarthi.models.BaseResponseDC
import com.vegasega.streetsaarthi.models.ItemSubscription
import com.vegasega.streetsaarthi.models.Login
import com.vegasega.streetsaarthi.networking.ApiInterface
import com.vegasega.streetsaarthi.networking.CallHandler
import com.vegasega.streetsaarthi.networking.Repository
import com.vegasega.streetsaarthi.networking.getJsonRequestBody
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity
import com.vegasega.streetsaarthi.utils.showSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SubscriptionRegisterVM @Inject constructor(private val repository: Repository) : ViewModel() {


    var policyCost: Double = 0.0
    var membershipCost: Double = 0.0
    var validity: String = ""
    var validityMonths: Int = 0
    var validityDays: Int = 0
    var gst: Double = 18.0
    var gstPrice : Double = 18.0
    var afterGst: Double = 0.0
    var couponDiscount: Double = 0.0
    var couponDiscountPrice: Double = 0.0
    var afterCouponDiscount: Double = 0.0
    var totalCost: Double = 0.0
    var monthYear: Int = 0
    var number: Int = 0

    var subscription = MutableLiveData<ItemSubscription>()
    fun subscription(jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.subscription(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        if (response.body()!!.data != null) {
                            subscription.value = Gson().fromJson(
                                response.body()!!.data,
                                ItemSubscription::class.java
                            )
                        }
                    }
                }

                override fun error(message: String) {
//                    super.error(message)
                    //  showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    var purchaseSubscription = MutableLiveData<Boolean>()
    fun purchaseSubscription(jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.purchaseSubscription(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        purchaseSubscription.value = true
                        showSnackBar(MainActivity.context.get()?.resources!!.getString(R.string.subscription_added_successfully))
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                    //  showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }



    fun profile(_id: String, callBack: Login.() -> Unit) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.profile(_id)

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        if(response.body()!!.data != null){
                            val login = Gson().fromJson(response.body()!!.data, Login::class.java)
                            callBack(login)
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





    var couponLiveListCalled = MutableLiveData<Boolean>(false)
    private var itemCouponLiveListResult = MutableLiveData<BaseResponseDC<Any>>()
    val itemCouponLiveList: LiveData<BaseResponseDC<Any>> get() = itemCouponLiveListResult
    fun couponLiveList(jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.couponLiveList(requestBody = jsonObject.getJsonRequestBody())
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        itemCouponLiveListResult.value = response.body() as BaseResponseDC<Any>
                        couponLiveListCalled.value = true
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                    //  showSnackBar(message)
                    // couponLiveListCalled.value = false
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }




    public override fun onCleared() {
        super.onCleared()
        monthYear = 0
        number = 0
    }

}