package com.vegasega.streetsaarthi.screens.main.subscription

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.razorpay.PaymentData
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import com.vegasega.streetsaarthi.R
import com.vegasega.streetsaarthi.databinding.SubscriptionBinding
import com.vegasega.streetsaarthi.datastore.DataStoreKeys
import com.vegasega.streetsaarthi.datastore.DataStoreKeys.LOGIN_DATA
import com.vegasega.streetsaarthi.datastore.DataStoreUtil.readData
import com.vegasega.streetsaarthi.datastore.DataStoreUtil.saveData
import com.vegasega.streetsaarthi.datastore.DataStoreUtil.saveObject
import com.vegasega.streetsaarthi.models.Login
import com.vegasega.streetsaarthi.networking.*
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity.Companion.navHostFragment
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity.Companion.networkFailed
import com.vegasega.streetsaarthi.utils.callNetworkDialog
import com.vegasega.streetsaarthi.utils.changeDateFormat
import com.vegasega.streetsaarthi.utils.dateTime
import com.vegasega.streetsaarthi.utils.gen
import com.vegasega.streetsaarthi.utils.imageZoom
import com.vegasega.streetsaarthi.utils.loadImage
import com.vegasega.streetsaarthi.utils.orderId
import com.vegasega.streetsaarthi.utils.showSnackBar
import com.vegasega.streetsaarthi.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class Subscription : Fragment(){
    private val viewModel: SubscriptionVM by activityViewModels()
    private var _binding: SubscriptionBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter : SubscriptionPagerAdapter

    companion object {
        var isFrom = ""
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SubscriptionBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(0)

        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.subscription)
            inclideHeaderSearch.editTextSearch.visibility = View.GONE
            inclideHeaderSearch.textHeaderEditTxt.visibility = View.GONE

            if (arguments?.getString("from") == "fullRegistration") {
                val vendor_id = arguments?.getString("vendor_id") ?: ""
                isFrom = "fullRegistration"
                viewModel.profile(vendor_id){

                }
            } else {
                isFrom = ""
            }

            lateinit var viewer: StfalconImageViewer<String>
            readData(LOGIN_DATA) { loginUser ->
                if (loginUser != null) {
                    val data = Gson().fromJson(loginUser, Login::class.java)
                    data.profile_image_name?.let {
                        inclidePersonalProfile.ivImageProfile.loadImage(type = 1, url = { data.profile_image_name.url })
                        inclidePersonalProfile.ivImageProfile.singleClick {
//                            data.profile_image_name?.let {
//                                arrayListOf(it.url).imageZoom(inclidePersonalProfile.ivImageProfile, 2)
//                            }

                            viewer = StfalconImageViewer.Builder<String>(binding.root.context, arrayListOf(data.profile_image_name.url)) { view, image ->
                                Picasso.get().load(image).into(view)
                            }.withImageChangeListener {
                                viewer.updateTransitionImage(inclidePersonalProfile.ivImageProfile)
                            }
                                .withBackgroundColor(
                                    ContextCompat.getColor(
                                        binding.root.context,
                                        R.color._D9000000
                                    )
                                )
                                .show()
                        }
                    }
                    inclidePersonalProfile.textNameOfMember.text = "${data.vendor_first_name} ${data.vendor_last_name}"
                    inclidePersonalProfile.textMobileNumber.text = "+91-${data.mobile_no}"
                    inclidePersonalProfile.textMembershipIdValue.text = "${data.member_id}"
                    data.validity_to?.let {
                        inclidePersonalProfile.textValidUptoValue.text = "${data.membership_validity.changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy")}"
                    }


                    if(networkFailed) {
                        val _id = Gson().fromJson(loginUser, Login::class.java)?.vending_state?.id
                        val obj: JSONObject = JSONObject().apply {
                            put(state_id, _id)
                        }
                        viewModel.subscription(obj)
                    } else {
                        requireContext().callNetworkDialog()
                    }
                }
            }


            adapter= SubscriptionPagerAdapter(requireActivity())
            adapter.notifyDataSetChanged()
            introViewPager.isUserInputEnabled = false
            adapter.addFragment(ViewManage())
            adapter.addFragment(SubscriptionHistory())

            Handler(Looper.getMainLooper()).postDelayed({
                introViewPager.adapter=adapter
                val array = listOf<String>(getString(R.string.view_manage), getString(R.string.history))
                TabLayoutMediator(tabLayout, introViewPager) { tab, position ->
                    tab.text = array[position]
                    //setTabStyle(tabLayout, array[position])
                }.attach()
            }, 100)

            introViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                }
            })

            viewModel.purchaseSubscription.observe(viewLifecycleOwner, Observer {
                if (it){
                    introViewPager.setCurrentItem(1)
                }
            })

