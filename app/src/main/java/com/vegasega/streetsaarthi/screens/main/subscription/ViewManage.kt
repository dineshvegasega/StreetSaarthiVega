package com.vegasega.streetsaarthi.screens.main.subscription

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.vegasega.streetsaarthi.R
import com.vegasega.streetsaarthi.databinding.ViewManageBinding
import com.vegasega.streetsaarthi.datastore.DataStoreKeys.LOGIN_DATA
import com.vegasega.streetsaarthi.datastore.DataStoreUtil.readData
import com.vegasega.streetsaarthi.models.ItemCouponLiveList
import com.vegasega.streetsaarthi.models.Login
import com.vegasega.streetsaarthi.networking.*
import com.vegasega.streetsaarthi.screens.main.subscription.Subscription.Companion.isFrom
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity.Companion.logoutAlert
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity.Companion.navHostFragment
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity.Companion.networkFailed
import com.vegasega.streetsaarthi.utils.callNetworkDialog
import com.vegasega.streetsaarthi.utils.dateTime
import com.vegasega.streetsaarthi.utils.gen
import com.vegasega.streetsaarthi.utils.getDateToLongTime
import com.vegasega.streetsaarthi.utils.getDateToLongTimeNow
import com.vegasega.streetsaarthi.utils.hideKeyboard
import com.vegasega.streetsaarthi.utils.orderId
import com.vegasega.streetsaarthi.utils.roundOffDecimal
import com.vegasega.streetsaarthi.utils.showDropDownDialog
import com.vegasega.streetsaarthi.utils.showSnackBar
import com.vegasega.streetsaarthi.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody
import org.json.JSONObject
import kotlin.text.get

@AndroidEntryPoint
class ViewManage : Fragment() {
    private val viewModel: SubscriptionVM by activityViewModels()
    private var _binding: ViewManageBinding? = null
    private val binding get() = _binding!!
    var policyAlert: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ViewManageBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(0)

        binding.apply {
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

            textViewPolicy.singleClick {
                callPolicyDialog()
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


                    readData(LOGIN_DATA) { loginUser ->
                        if (loginUser != null) {
                            val data = Gson().fromJson(loginUser, Login::class.java)
                            if (networkFailed) {
                                if(data?.vending_state?.id != null){
                                    val obj: JSONObject = JSONObject().apply {
                                        put(state_id, data?.vending_state?.id)
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

                            viewModel.membershipCost =
                                viewModel.subscription.value?.subscription_cost?.times(viewModel.validityMonths) ?: 0.0
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

//            btPurchaseSubscription.singleClick {
//              hideKeyboard()
//                readData(LOGIN_DATA) { loginUser ->
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
//                                    viewModel.purchaseSubscription(obj)
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
//            }



            viewModel.purchaseSubscription.observe(viewLifecycleOwner, Observer {
                groupVisibility.visibility = View.GONE
                editTextSelectMonthYear.setText("")
                editTextChooseNumber.setText("")
                viewModel.number = 0
                viewModel.monthYear = 0
            })
        }
    }

    //    val mrp: BigDecimal= "0.00".toBigDecimal(),
//    val price: BigDecimal = "0.00".toBigDecimal(),
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


    private fun showDropDownMonthYearDialog() {
        val list = resources.getStringArray(R.array.month_year_array)
        MaterialAlertDialogBuilder(requireContext(), R.style.DropdownDialogTheme)
            .setTitle(resources.getString(R.string.select_month_year))
            .setItems(list) { _, which ->
                binding.editTextSelectMonthYear.setText(list[which])
                viewModel.monthYear = which + 1
            }.show()
    }


    private fun showDropDownChooseNumberDialog() {
        val list = resources.getStringArray(R.array.numbers_array)
        MaterialAlertDialogBuilder(requireContext(), R.style.DropdownDialogTheme)
            .setTitle(resources.getString(R.string.choose_number))
            .setItems(list) { _, which ->
                binding.editTextChooseNumber.setText(list[which])
                viewModel.number = which + 1
            }.show()
    }



    fun setPayment(){
        readData(LOGIN_DATA) { loginUser ->
            val data = Gson().fromJson(loginUser, Login::class.java)
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
                    data.vendor_first_name
                )
//                prefill.put(
//                    "email",
//                    data.vendor_first_name
//                )
                prefill.put(
                    "contact",
                    data.mobile_no
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

}