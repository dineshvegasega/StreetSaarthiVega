package com.vegasega.streetsaarthi.screens.onboarding.registerSuccessful

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.vegasega.streetsaarthi.R
import com.vegasega.streetsaarthi.databinding.RegisterSuccessfulBinding
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity
import com.vegasega.streetsaarthi.utils.firstCharIfItIsLowercase
import com.vegasega.streetsaarthi.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterSuccessful : Fragment() {
    private var _binding: RegisterSuccessfulBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = RegisterSuccessfulBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(0)
        binding.apply {
            btSignIn.singleClick {
                requireView().findNavController().navigate(R.id.action_registerSuccessful_to_SubscriptionRegister,
                    Bundle().apply {
                        putString("vendor_id", arguments?.getString("vendor_id") ?: "")
                    })
            }


            btBack.singleClick {
                view.findNavController().navigateUp()
            }

//
//            if (arguments?.getString("from") == "quickRegistration"){
//                val vendor_id = arguments?.getString("vendor_id") ?: ""
//            }
//
//
//            if (arguments?.getString("from") == "fullRegistration"){
//                val vendor_id = arguments?.getString("vendor_id") ?: ""
//            }



            val text =
                "<font color="+ ResourcesCompat.getColor(resources, R.color.black, null)+">"+resources.getString(R.string.congratulations)+"</font> <br/>" +
                        "<font color="+ ResourcesCompat.getColor(resources, R.color._E79D46, null)+">"+arguments?.getString("key")?.firstCharIfItIsLowercase()+"</font>"+
                        "<font color="+ ResourcesCompat.getColor(resources, R.color.black, null)+">,</font> "+
                        "<font color="+ ResourcesCompat.getColor(resources, R.color.black, null)+">"+resources.getString(R.string.your_initial_registration_is_complete)+"</font>"
                        textHeaderTxt312.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)

        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}