package com.vegasega.streetsaarthi.screens.onboarding.subscriptionRegister

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import com.vegasega.streetsaarthi.R
import com.vegasega.streetsaarthi.databinding.SubscriptionBinding
import com.vegasega.streetsaarthi.databinding.SubscriptionRegisterBinding
import com.vegasega.streetsaarthi.datastore.DataStoreKeys.LOGIN_DATA
import com.vegasega.streetsaarthi.datastore.DataStoreUtil.readData
import com.vegasega.streetsaarthi.models.ItemCouponLiveList
import com.vegasega.streetsaarthi.models.Login
import com.vegasega.streetsaarthi.networking.RAZORPAY_KEY
import com.vegasega.streetsaarthi.networking.coupon_amount
import com.vegasega.streetsaarthi.networking.coupon_discount
import com.vegasega.streetsaarthi.networking.date_time
import com.vegasega.streetsaarthi.networking.gst_amount
import com.vegasega.streetsaarthi.networking.gst_rate
import com.vegasega.streetsaarthi.networking.membership_id
import com.vegasega.streetsaarthi.networking.month_year
import com.vegasega.streetsaarthi.networking.net_amount
import com.vegasega.streetsaarthi.networking.no_of_month_year
import com.vegasega.streetsaarthi.networking.order_id
import com.vegasega.streetsaarthi.networking.payment_method
import com.vegasega.streetsaarthi.networking.payment_status
import com.vegasega.streetsaarthi.networking.payment_validity
import com.vegasega.streetsaarthi.networking.plan_type
import com.vegasega.streetsaarthi.networking.state_id
import com.vegasega.streetsaarthi.networking.total_amount
import com.vegasega.streetsaarthi.networking.transaction_id
import com.vegasega.streetsaarthi.networking.user_id
import com.vegasega.streetsaarthi.screens.main.subscription.Subscription
import com.vegasega.streetsaarthi.screens.main.subscription.Subscription.Companion.isFrom
import com.vegasega.streetsaarthi.screens.main.subscription.SubscriptionHistory
import com.vegasega.streetsaarthi.screens.main.subscription.SubscriptionPagerAdapter
import com.vegasega.streetsaarthi.screens.main.subscription.SubscriptionVM
import com.vegasega.streetsaarthi.screens.main.subscription.ViewManage
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity.Companion.navHostFragment
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity.Companion.networkFailed
import com.vegasega.streetsaarthi.utils.callNetworkDialog
import com.vegasega.streetsaarthi.utils.changeDateFormat
import com.vegasega.streetsaarthi.utils.dateTime
import com.vegasega.streetsaarthi.utils.gen
import com.vegasega.streetsaarthi.utils.getDateToLongTime
import com.vegasega.streetsaarthi.utils.getDateToLongTimeNow
import com.vegasega.streetsaarthi.utils.hideKeyboard
import com.vegasega.streetsaarthi.utils.loadImage
import com.vegasega.streetsaarthi.utils.orderId
import com.vegasega.streetsaarthi.utils.roundOffDecimal
import com.vegasega.streetsaarthi.utils.showDropDownDialog
import com.vegasega.streetsaarthi.utils.showSnackBar
import com.vegasega.streetsaarthi.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import kotlin.getValue

@AndroidEntryPoint
class SubscriptionRegister : Fragment() {
    private val viewModel: SubscriptionRegisterVM by activityViewModels()
    private var _binding: SubscriptionRegisterBinding? = null
    private val binding get() = _binding!!

