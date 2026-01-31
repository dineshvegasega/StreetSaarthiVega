package com.vegasega.streetsaarthi.screens.onboarding.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vegasega.streetsaarthi.R
import com.vegasega.streetsaarthi.databinding.SplashBinding
import com.vegasega.streetsaarthi.datastore.DataStoreKeys.LOGIN_DATA
import com.vegasega.streetsaarthi.datastore.DataStoreKeys.TOKEN
import com.vegasega.streetsaarthi.datastore.DataStoreUtil.readData
import com.vegasega.streetsaarthi.screens.main.dashboard.Dashboard
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity.Companion.navHostFragment
import com.vegasega.streetsaarthi.screens.onboarding.start.Start
import com.vegasega.streetsaarthi.utils.ioThread
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlin.collections.get
import kotlin.text.get


@AndroidEntryPoint
class Splash : Fragment() {
    private var _binding: SplashBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SplashBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(0)
    }


    override fun onResume() {
        super.onResume()
        handleSplashTime()
    }

    private fun handleSplashTime() {
        ioThread {
            delay(2000)
            readData(LOGIN_DATA) { loginUser ->
                val fragmentInFrame = navHostFragment!!.getChildFragmentManager().getFragments().get(0)
                if(loginUser == null){
                    if (fragmentInFrame !is Start){
                        navHostFragment?.navController?.navigate(R.id.action_splash_to_start)
                        MainActivity.mainActivity.get()!!.callBack()
                    }
                }else{
                    if (fragmentInFrame !is Dashboard){
                        if(!MainActivity.isBackStack){
                            navHostFragment?.navController?.navigate(R.id.action_splash_to_dashboard)
                        }
                        MainActivity.mainActivity.get()!!.callBack()
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}