//
//            viewModel.purchaseSubscriptionFromRegister.observe(viewLifecycleOwner, Observer {
//                if (it){
//                    view?.findNavController()?.navigate(R.id.action_subscription_to_registerSuccessful, Bundle().apply {
//                        putString("key", "ddddd")
////                            putString("from", "quickRegistration")
//                        putString("from", "fullRegistration")
//                        putString("vendor_id", "4589")
//                    })
//                }
//            })
        }
    }




    fun getCheckoutFragment(): Subscription? {
        // Get the NavHostFragment
//        val navHostFragment = getSupportFragmentManager()
//            .findFragmentById(R.id.navigation_bar)

        if (navHostFragment != null) {
            // Get the currently displayed fragment inside the NavHostFragment
            val currentFragment = navHostFragment!!.childFragmentManager.primaryNavigationFragment

            if (currentFragment is Subscription) {
                return currentFragment
            }
        }
        return null
    }



    fun onPaymentSuccess(p0: String?, p1: PaymentData?) {

        Log.e("TAG", "payJSON1 " + p0.toString())
        Log.e("TAG", "payJSON2 " + p1.toString())

//        if (isFrom == "fullRegistration") {
//            readData(LOGIN_DATA) { loginUser ->
//                    if (loginUser != null) {
//                        val data = Gson().fromJson(loginUser, Login::class.java)
//                        if (networkFailed) {
//                            val obj: JSONObject = JSONObject().apply {
//                                put(user_id, data.id)
//                                put(membership_id, data.membership_id)
//                                put(order_id, data.membership_id.orderId())
//                                put(date_time, dateTime())
//                                put(transaction_id, gen())
//                                put(plan_type, "subscription")
//                                put(payment_method, "UPI")
//                                put(payment_status,"success")
//                                put(payment_validity, viewModel.validityDays)
//                                put(net_amount, viewModel.membershipCost)
//                                put(coupon_discount, viewModel.couponDiscount)
//                                put(coupon_amount, if (viewModel.couponDiscountPrice == 0.0) "0.00" else viewModel.couponDiscountPrice)
//                                put(gst_rate, viewModel.gst)
//                                put(gst_amount, viewModel.gstPrice)
//                                put(total_amount, viewModel.totalCost)
//                            }
//                            if (viewModel.number > 0 && viewModel.monthYear > 0) {
//                                if (viewModel.totalCost > 0) {
//                                    viewModel.purchaseSubscriptionFromRegister(obj)
//                                } else {
//                                    showSnackBar(getString(R.string.please_calculate_price))
//                                }
//                            } else {
//                                showSnackBar(getString(R.string.please_select_month_year_and_number))
//                            }
//                        } else {
//                            requireContext().callNetworkDialog()
//                        }
//                    }
//                }
//        } else {
            readData(LOGIN_DATA) { loginUser ->
                if (loginUser != null) {
                    val data = Gson().fromJson(loginUser, Login::class.java)
                    if (networkFailed) {
                        val obj: JSONObject = JSONObject().apply {
                            put(user_id, data.id)
                            put(membership_id, data.membership_id)
                            put(order_id, data.membership_id.orderId())
                            put(date_time, dateTime())
                            put(transaction_id, gen())
                            put(plan_type, "subscription")
                            put(payment_method, "UPI")
                            put(payment_status,"success")
                            put(payment_validity, viewModel.validityDays)
                            put(net_amount, viewModel.membershipCost)
                            put(coupon_discount, viewModel.couponDiscount)
                            put(coupon_amount, if (viewModel.couponDiscountPrice == 0.0) "0.00" else viewModel.couponDiscountPrice)
                            put(gst_rate, viewModel.gst)
                            put(gst_amount, viewModel.gstPrice)
                            put(total_amount, viewModel.totalCost)
                        }
                        if (viewModel.number > 0 && viewModel.monthYear > 0) {
                            if (viewModel.totalCost > 0) {
                                viewModel.purchaseSubscription(obj)
                            } else {
                                showSnackBar(getString(R.string.please_calculate_price))
                            }
                        } else {
                            showSnackBar(getString(R.string.please_select_month_year_and_number))
                        }
                    } else {
                        requireContext().callNetworkDialog()
                    }
                }
//            }
        }





//        readData(LOGIN_DATA) { loginUser ->
//            if (loginUser != null) {
//                val data = Gson().fromJson(
//                    loginUser,
//                    ItemUserItem::class.java
//                )
//
//
//                val payJSON = JSONObject(p1?.data.toString())
////                Log.e("TAG", "payJSON " + payJSON)
//
//                if (payJSON.has("razorpay_payment_id")) {
//                    val payName = payJSON.getString("razorpay_payment_id")
//                    Log.e("TAG", "payName " + payName)
//
//                    readData(ADMIN_TOKEN) { tokenAdmin ->
//                        readData(WEBSITE_DATA) { webData ->
//                            if (webData != null) {
//                                val data = Gson().fromJson(
//                                    webData,
//                                    ItemWebsite::class.java
//                                )
//
//                                val customerData = JSONObject().apply {
//                                    put("customerEmail", "" + data.email)
//                                }
//
//
//                                val invoiceData = JSONObject().apply {
//                                    put("capture", true)
//                                    put("notify", true)
//                                    put("appendComment", false)
//                                    put("comment", JSONObject().apply {
//                                        put("comment", "Invoice created for Razrpay transaction")
//                                    })
//                                    put("is_visible_on_front", 0)
//                                }
//
//                                viewModel.getInvoice(tokenAdmin!!, invoiceData, orderID) {
//
//                                    val transactionsData = JSONObject().apply {
//                                        put("data", JSONObject().apply {
//                                            put("order_id", orderID)
//                                            put("razorpay_order_id", "" + payName)
//                                            put("razorpay_payment_id", "" + payName)
//                                            put("razorpay_signature", "" + payName)
//                                        })
//                                    }
//
//
//                                    viewModel.getTransactions(tokenAdmin, transactionsData) {
//                                        viewModel.getQuoteId(customerToken, JSONObject()) {
//                                            viewModel.resetToken(
//                                                tokenAdmin,
//                                                data.website_id,
//                                                customerData
//                                            ) {
//                                                val token = this
//                                                saveData(CUSTOMER_TOKEN, token)
//
//                                                findNavController().navigate(
//                                                    R.id.action_checkout_to_thankyou,
//                                                    Bundle().apply {
//                                                        putString("orderID", "" + orderIDForSend)
//                                                    })
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                } else {
//                    showSnackBar("Error in payment")
//                }
//            }
//        }
    }


    fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Log.e(
            "TAG",
            "onPaymentError " + p0.toString() + " ::: " + p1.toString() + " ::: " + p2?.data.toString()
        )
