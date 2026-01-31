package com.vegasega.streetsaarthi.screens.mainActivity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
//import com.google.android.material.snackbar.Snackbar
//import com.google.android.play.core.appupdate.AppUpdateManager
//import com.google.android.play.core.appupdate.AppUpdateManagerFactory
//import com.google.android.play.core.common.IntentSenderForResultStarter
//import com.google.android.play.core.install.InstallStateUpdatedListener
//import com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED
//import com.google.android.play.core.install.model.AppUpdateType
//import com.google.android.play.core.install.model.InstallStatus
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.ExternalWalletListener
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import com.vegasega.streetsaarthi.R
import com.vegasega.streetsaarthi.databinding.MainActivityBinding
import com.vegasega.streetsaarthi.datastore.DataStoreKeys
import com.vegasega.streetsaarthi.datastore.DataStoreKeys.LOGIN_DATA
import com.vegasega.streetsaarthi.datastore.DataStoreUtil
import com.vegasega.streetsaarthi.datastore.DataStoreUtil.readData
import com.vegasega.streetsaarthi.datastore.DataStoreUtil.saveData
import com.vegasega.streetsaarthi.models.Login
import com.vegasega.streetsaarthi.networking.ConnectivityManager
import com.vegasega.streetsaarthi.networking.*
import com.vegasega.streetsaarthi.screens.main.subscription.Subscription
import com.vegasega.streetsaarthi.screens.main.subscription.ViewManage
import com.vegasega.streetsaarthi.screens.onboarding.subscriptionRegister.SubscriptionRegister
import com.vegasega.streetsaarthi.utils.LocaleHelper
import com.vegasega.streetsaarthi.utils.autoScroll
import com.vegasega.streetsaarthi.utils.callNetworkDialog
import com.vegasega.streetsaarthi.utils.getDensityName
import com.vegasega.streetsaarthi.utils.getSignature
import com.vegasega.streetsaarthi.utils.getToken
import com.vegasega.streetsaarthi.utils.imageZoom
import com.vegasega.streetsaarthi.utils.ioThread
import com.vegasega.streetsaarthi.utils.loadImage
import com.vegasega.streetsaarthi.utils.showSnackBar
import com.vegasega.streetsaarthi.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import okhttp3.MultipartBody
import java.lang.ref.WeakReference

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PaymentResultWithDataListener,
    ExternalWalletListener {

    companion object {
        @JvmStatic
        var PACKAGE_NAME = ""

        @JvmStatic
        var SIGNATURE_NAME = ""

        @JvmStatic
        var isBackApp = false

        @JvmStatic
        var isBackStack = false

        @JvmStatic
        lateinit var context: WeakReference<Context>

        @JvmStatic
        lateinit var activity: WeakReference<Activity>

        @JvmStatic
        lateinit var mainActivity: WeakReference<MainActivity>

        var logoutAlert: AlertDialog? = null
        var deleteAlert: AlertDialog? = null

        @SuppressLint("StaticFieldLeak")
        var navHostFragment: NavHostFragment? = null

        private var _binding: MainActivityBinding? = null
        val binding get() = _binding!!

        @JvmStatic
        lateinit var isOpen: WeakReference<Boolean>

        @JvmStatic
        var scale10: Int = 0

        @JvmStatic
        var fontSize: Float = 0f

        @JvmStatic
        var networkFailed: Boolean = false
    }

    private val viewModel: MainActivityVM by viewModels()

    private val connectivityManager by lazy { ConnectivityManager(this) }
//    private lateinit var appUpdateManager: AppUpdateManager

    private val pushNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
//            Log.e("TAG", "AAAAgranted " + granted)

        } else {
//            Log.e("TAG", "BBBBgranted " + granted)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("SdCardPath", "MutableImplicitPendingIntent", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        val policy = ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()
            .build()
        StrictMode.setThreadPolicy(policy)
        super.onCreate(savedInstanceState)
        _binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        context = WeakReference(this)
        activity = WeakReference(this)
        mainActivity = WeakReference(this)
//        appUpdateManager = AppUpdateManagerFactory.create(this)

//        val appLinkIntent: Intent = intent
//        val appLinkAction: String? = appLinkIntent.action
//        val appLinkData: Uri? = appLinkIntent.data
//
//        Log.e("TAG", "appLinkIntent "+appLinkIntent)
//        Log.e("TAG", "appLinkAction "+appLinkAction)
//        Log.e("TAG", "appLinkData "+appLinkData)

        PACKAGE_NAME = packageName
        SIGNATURE_NAME = getSignature()
        Checkout.preload(applicationContext)

        if(LocaleHelper.getLanguage(applicationContext) == "ur"){
            binding.root.layoutDirection = View.LAYOUT_DIRECTION_RTL
        } else {
            binding.root.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }

//        checkUpdate()




        if (Build.VERSION.SDK_INT >= 33) {
            pushNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }



        window.decorView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            window.decorView.getWindowVisibleDisplayFrame(r)
            val height = window.decorView.height
            if (height - r.bottom > height * 0.1399) {
                isOpen = WeakReference(true)
            } else {
                isOpen = WeakReference(false)
            }
        }




        loadBanner()
        viewModel.itemAds.observe(this@MainActivity, Observer {
            if (it != null) {
                viewModel.itemAds.value?.let { it1 ->
                    binding.apply {
                        viewModel.bannerAdapter.submitData(it1)
                        banner.adapter = viewModel.bannerAdapter
                        tabDots.setupWithViewPager(banner, true)

                        banner.autoScroll()
                        when (screenValue) {
                            0 -> layoutBanner.visibility = View.GONE
                            in 1..2 -> layoutBanner.visibility = View.VISIBLE
                        }
                    }
                }
            }
        })


        observeConnectivityManager()



        getToken(){
            Log.e("TAG", "getToken "+this)
            saveData(DataStoreKeys.TOKEN, this)
        }



        val bundle = intent?.extras
        if (bundle != null) {
            showData(bundle)
        }

        binding.apply {
            val manager = packageManager
            val info = manager?.getPackageInfo(packageName, 0)
            val versionName = info?.versionName
            textVersion.text = getString(R.string.app_version_1_0, versionName)
            btLogout.singleClick {
                callLogoutDialog()
            }


            val mDrawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
                this@MainActivity, drawerLayout,
                R.string.open, R.string.close
            ) {
                override fun onDrawerClosed(view: View) {
                    super.onDrawerClosed(view)
                }

                override fun onDrawerOpened(drawerView: View) {
                    super.onDrawerOpened(drawerView)
                }
            }
            drawerLayout.addDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();

            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

            textTitleMain.singleClick {
                drawerLayout.close()
            }

            topLayout.ivMenu.singleClick {
                drawerLayout.open()
            }

            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = viewModel.menuAdapter
            viewModel.menuAdapter.submitList(viewModel.itemMain)
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        }




        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val backStackEntryCount = navHostFragment?.childFragmentManager?.backStackEntryCount
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.close()
                } else {
                    if (backStackEntryCount == 0) {
                        val setIntent = Intent(Intent.ACTION_MAIN)
                        setIntent.addCategory(Intent.CATEGORY_HOME)
                        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(setIntent)
                    } else {
                        if (isBackStack) {
                            isBackStack = false
                            val navOptions: NavOptions = NavOptions.Builder()
                                .setPopUpTo(R.id.navigation_bar, true)
                                .build()
                            runOnUiThread {
                                navHostFragment?.navController?.navigate(
                                    R.id.dashboard,
                                    null,
                                    navOptions
                                )
                            }
                        } else {
                            navHostFragment?.navController?.navigateUp()
                        }
                    }
                }
            }
        })
    }


    @SuppressLint("SuspiciousIndentation")
    fun callLogoutDialog() {
        if (logoutAlert?.isShowing == true) {
            return
        }

        logoutAlert = MaterialAlertDialogBuilder(this, R.style.LogoutDialogTheme)
            .setTitle(resources.getString(R.string.app_name))
            .setMessage(resources.getString(R.string.are_your_sure_want_to_logout))
            .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                dialog.dismiss()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.drawerLayout.close()
                }, 500)
                readData(LOGIN_DATA) { loginUser ->
                    if (loginUser != null) {
                        val requestBody: MultipartBody.Builder = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                        requestBody.addFormDataPart(
                            mobile_number,
                            "" + Gson().fromJson(loginUser, Login::class.java).mobile_no
                        )
                        if (networkFailed) {
                            viewModel.logoutAccount(requestBody.build())
                        } else {
                            callNetworkDialog()
                        }
                    }
                }
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.drawerLayout.close()
                }, 500)
            }
            .setCancelable(false)
            .show()


        viewModel.itemLogoutResult.value = false
        viewModel.itemLogoutResult.observe(this@MainActivity, Observer {
            if (it) {
                clearData()
            }
        })
    }


    @SuppressLint("SuspiciousIndentation")
    fun callDeleteDialog() {
        if (deleteAlert?.isShowing == true) {
            return
        }
        deleteAlert = MaterialAlertDialogBuilder(this, R.style.LogoutDialogTheme)
            .setTitle(resources.getString(R.string.app_name))
            .setMessage(resources.getString(R.string.are_your_sure_want_to_delete))
            .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                dialog.dismiss()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.drawerLayout.close()
                }, 500)
                readData(LOGIN_DATA) { loginUser ->
                    if (loginUser != null) {
                        val requestBody: MultipartBody.Builder = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart(user_type, USER_TYPE)
                        requestBody.addFormDataPart(
                            user_id,
                            "" + Gson().fromJson(loginUser, Login::class.java).id
                        )
                        requestBody.addFormDataPart(delete_account, "Yes")
                        if (networkFailed) {
                            viewModel.deleteAccount(requestBody.build())
                        } else {
                            callNetworkDialog()
                        }
                    }
                }
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.drawerLayout.close()
                }, 500)
            }
            .setCancelable(false)
            .show()

        viewModel.itemDeleteResult.value = false
        viewModel.itemDeleteResult.observe(this@MainActivity, Observer {
            if (it) {
                showSnackBar(getString(R.string.request_delete))
                clearData()
            }
        })
    }


    @SuppressLint("SuspiciousIndentation")
    fun callSesstionDialog() {
        if (deleteAlert?.isShowing == true) {
            return
        }
        deleteAlert = MaterialAlertDialogBuilder(this, R.style.LogoutDialogTheme)
            .setTitle(resources.getString(R.string.app_name))
            .setMessage(resources.getString(R.string.are_your_sure_want_to_delete))
            .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                dialog.dismiss()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.drawerLayout.close()
                }, 500)
                readData(LOGIN_DATA) { loginUser ->
                    if (loginUser != null) {
                        val requestBody: MultipartBody.Builder = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart(user_type, USER_TYPE)
                        requestBody.addFormDataPart(
                            user_id,
                            "" + Gson().fromJson(loginUser, Login::class.java).id
                        )
                        requestBody.addFormDataPart(delete_account, "Yes")
                        if (networkFailed) {
                            viewModel.deleteAccount(requestBody.build())
                        } else {
                            callNetworkDialog()
                        }
                    }
                }
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.drawerLayout.close()
                }, 500)
            }
            .setCancelable(false)
            .show()

        viewModel.itemDeleteResult.value = false
        viewModel.itemDeleteResult.observe(this@MainActivity, Observer {
            if (it) {
                showSnackBar(getString(R.string.request_delete))
                clearData()
            }
        })
    }


    fun clearData() {
        DataStoreUtil.removeKey(DataStoreKeys.LOGIN_DATA) {}
        DataStoreUtil.removeKey(DataStoreKeys.AUTH) {}
        DataStoreUtil.removeKey(DataStoreKeys.LIVE_SCHEME_DATA) {}
        DataStoreUtil.removeKey(DataStoreKeys.LIVE_NOTICE_DATA) {}
        DataStoreUtil.removeKey(DataStoreKeys.LIVE_TRAINING_DATA) {}
        DataStoreUtil.removeKey(DataStoreKeys.Complaint_Feedback_DATA) {}
        DataStoreUtil.removeKey(DataStoreKeys.Information_Center_DATA) {}
        DataStoreUtil.clearDataStore { }
        callBack()
        val navOptions: NavOptions = NavOptions.Builder()
            .setPopUpTo(R.id.navigation_bar, true)
            .build()
        runOnUiThread {
            navHostFragment?.navController?.navigate(R.id.onboard, null, navOptions)
        }
    }


    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent?.extras != null) {
            showData(intent?.extras!!)
        }
    }


    private fun showData(bundle: Bundle) {
        try {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.close()
            }
                val navOptions: NavOptions = NavOptions.Builder()
                .setPopUpTo(R.id.navigation_bar, true)
                .build()
            if (intent!!.hasExtra(Screen)) {
                val screen = intent.getStringExtra(Screen)
//                Log.e("TAG", "screenAA " + screen)
                if (screen == Main) {
                    binding.topLayout.topToolbar.visibility = View.VISIBLE
                }
                callBack()
                if (screen == Start) {
                    navHostFragment?.navController?.navigate(R.id.start, null, navOptions)
                } else if (screen == Main) {
                    if (bundle?.getString("key") != null) {
                        callRedirect(bundle)
                    } else {
//                        Log.e("key", "showDataBB ")
//                        Log.e("_id", "showDataBB ")
                        navHostFragment?.navController?.navigate(R.id.dashboard, null, navOptions)
                    }
                }
            } else {
//                Log.e("TAG", "screenBB ")
                if (bundle?.getString("key") != null) {
                    callRedirect(bundle)
                }
            }
        } catch (e: Exception) {
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun callRedirect(bundle: Bundle) {
        val key = bundle?.getString("key")
        val _id = bundle?.getString("_id")
//        Log.e("key", "showDataAA " + key)
//        Log.e("_id", "showDataAA " + _id)

        readData(LOGIN_DATA) { loginUser ->
            if (loginUser != null) {
                val data = Gson().fromJson(loginUser, Login::class.java)
                when (data.status) {
                    "approved" -> {
                        isBackStack = true
                        when (key) {
                            "profile" -> navHostFragment!!.navController.navigate(R.id.profiles)
                            "membership" -> navHostFragment!!.navController.navigate(R.id.subscription)
                            "scheme" -> {
                                when(data.subscription_status) {
                                    null -> navHostFragment!!.navController.navigate(R.id.liveSchemes)
                                    "trial" -> navHostFragment!!.navController.navigate(R.id.liveSchemes)
                                    "active" -> navHostFragment!!.navController.navigate(R.id.liveSchemes)
                                    "expired" -> showSnackBar(resources.getString(R.string.expired_account_message), type = 2, navHostFragment?.navController)
                                }
                            }
                            "notice" -> {
                                when(data.subscription_status) {
                                    null -> navHostFragment!!.navController.navigate(R.id.liveNotices)
                                    "trial" -> navHostFragment!!.navController.navigate(R.id.liveNotices)
                                    "active" -> navHostFragment!!.navController.navigate(R.id.liveNotices)
                                    "expired" -> showSnackBar(resources.getString(R.string.expired_account_message), type = 2, navHostFragment?.navController)
                                }
                            }
                            "training" -> {
                                when(data.subscription_status) {
                                    null -> navHostFragment!!.navController.navigate(R.id.liveTraining)
                                    "trial" -> navHostFragment!!.navController.navigate(R.id.liveTraining)
                                    "active" -> navHostFragment!!.navController.navigate(R.id.liveTraining)
                                    "expired" -> showSnackBar(resources.getString(R.string.expired_account_message), type = 2, navHostFragment?.navController)
                                }
                            }
                            "information" -> {
                                when(data.subscription_status) {
                                    null -> navHostFragment!!.navController.navigate(R.id.informationCenter)
                                    "trial" -> navHostFragment!!.navController.navigate(R.id.informationCenter)
                                    "active" -> navHostFragment!!.navController.navigate(R.id.informationCenter)
                                    "expired" -> showSnackBar(resources.getString(R.string.expired_account_message), type = 2, navHostFragment?.navController)
                                }
                            }

                            "feedback" -> {
                                when(data.subscription_status) {
                                    null -> navHostFragment!!.navController.navigate(
                                        R.id.historyDetail,
                                        Bundle().apply {
                                            putString("key", _id)
                                        })
                                    "trial" -> navHostFragment!!.navController.navigate(
                                        R.id.historyDetail,
                                        Bundle().apply {
                                            putString("key", _id)
                                        })
                                    "active" -> navHostFragment!!.navController.navigate(
                                        R.id.historyDetail,
                                        Bundle().apply {
                                            putString("key", _id)
                                        })
                                    "expired" -> showSnackBar(resources.getString(R.string.expired_account_message), type = 2, navHostFragment?.navController)
                                }
                            }
                        }
                    }

                    "unverified" -> {
                        showSnackBar(resources.getString(R.string.registration_processed))
                    }

                    "pending" -> {
                        if (key == "profile") {
                            isBackStack = true
                            navHostFragment!!.navController.navigate(R.id.profiles)
                        } else {
                            showSnackBar(resources.getString(R.string.registration_processed))
                        }
                    }

                    "rejected" -> {
                        if (key == "profile") {
                            isBackStack = true
                            navHostFragment!!.navController.navigate(R.id.profiles)
                        } else {
                            showSnackBar(resources.getString(R.string.registration_processed))
                        }
                    }
                }
            }
        }


//        else -> {
//            isBackStack = true
//            val status = bundle?.getString("status")
//            Log.e("TAG", "statusAZ "+status)
//            when (status) {
//                "profile" -> {
//                    navHostFragment!!.navController.navigate(R.id.profiles)
//                }
//                else -> showSnackBar(resources.getString(R.string.registration_processed))
//            }
//        }

    }


    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, ""))
    }


    fun callBack() {
        binding.apply {

//            readData(IS_LOGIN) { IS_LOGINUser ->
//                if (IS_LOGINUser != null || IS_LOGINUser != ""){
//                    if (IS_LOGINUser == "1"){
                        readData(LOGIN_DATA) { loginUser ->
                            if (loginUser == null) {
                                topLayout.topToolbar.visibility = View.GONE
                                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                                layoutBanner.visibility = View.GONE
                                textHeaderTxt1.visibility = View.GONE
                            } else {
                                topLayout.topToolbar.visibility = View.VISIBLE
                                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

                                var imageUrl = ""
                                try {
                                    val imageUrlLogin = Gson().fromJson(loginUser, Login::class.java)
                                    if (imageUrlLogin != null) {
                                        val imageUrlName = imageUrlLogin.profile_image_name
                                        imageUrl = imageUrlName.url ?: ""
                                    }
                                } catch (_: Exception) {
                                }
                                topLayout.ivImage.loadImage(type = 1, url = { imageUrl })
                                lateinit var viewer: StfalconImageViewer<String>
                                topLayout.ivImage.singleClick {
//                        arrayListOf(imageUrl).imageZoom(topLayout.ivImage, 2)
                                    viewer = StfalconImageViewer.Builder<String>(binding.root.context, arrayListOf(imageUrl)) { view, image ->
                                        Picasso.get().load(image).into(view)
                                    }.withImageChangeListener {
                                        viewer.updateTransitionImage(topLayout.ivImage)
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
//                        }
//                    } else {
//                        topLayout.topToolbar.visibility = View.GONE
//                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//                        layoutBanner.visibility = View.GONE
//                        textHeaderTxt1.visibility = View.GONE
//                    }
//                } else {
//                    topLayout.topToolbar.visibility = View.GONE
//                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//                    layoutBanner.visibility = View.GONE
//                    textHeaderTxt1.visibility = View.GONE
//                }
            }


            loadBanner()
        }
    }


    var screenValue = 0
    fun callFragment(screen: Int) {
        screenValue = screen
        binding.apply {
            when (screen) {
                0 -> {
                    textHeaderTxt1.visibility = View.GONE
                    layoutBanner.visibility = View.GONE
                }

                1 -> {
                    textHeaderTxt1.visibility = View.VISIBLE
                    mainLayout.setBackgroundResource(R.color.white)
                    viewModel.itemAds.value?.let { it1 ->
                        if (it1.size > 0) {
                            if (screen == 1) {
                                layoutBanner.visibility = View.VISIBLE
                            } else {
                                layoutBanner.visibility = View.GONE
                            }
                        } else {
                            layoutBanner.visibility = View.GONE
                        }
                    }
                }

                2 -> {
                    textHeaderTxt1.visibility = View.VISIBLE
                    mainLayout.setBackgroundResource(R.color._FFF3E4)
                    viewModel.itemAds.value?.let { it1 ->
                        if (it1.size > 0) {
                            if (screen == 2) {
                                layoutBanner.visibility = View.VISIBLE
                            } else {
                                layoutBanner.visibility = View.GONE
                            }
                        } else {
                            layoutBanner.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun observeConnectivityManager() = try {
        connectivityManager.observe(this) {
            binding.tvInternet.isVisible = !it
            networkFailed = it
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }


//    var resultUpdate =
//        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
//            Log.e("TAG", "result.resultCode " + result.resultCode)
//            if (result.resultCode == RESULT_OK) {
//                // Handle successful app update
//                Log.e("TAG", "RESULT_OK")
//            } else if (result.resultCode == RESULT_CANCELED) {
////                finish()
//            } else if (result.resultCode == RESULT_IN_APP_UPDATE_FAILED) {
////                finish()
//            } else {
////                finish()
//            }
//        }


//    private fun checkUpdate() {
//        appUpdateManager
//            .appUpdateInfo
//            .addOnSuccessListener { appUpdateInfo ->
//                val starter =
//                    IntentSenderForResultStarter { intent, _, fillInIntent, flagsMask, flagsValues, _, _ ->
//                        val request = IntentSenderRequest.Builder(intent)
//                            .setFillInIntent(fillInIntent)
//                            .setFlags(flagsValues, flagsMask)
//                            .build()
//                        resultUpdate.launch(request)
//                    }
//
//                appUpdateManager.startUpdateFlowForResult(
//                    appUpdateInfo,
//                    AppUpdateType.IMMEDIATE,
//                    starter,
//                    123,
//                )
//            }
//    }


    fun loadBanner() {
        if (viewModel.itemAds.value == null) {
            readData(LOGIN_DATA) { loginUser ->
                if (loginUser != null) {
                    viewModel.adsList()
                }
            }
        }
    }


    fun reloadActivity(language: String, screen: String) {
        LocaleHelper.setLocale(this, language)
        val refresh = Intent(Intent(this, MainActivity::class.java))
        refresh.putExtra(Screen, screen)
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        finish()
        finishAffinity()
        startActivity(refresh)
    }


    override fun onStart() {
        super.onStart()
        val fontScale = resources.configuration.fontScale
        scale10 = when (fontScale) {
            0.8f -> 13
            0.9f -> 12
            1.0f -> 11
            1.1f -> 10
            1.2f -> 9
            1.3f -> 8
            1.5f -> 7
            1.7f -> 6
            2.0f -> 5
            else -> 4
        }

        val densityDpi = getDensityName()
//        Log.e("TAG", "densityDpiAA " + densityDpi)
        fontSize = when (densityDpi) {
            "xxxhdpi" -> 9f
            "xxhdpi" -> 9.5f
            "xhdpi" -> 10.5f
            "hdpi" -> 10.5f
            "mdpi" -> 11f
            "ldpi" -> 11.5f
            else -> 12f
        }
    }



//    val listener = InstallStateUpdatedListener { state ->
//        if (state.installStatus() == InstallStatus.DOWNLOADING) {
//            val bytesDownloaded = state.bytesDownloaded()
//            val totalBytesToDownload = state.totalBytesToDownload()
//        }
//        if (state.installStatus() == InstallStatus.DOWNLOADED) {
//            Snackbar.make(
//                binding.root,
//                "New app is ready",
//                Snackbar.LENGTH_INDEFINITE
//            ).setAction("Restart") {
//                appUpdateManager.completeUpdate()
//            }.show()
//        }
//    }


    override fun onResume() {
        super.onResume()
        ioThread {
            delay(2500)
            callBack()
        }
//        appUpdateManager?.let {
//            it.registerListener(listener)
//        }
    }

    override fun onDestroy() {
        Log.e("TAG", "onDestroy")
        super.onDestroy()

        logoutAlert?.let {
            logoutAlert!!.cancel()
        }
        deleteAlert?.let {
            deleteAlert!!.cancel()
        }
//        appUpdateManager?.let {
//            it.unregisterListener(listener)
//        }
    }





    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        Log.e("TAG", "onPaymentSuccess "+p0.toString() +" ::: "+p1?.data.toString())
        try{
            val checkoutFragment: Subscription? = Subscription().getCheckoutFragment()
            Log.e("TAG", "checkoutFragment "+checkoutFragment.toString())
            if (checkoutFragment != null) {
                checkoutFragment.onPaymentSuccess(p0, p1)
            }

            val checkoutRegisterFragment: SubscriptionRegister? = SubscriptionRegister().getCheckoutFragment()
            Log.e("TAG", "checkoutFragment "+checkoutFragment.toString())
            if (checkoutRegisterFragment != null) {
                checkoutRegisterFragment.onPaymentSuccess(p0, p1)
            }

        }catch (e: java.lang.Exception){
            e.printStackTrace()
            Log.e("TAG", "onPaymentSuccessBB "+e.message)
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Log.e("TAG", "onPaymentError "+p0.toString() +" ::: "+p1.toString() +" ::: "+p2?.data.toString())
        try{
            val checkoutFragment: Subscription? = Subscription()?.getCheckoutFragment()
            if (checkoutFragment != null) {
                checkoutFragment.onPaymentError(p0, p1, p2)
            }


            val checkoutRegisterFragment: SubscriptionRegister? = SubscriptionRegister()?.getCheckoutFragment()
            if (checkoutRegisterFragment != null) {
                checkoutRegisterFragment.onPaymentError(p0, p1, p2)
            }
        }catch (e: java.lang.Exception){
            e.printStackTrace()
        }
    }

    override fun onExternalWalletSelected(p0: String?, p1: PaymentData?) {
        Log.e("TAG", "onExternalWalletSelected "+p0.toString() +" ::: "+p1?.data.toString())
        try{
            val checkoutFragment: Subscription? = Subscription()?.getCheckoutFragment()
            if (checkoutFragment != null) {
                checkoutFragment.onExternalWalletSelected(p0, p1)
            }


            val checkoutRegisterFragment: SubscriptionRegister? = SubscriptionRegister()?.getCheckoutFragment()
            if (checkoutRegisterFragment != null) {
                checkoutRegisterFragment.onExternalWalletSelected(p0, p1)
            }

        }catch (e: java.lang.Exception){
            e.printStackTrace()
        }
    }




}