    var policyAlert: AlertDialog? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SubscriptionRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }


    var user : Login ?= null


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(0)

        binding.apply {

//            if (arguments?.getString("from") == "fullRegistration") {
                val vendor_id = arguments?.getString("vendor_id") ?: ""
//                isFrom = "fullRegistration"
                viewModel.profile(vendor_id) {
                    user = this
                    if (user?.vending_state?.id != null) {
                        val obj: JSONObject = JSONObject().apply {
                            put(state_id, user?.vending_state?.id)
                        }
                        viewModel.subscription(obj)
                    }
                }
//            } else {
//                isFrom = ""
//            }

            btSignIn.singleClick {
                view.findNavController().navigateUp()
            }

            textViewPolicy.singleClick {
                callPolicyDialog()
            }

            editTextSelectMonthYear.singleClick {
                requireActivity().showDropDownDialog(type = 18){
                    binding.editTextSelectMonthYear.setText(name)
                    viewModel.monthYear = position + 1
                }
            }

            editTextChooseNumber.singleClick {
                requireActivity().showDropDownDialog(type = 19){
                    binding.editTextChooseNumber.setText(name)
                    viewModel.number = position + 1
                }
            }

            btCalculatePrice.singleClick {
                hideKeyboard()
                if (viewModel.number > 0 && viewModel.monthYear > 0) {
                    viewModel.validity = when (viewModel.monthYear) {
                        1 -> if (viewModel.number == 1) "${viewModel.number} " + getString(R.string.month) else "${viewModel.number} " + getString(
                            R.string.months
                        )

                        2 -> if (viewModel.number == 1) "${viewModel.number} " + getString(R.string.year) else "${viewModel.number} " + getString(
                            R.string.years
                        )

                        else -> ""
                    }
                    viewModel.validityMonths = when (viewModel.monthYear) {
                        1 -> viewModel.number
                        2 -> viewModel.number * 12
                        else -> 0
                    }
                    viewModel.validityDays = when (viewModel.monthYear) {
                        1 -> viewModel.number * 30
                        2 -> viewModel.number * 365
                        else -> 0
                    }
                    Log.e("TAG", "viewModel.validity: " +viewModel.validity)
                    Log.e("TAG", "validityMonths: " +viewModel.validityMonths)
                    Log.e("TAG", "validityDays: " +viewModel.validityDays)

                        if (user != null) {
                            if (networkFailed) {
                                if(user?.vending_state?.id != null){
                                    val obj: JSONObject = JSONObject().apply {
                                        put(state_id, user?.vending_state?.id)
                                        put(no_of_month_year, viewModel.number)
                                        put(
                                            month_year,
                                            if (viewModel.monthYear == 1) "month" else "year"
                                        )
                                    }
                                    viewModel.couponLiveList(obj)
                                } else {
                                    showSnackBar(resources.getString(R.string.need_to_add_complete_subscription))
                                }
                            } else {
                                requireContext().callNetworkDialog()
                            }
                        }



                    viewModel.couponLiveListCalled.observe(viewLifecycleOwner, Observer {
                        if (it) {
                            val typeToken = object : TypeToken<ArrayList<ItemCouponLiveList>>() {}.type
                            val changeValue =
                                Gson().fromJson<ArrayList<ItemCouponLiveList>>(
                                    Gson().toJson(viewModel.itemCouponLiveList.value?.data),
                                    typeToken
                                )
                            val changeValueMain : ArrayList<ItemCouponLiveList> = ArrayList()
                            if (changeValue.size > 0) {
                                changeValue.forEach {
                                    if (getDateToLongTime(it.coupon_validity) >= getDateToLongTimeNow()){
                                        changeValueMain.add(it)
                                    }
                                }
                                if (changeValueMain.size > 0) {
                                    var lar = changeValueMain[0].coupon_discount
                                    for (i in changeValueMain.listIterator()){
                                        if(lar < i.coupon_discount){
                                            lar = i.coupon_discount
                                        }
                                    }
                                    viewModel.couponDiscount = lar
                                } else {
                                    viewModel.couponDiscount = 0.0
                                }
                            } else {
                                viewModel.couponDiscount = 0.0
                            }


                            Log.e("TAG", "couponDiscount: " +viewModel.subscription.value?.subscription_cost)
                            Log.e("TAG", "couponDiscount: " + viewModel.couponDiscount)

                            viewModel.membershipCost = viewModel.subscription.value?.subscription_cost?.times(viewModel.validityMonths) ?: 0.0
                            viewModel.couponDiscountPrice = (viewModel.membershipCost * viewModel.couponDiscount) / 100
                            viewModel.afterCouponDiscount = viewModel.membershipCost - viewModel.couponDiscountPrice
                            viewModel.gstPrice = (viewModel.afterCouponDiscount * viewModel.gst) / 100
                            viewModel.afterGst = viewModel.afterCouponDiscount + viewModel.gstPrice
                            viewModel.totalCost = viewModel.afterGst + viewModel.policyCost


                            cbClickCheckbox.setOnClickListener {
                                if (cbClickCheckbox.isChecked) {
                                    viewModel.policyCost = 50.0
                                    viewModel.totalCost = viewModel.afterGst + viewModel.policyCost
                                    update(
                                        viewModel.policyCost,
                                        viewModel.membershipCost,
                                        "" + viewModel.validityDays,
                                        viewModel.gst,
                                        viewModel.gstPrice,
                                        viewModel.couponDiscount,
                                        viewModel.couponDiscountPrice,
                                        viewModel.totalCost,
                                    )
                                } else {
                                    viewModel.policyCost = 0.0
                                    viewModel.totalCost = viewModel.afterGst + viewModel.policyCost
                                    update(
                                        viewModel.policyCost,
                                        viewModel.membershipCost,
                                        "" + viewModel.validityDays,
                                        viewModel.gst,
                                        viewModel.gstPrice,
                                        viewModel.couponDiscount,
                                        viewModel.couponDiscountPrice,
                                        viewModel.totalCost,
                                    )
                                }
                            }

                            update(
                                viewModel.policyCost,
                                viewModel.membershipCost,
                                "" + viewModel.validityDays,
                                viewModel.gst,
                                viewModel.gstPrice,
                                viewModel.couponDiscount,
                                viewModel.couponDiscountPrice,
                                viewModel.totalCost,
                            )
                            groupVisibility.visibility = View.VISIBLE
                        } else {
                            viewModel.couponDiscount = 0.0
                        }
                    })
                } else {
                    showSnackBar(getString(R.string.please_select_month_year_and_number))
                }
            }



            btPurchaseSubscription.singleClick {
                setPayment()
            }


            viewModel.purchaseSubscription.observe(viewLifecycleOwner, Observer {
                groupVisibility.visibility = View.GONE
                editTextSelectMonthYear.setText("")
                editTextChooseNumber.setText("")
                viewModel.number = 0
                viewModel.monthYear = 0


                Handler(Looper.getMainLooper()).postDelayed({
                    requireView().findNavController().navigate(R.id.action_subscriptionRegister_to_loginPassword)
                }, 500)
            })
        }
    }





    fun setPayment(){

        user?.let {

            val co = Checkout()
            co.setKeyID(RAZORPAY_KEY)
            try {
//                Log.e(
//                    "TAG",
//                    "totalXXXCC: " + gstTotalPrice
//                )
                val total: Double =
                    viewModel.totalCost * 100
                Log.e(
                    "TAG",
                    "totalXXXDD: " + total
                )

//                                                            val sss = 130361.0 * 100
//                                                            Log.e("TAG", "totalXXXEE: "+sss)
                val totalX =
                    total.toInt()

                val options =
                    JSONObject()
                options.put(
                    "name",
                    resources.getString(
                        R.string.app_name
                    )
                )
                options.put(
                    "theme.color",
                    "#ff6682"
                )

//                                                            options.put("name","Razorpay Corp")
                options.put(
                    "description",
                    "6 months subs"
                )
                options.put(
                    "image",
                    R.drawable.main_logo
                )
                options.put(
                    "currency",
                    "INR"
                )
                options.put(
                    "amount",
                    "" + totalX
                )
                options.put(
                    "send_sms_hash",
                    true
                )
                options.put(
                    "readOnlyName",
                    "com.klifora.shop"
                )
                options.put(
                    "orderId",
                    ""
                )

                val prefill =
                    JSONObject()
//                                                    prefill.put("email", "test@razorpay.com")
//                                                    prefill.put("contact", "9988397522")
                prefill.put(
                    "name",
                    user!!.vendor_first_name
                )
//                prefill.put(
//                    "email",
//                    data.vendor_first_name
//                )
                prefill.put(
                    "contact",
                    user!!.mobile_no
                )
                options.put(
                    "prefill",
                    prefill
                )

                val method = JSONObject()
                method.put("upi", true)
                method.put("card", false)
                method.put("wallet", false)
                method.put("netbanking", false)
                method.put("paylater", false)
                options.put("method", method)
//                options.put("theme.color", "#3399cc")
                co.open(
                    requireActivity(),
                    options
                )
            } catch (e: Exception) {
//                                                        onPaymentError 0 ::: undefined ::: com.razorpay.PaymentData@dc1cb00
                Toast.makeText(
                    requireContext(),
                    "Error in payment: " + e.message,
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
        }
    }





    private fun update(
        policyCost: Double = 0.00,
        membershipCost: Double = 0.00,
        validity: String,
        gst: Double,
        gstPrice: Double,
        couponDiscount: Double,
        couponDiscountPrice: Double,
        totalCost: Double = 0.00
    ) {
        binding.apply {
            textPolicyCostValue.text = resources.getString(
                R.string.rupees, policyCost.roundOffDecimal()
            )
            textMembershipCostValue.text = resources.getString(
                R.string.rupees, membershipCost.roundOffDecimal()
            )
            textValidityValue.text = resources.getString(R.string.days, "${validity}")
//            textGSTValue.text = "${gst} %"
//            textCouponDiscountValue.text = "${couponDiscount} %"

            textCouponDiscountTxt.text = resources.getString(R.string.discount, "${couponDiscount}%")
            textCouponDiscountValue.text = resources.getString(
                R.string.rupees, couponDiscountPrice.roundOffDecimal()
            )
            textGSTTxt.text = resources.getString(R.string.gst, "${gst}%")
            textGSTValue.text = resources.getString(
                R.string.rupees, gstPrice.roundOffDecimal()
            )

            textTotalCostValue.text =
                resources.getString(R.string.rupees, totalCost.roundOffDecimal())

            if(couponDiscount > 0.0){
                groupCouponDiscountVisibility.visibility = View.VISIBLE
            } else {
                groupCouponDiscountVisibility.visibility = View.GONE
            }
        }
    }













    fun onPaymentSuccess(p0: String?, p1: PaymentData?) {

        Log.e("TAG", "payJSON1 " + p0.toString())
        Log.e("TAG", "payJSON2 " + p1.toString())

        if (user != null) {
            if (networkFailed) {
                val obj: JSONObject = JSONObject().apply {
                    put(user_id, user!!.id)
                    put(membership_id, user!!.membership_id)
                    put(order_id, user!!.membership_id.orderId())
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



    fun getCheckoutFragment(): SubscriptionRegister? {
        // Get the NavHostFragment
//        val navHostFragment = getSupportFragmentManager()
//            .findFragmentById(R.id.navigation_bar)

        if (navHostFragment != null) {
            // Get the currently displayed fragment inside the NavHostFragment
            val currentFragment = navHostFragment!!.childFragmentManager.primaryNavigationFragment

            if (currentFragment is SubscriptionRegister) {
                return currentFragment
            }
        }
        return null
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




    @SuppressLint("SuspiciousIndentation")
    fun callPolicyDialog() {
        if (policyAlert?.isShowing == true) {
            return
        }

        policyAlert = MaterialAlertDialogBuilder(requireContext(), R.style.LogoutDialogTheme)
            .setTitle("Insurance Policy")
            .setMessage("1. Policy Overview\n" +
                    "\n" +
                    "This insurance policy is designed to provide financial protection and social security to registered street vendors against unforeseen events such as accidental death, permanent disability, and health emergencies. The policy aims to support vendors and their families during difficult times.\n" +
                    "\n" +
                    "2. Eligibility\n" +
                    "\n" +
                    "The applicant must be a registered street vendor under the Street Vendors (Protection of Livelihood and Regulation of Street Vending) Act, 2014.\n" +
                    "\n" +
                    "The vendor must be 18 to 60 years of age.\n" +
                    "\n" +
                    "The vendor should possess a valid Vendor ID / Registration Certificate issued by the local authority.\n" +
                    "\n" +
                    "The policy is applicable only within India.")
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }






    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onCleared()
    }


}