//        if (!p2?.data.toString().equals("{}")) {
//            try {
//
//                val jsonNull = JSONObject(p1.toString())
//                val errorJSON = jsonNull.getJSONObject("error")
//                val description = errorJSON.getString("description")
//
//
//
//                readData(LOGIN_DATA) { loginUser ->
//                    if (loginUser != null) {
//                        val data = Gson().fromJson(
//                            loginUser,
//                            ItemUserItem::class.java
//                        )
//
//                        readData(ADMIN_TOKEN) { tokenAdmin ->
//                            readData(WEBSITE_DATA) { webData ->
//                                if (webData != null) {
//                                    val data = Gson().fromJson(
//                                        webData,
//                                        ItemWebsite::class.java
//                                    )
//
//                                    val customerData = JSONObject().apply {
//                                        put("customerEmail", "" + data.email)
//                                    }
//
//
//                                    val invoiceData = JSONObject().apply {
//                                        put("capture", "void")
//                                        put("notify", true)
//                                        put("appendComment", false)
//                                        put("comment", JSONObject().apply {
//                                            put(
//                                                "comment",
//                                                "Invoice created failed for Razorpay transaction"
//                                            )
//                                        })
//                                        put("is_visible_on_front", 0)
//                                    }
//
////                                    viewModel.getInvoice(tokenAdmin!!, invoiceData, orderID) {
//
//                                    viewModel.getCancel(tokenAdmin!!, invoiceData, orderID) {
//                                        viewModel.getQuoteId(customerToken, JSONObject()) {
//                                            saveData(QUOTE_ID, this)
//                                            viewModel.resetToken(
//                                                tokenAdmin,
//                                                data.website_id,
//                                                customerData
//                                            ) {
//                                                val token = this
//                                                saveData(CUSTOMER_TOKEN, token)
//
//
//                                                mainThread {
//                                                    itemCartItem?.let {
//                                                        itemCartItem!!.items.forEach { cartItem ->
//                                                            readData(QUOTE_ID) {
//                                                                val json: JSONObject =
//                                                                    JSONObject().apply {
//                                                                        put("sku", cartItem.sku)
//                                                                        put("qty", cartItem.qty)
//                                                                        put(
//                                                                            "quote_id",
//                                                                            it.toString()
//                                                                        )
//                                                                    }
//                                                                val jsonCartItem: JSONObject =
//                                                                    JSONObject().apply {
//                                                                        put("cartItem", json)
//                                                                    }
//                                                                readData(CUSTOMER_TOKEN) { token ->
//                                                                    viewModel.addCart(
//                                                                        token!!,
//                                                                        jsonCartItem
//                                                                    ) {
//                                                                        //cartMutableList.value = true
//                                                                        Log.e(
//                                                                            "TAG",
//                                                                            "onCallBack: ${this.toString()}"
//                                                                        )
////                                                                        showSnackBar(getString(R.string.item_added_to_cart))
//                                                                        MainActivity.mainActivity.get()!!
//                                                                            .callCartApi()
//                                                                    }
//                                                                }
//                                                            }
//                                                        }
//                                                    }
//
//
//
//                                                }
//
//
//
//                                                MaterialAlertDialogBuilder(
//                                                    Companion.activity.get()!!,
//                                                    R.style.LogoutDialogTheme
//                                                )
//                                                    .setTitle(resources.getString(R.string.app_name))
//                                                    .setMessage("Payment failed, please try again.")
//                                                    .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
//                                                        dialog.dismiss()
////                        findNavController().navigate(R.id.action_checkout_to_cart2)
//                                                    }
////                    .setNegativeButton(resources.getString(R.string.try_again)) { dialog, _ ->
////                        dialog.dismiss()
////                        readData(MOBILE_NUMBER) { number ->
////                            val co = Checkout()
////                            co.setKeyID(RAZORPAY_KEY)
////                            try {
////                                Log.e("TAG", "totalXXXCC: "+gstTotalPrice)
////                                val total : Double = gstTotalPrice * 100
////                                Log.e("TAG", "totalXXXDD: "+total)
////
////                                val totalX = total.toInt()
////
////                                val options = JSONObject()
////                                options.put("name",resources.getString(R.string.app_name))
//////                                                            options.put("name","Razorpay Corp")
////                                options.put("description", name)
////                                options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
////                                options.put("currency","INR")
////                                options.put("amount", ""+totalX)
////                                options.put("send_sms_hash",true)
////                                options.put("readOnlyName","com.klifora.shop")
////                                options.put("orderId",""+skuS)
////
////                                val prefill = JSONObject()
//////                                                    prefill.put("email", "test@razorpay.com")
//////                                                    prefill.put("contact", "9988397522")
////                                prefill.put("name", binding.editTextN.text.toString())
////                                prefill.put("email", binding.editEmail.text.toString())
////                                prefill.put("contact", binding.editMobileNo.text.toString())
////                                options.put("prefill", prefill)
////                                co.open(requireActivity(), options)
////                            }catch (e: Exception){
//////                                                        onPaymentError 0 ::: undefined ::: com.razorpay.PaymentData@dc1cb00
////                                Toast.makeText(requireContext(),"Error in payment: "+ e.message, Toast.LENGTH_LONG).show()
////                                e.printStackTrace()
////                            }
////                        }
////                    }
//                                                    .setCancelable(false)
//                                                    .show()
//
//                                            }
//                                        }
//                                    }
//
////                                        val transactionsData = JSONObject().apply {
////                                            put("data", JSONObject().apply {
////                                                put("order_id", orderID)
////                                                put("razorpay_order_id", ""+payName)
////                                                put("razorpay_payment_id", ""+payName)
////                                                put("razorpay_signature", ""+payName)
////                                            })
////                                        }
//
//
////                                    }
//                                }
//                            }
//                        }
//
//
//                    }
//                }
//
//
//            } catch (e: java.lang.Exception) {
//                e.printStackTrace()
//            }
//        }
    }

    fun onExternalWalletSelected(p0: String?, p1: PaymentData?) {
//        try {
//            MaterialAlertDialogBuilder(Companion.activity.get()!!, R.style.LogoutDialogTheme)
//                .setTitle(resources.getString(R.string.app_name))
//                .setMessage("External wallet was selected : Payment Data: ${p1?.data}")
//                .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
//                    dialog.dismiss()
//                }
//                .setCancelable(false)
//                .show()
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onCleared()
    }

}