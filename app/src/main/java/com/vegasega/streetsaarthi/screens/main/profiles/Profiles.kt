package com.vegasega.streetsaarthi.screens.main.profiles

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import com.vegasega.streetsaarthi.R
import com.vegasega.streetsaarthi.databinding.ProfilesBinding
import com.vegasega.streetsaarthi.datastore.DataStoreKeys.LOGIN_DATA
import com.vegasega.streetsaarthi.datastore.DataStoreUtil.readData
import com.vegasega.streetsaarthi.models.Login
import com.vegasega.streetsaarthi.screens.interfaces.CallBackListener
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity
import com.vegasega.streetsaarthi.utils.changeDateFormat
import com.vegasega.streetsaarthi.utils.imageZoom
import com.vegasega.streetsaarthi.utils.loadImage
import com.vegasega.streetsaarthi.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Profiles : Fragment() , CallBackListener {
    private val viewModel: ProfilesVM by activityViewModels()
    private var _binding: ProfilesBinding? = null
    private val binding get() = _binding!!

    companion object{
        var callBackListener: CallBackListener? = null
        var tabPosition = 0
    }

    lateinit var adapter : ProfilePagerAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfilesBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)
        callBackListener = this

        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.your_Profile)
            inclideHeaderSearch.editTextSearch.visibility = View.GONE

            inclideHeaderSearch.textHeaderEditTxt.visibility = View.GONE
            inclideHeaderSearch.textHeaderEditTxt.singleClick {
                inclideHeaderSearch.textHeaderEditTxt.visibility = View.INVISIBLE
                btSave.visibility = View.VISIBLE
                btCancel.visibility = View.VISIBLE
                viewModel.isEditable.value = true
            }

            btSave.singleClick {
                PersonalDetails.callBackListener!!.onCallBack(1)
            }

            viewModel.isEditable.value = false
            btCancel.singleClick {
                inclideHeaderSearch.textHeaderEditTxt.visibility = View.VISIBLE
                btSave.visibility = View.GONE
                btCancel.visibility = View.GONE
                viewModel.isEditable.value = false
            }

            inclideHeaderSearch.btNominee.singleClick {
                view.findNavController().navigate(R.id.action_profiles_to_nomineeDetails)
            }

            readData(LOGIN_DATA) { loginUser ->
                if (loginUser != null) {
//                    Log.e("TAG", "loginUser "+loginUser)
                    val data = Gson().fromJson(loginUser, Login::class.java).status
                    when(data){
                        "approved" -> {
                            inclideHeaderSearch.textHeaderEditTxt.visibility = View.GONE
                            inclideHeaderSearch.btNominee.visibility = View.VISIBLE
                        }
                        "unverified" -> {
                            inclideHeaderSearch.textHeaderEditTxt.visibility = View.VISIBLE
                            inclideHeaderSearch.btNominee.visibility = View.GONE
                        }
                        "pending" -> {
                            inclideHeaderSearch.textHeaderEditTxt.visibility = View.VISIBLE
                            inclideHeaderSearch.btNominee.visibility = View.GONE
                        }
                        "rejected" -> {
                            inclideHeaderSearch.textHeaderEditTxt.visibility = View.VISIBLE
                            inclideHeaderSearch.btNominee.visibility = View.GONE
                        }
                        else -> {
                            inclideHeaderSearch.textHeaderEditTxt.visibility = View.GONE
                            inclideHeaderSearch.btNominee.visibility = View.GONE
                        }
                    }
                }
            }


            adapter= ProfilePagerAdapter(requireActivity())
            adapter.notifyDataSetChanged()
            introViewPager.isUserInputEnabled = false
            adapter.addFragment(PersonalDetails())
            adapter.addFragment(ProfessionalDetails())

            Handler(Looper.getMainLooper()).postDelayed({
                introViewPager.adapter=adapter
                val array = listOf<String>(getString(R.string.personal_detailsFull), getString(R.string.professional_details))
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
                    tabPosition = position
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                }
            })


            updateData()


        }
    }

    private fun setTabStyle(tabs: TabLayout, txt: String) {
        val av = ArrayList<View?>()
        tabs.findViewsWithText(av, txt, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION)
        if (av.count() > 0) {
            val avt = ArrayList<View?>()
            (av[0] as? ViewGroup)?.let {
                for (i in 0 until it.childCount) {
                    val tv = it.getChildAt(i) as? TextView
                    tv?.let { t ->
                        if (tv.text == txt) {
                            t.isAllCaps = false
                            t.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 6.toFloat())
                        }
                    }

                }

            }
        }
    }
    lateinit var viewer: StfalconImageViewer<String>
    private fun updateData() {
        binding.apply {
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
                    data.membership_validity?.let {
                        inclidePersonalProfile.textValidUptoValue.text = "${data.membership_validity.changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy")}"
                    }
                    MainActivity.mainActivity.get()!!.callBack()
                }
            }
        }
    }


    override fun onCallBack(pos: Int) {
        if (pos == 2){
            if(ProfessionalDetails.callBackListener != null){
                ProfessionalDetails.callBackListener!!.onCallBack(3)
            } else {
                binding.introViewPager.setCurrentItem(1, false)
                Handler(Looper.getMainLooper()).postDelayed({
                    ProfessionalDetails.callBackListener!!.onCallBack(3)
                }, 1000)
            }
        } else if (pos == 4){
              binding.apply {
                    inclideHeaderSearch.textHeaderEditTxt.visibility = View.VISIBLE
                    btSave.visibility = View.GONE
                    btCancel.visibility = View.GONE
                    viewModel.isEditable.value = false
                    updateData()
            }
        }
    }

}