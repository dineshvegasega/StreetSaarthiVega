package com.vegasega.streetsaarthi.screens.onboarding.quickRegistration

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.vegasega.streetsaarthi.R
import com.vegasega.streetsaarthi.databinding.QuickRegistrationBinding
import com.vegasega.streetsaarthi.screens.interfaces.CallBackListener
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity.Companion.networkFailed
import com.vegasega.streetsaarthi.networking.*
import com.vegasega.streetsaarthi.utils.OtpTimer
import com.vegasega.streetsaarthi.utils.callNetworkDialog
import com.vegasega.streetsaarthi.utils.singleClick
import com.vegasega.streetsaarthi.utils.updatePagerHeightForChild
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class QuickRegistration : Fragment(), CallBackListener{
    private var _binding: QuickRegistrationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuickRegistrationVM by activityViewModels()
    var tabPosition: Int = 0;

    companion object{
        var callBackListener: CallBackListener? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = QuickRegistrationBinding.inflate(inflater)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED)

        MainActivity.mainActivity.get()?.callFragment(0)
        callBackListener = this
        binding.apply {
            val adapter= QuickRegistrationAdapter(requireActivity())
            adapter.notifyDataSetChanged()
            introViewPager.adapter=adapter
            introViewPager.setUserInputEnabled(false)

            btSignIn.setEnabled(false)


            viewModel.isAgree.observe(viewLifecycleOwner, Observer {
                if (it == true){
                    btSignIn.setEnabled(true)
                    btSignIn.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._E79D46, null)))
                } else {
                    btSignIn.setEnabled(false)
                    btSignIn.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._999999, null)))
                }
            })

            viewModel.isSendMutable.value = false
            viewModel.isSendMutable.observe(viewLifecycleOwner, Observer {
                if (it == true){
                    btSignIn.setEnabled(true)
                    btSignIn.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._E79D46, null)))
                }
            })


            btSignIn.singleClick {
                if (tabPosition == 0){
                    QuickRegistration1.callBackListener!!.onCallBack(1)
                } else if (tabPosition == 1){
                    QuickRegistration2.callBackListener!!.onCallBack(3)
                }
                loadProgress(tabPosition)
            }

            textBack.singleClick {
                if (tabPosition == 0){
                    view.findNavController().navigateUp()
                } else if (tabPosition == 1){
                    btSignIn.setEnabled(true)
                    btSignIn.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._E79D46, null)))
                    introViewPager.setCurrentItem(0, false)
                    btSignIn.setText(getString(R.string.continues))
                }
                loadProgress(tabPosition)
            }



            introViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    introViewPager.requestLayout()
                    super.onPageSelected(position)
                    tabPosition = position
                    if(position == 1) {
                        btSignIn.setEnabled(false)
                        btSignIn.setBackgroundTintList(
                            ColorStateList.valueOf(
                                ResourcesCompat.getColor(
                                    getResources(), R.color._999999, null)))
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                }
            })


            introViewPager.setPageTransformer { page, position ->
                introViewPager.updatePagerHeightForChild(page)
            }
        }
    }



    private fun loadProgress(tabPosition: Int) {
        val forOne = 100 / 2
        val myPro = tabPosition + 1
        val myProTotal = forOne * myPro
        binding.loading.progress = myProTotal
    }

    override fun onCallBack(pos: Int) {
        binding.apply {
            if (pos == 21){
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            } else if (pos == 2){
                introViewPager.setCurrentItem(1, false)
                btSignIn.setText(getString(R.string.RegisterNow))
            } else if (pos == 4){
                val obj: JSONObject = JSONObject().apply {
                    put(vendor_first_name, viewModel.data.vendor_first_name)
                    put(vendor_last_name, viewModel.data.vendor_last_name)
                    put(mobile_no, viewModel.data.mobile_no)
                    put(password, viewModel.data.password)
                    put(user_type, USER_TYPE)
                }
                if(networkFailed) {
                    viewModel.register(view = requireView(), obj)
                } else {
                    requireContext().callNetworkDialog()
                }
            }
        }
        loadProgress(tabPosition)
    }




    override fun onDestroyView() {
        viewModel.isAgree.value = false
        OtpTimer.sendOtpTimerData = null
        OtpTimer.stopTimer()
        _binding = null
        super.onDestroyView()
    }
}