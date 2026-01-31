package com.vegasega.streetsaarthi.screens.onboarding.register

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.vegasega.streetsaarthi.R
import com.vegasega.streetsaarthi.databinding.Register2Binding
import com.vegasega.streetsaarthi.datastore.DataStoreKeys.LOGIN_DATA
import com.vegasega.streetsaarthi.datastore.DataStoreUtil.readData
import com.vegasega.streetsaarthi.models.Login
import com.vegasega.streetsaarthi.screens.interfaces.CallBackListener
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity.Companion.networkFailed
import com.vegasega.streetsaarthi.networking.*
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity
import com.vegasega.streetsaarthi.utils.callNetworkDialog
import com.vegasega.streetsaarthi.utils.callPermissionDialog
import com.vegasega.streetsaarthi.utils.callPermissionDialogGPS
import com.vegasega.streetsaarthi.utils.getAddress
import com.vegasega.streetsaarthi.utils.getCameraPath
import com.vegasega.streetsaarthi.utils.getImageName
import com.vegasega.streetsaarthi.utils.getMediaFilePathFor
import com.vegasega.streetsaarthi.utils.isLocationEnabled
import com.vegasega.streetsaarthi.utils.loadImage
import com.vegasega.streetsaarthi.utils.loadImageForms
import com.vegasega.streetsaarthi.utils.mainThread
import com.vegasega.streetsaarthi.utils.showDropDownDialog
import com.vegasega.streetsaarthi.utils.showOptions
import com.vegasega.streetsaarthi.utils.showSnackBar
import com.vegasega.streetsaarthi.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


@AndroidEntryPoint
class Register2  : Fragment() , CallBackListener {

    private var _binding: Register2Binding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterVM by activityViewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    var imagePath : String = ""

    var scrollPoistion = 0

    companion object {
        var TAG = "Register2"
        var callBackListener: CallBackListener? = null
    }



    var imagePosition = 0
    private var pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            try {
                lifecycleScope.launch {
                    if (uri != null) {
                        when (imagePosition) {
                            11 -> {
                                val compressedImageFile = Compressor.compress(requireContext(), File(requireContext().getMediaFilePathFor(uri)))
                                viewModel.data.shopImage = compressedImageFile.path
                                binding.textViewlayoutShopImage.setText(File(viewModel.data.shopImage!!).name)
                            }

                            1 -> {
                                val compressedImageFile = Compressor.compress(requireContext(), File(requireContext().getMediaFilePathFor(uri)))
                                viewModel.data.ImageUploadCOV = compressedImageFile.path
                                binding.inclideDocuments.textViewImageUploadCOV.setText(File(viewModel.data.ImageUploadCOV!!).name)
                            }

                            2 -> {
                                val compressedImageFile = Compressor.compress(requireContext(), File(requireContext().getMediaFilePathFor(uri)))
                                viewModel.data.ImageUploadLOR = compressedImageFile.path
                                binding.inclideDocuments.textViewImageUploadLOR.setText(File(viewModel.data.ImageUploadLOR!!).name)
                            }

                            3 -> {
                                val compressedImageFile = Compressor.compress(requireContext(), File(requireContext().getMediaFilePathFor(uri)))
                                viewModel.data.UploadSurveyReceipt = compressedImageFile.path
                                binding.inclideDocuments.textViewUploadSurveyReceipt.setText(File(viewModel.data.UploadSurveyReceipt!!).name)
                            }

                            4 -> {
                                val compressedImageFile = Compressor.compress(requireContext(), File(requireContext().getMediaFilePathFor(uri)))
                                viewModel.data.UploadChallan = compressedImageFile.path
                                binding.inclideDocuments.textViewUploadChallan.setText(File(viewModel.data.UploadChallan!!).name)
                            }

                            5 -> {
                                val compressedImageFile = Compressor.compress(requireContext(), File(requireContext().getMediaFilePathFor(uri)))
                                viewModel.data.UploadApprovalLetter = compressedImageFile.path
                                binding.inclideDocuments.textViewUploadApprovalLetter.setText(File(viewModel.data.UploadApprovalLetter!!).name)
                            }
                        }
                    }
                }
            }catch (e : Exception){

            }


        }



    var uriReal: Uri? = null
    val captureMedia = registerForActivityResult(ActivityResultContracts.TakePicture()) { uri ->
        try {
            lifecycleScope.launch {
                if (uri == true) {
                    when (imagePosition) {
                        11 -> {
                            val compressedImageFile = Compressor.compress(requireContext(), File(requireContext().getMediaFilePathFor(uriReal!!)))
                            viewModel.data.shopImage = compressedImageFile.path
                            binding.textViewlayoutShopImage.setText(compressedImageFile.name)
                        }

                        1 -> {
                            val compressedImageFile = Compressor.compress(requireContext(), File(requireContext().getMediaFilePathFor(uriReal!!)))
                            viewModel.data.ImageUploadCOV = compressedImageFile.path
                            binding.inclideDocuments.textViewImageUploadCOV.setText(compressedImageFile.name)
                        }

                        2 -> {
                            val compressedImageFile = Compressor.compress(requireContext(), File(requireContext().getMediaFilePathFor(uriReal!!)))
                            viewModel.data.ImageUploadLOR = compressedImageFile.path
                            binding.inclideDocuments.textViewImageUploadLOR.setText(compressedImageFile.name)
                        }

                        3 -> {
                            val compressedImageFile = Compressor.compress(requireContext(), File(requireContext().getMediaFilePathFor(uriReal!!)))
                            viewModel.data.UploadSurveyReceipt = compressedImageFile.path
                            binding.inclideDocuments.textViewUploadSurveyReceipt.setText(compressedImageFile.name)
                        }

                        4 -> {
                            val compressedImageFile = Compressor.compress(requireContext(), File(requireContext().getMediaFilePathFor(uriReal!!)))
                            viewModel.data.UploadChallan = compressedImageFile.path
                            binding.inclideDocuments.textViewUploadChallan.setText(compressedImageFile.name)
                        }

                        5 -> {
                            val compressedImageFile = Compressor.compress(requireContext(), File(requireContext().getMediaFilePathFor(uriReal!!)))
                            viewModel.data.UploadApprovalLetter = compressedImageFile.path
                            binding.inclideDocuments.textViewUploadApprovalLetter.setText(compressedImageFile.name)
                        }
                    }
                }
            }
        }catch (e : Exception){

        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Register2Binding.inflate(inflater)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callBackListener = this
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        binding.apply {
            if(networkFailed) {
                viewModel.vending(view)
                viewModel.marketplace(view)
            } else {
                requireContext().callNetworkDialog()
            }


            editTextTypeofMarketPlace.singleClick {
                var index = 0
                val list = arrayOfNulls<String>(viewModel.itemMarketplace.size)
                for (value in viewModel.itemMarketplace) {
                    list[index] = value.name
                    index++
                }
                requireActivity().showDropDownDialog(type = 10, arrayList = list){
                    binding.editTextTypeofMarketPlace.setText(name)
                    viewModel.marketplaceId = viewModel.itemMarketplace[position].marketplace_id
                    if (viewModel.marketplaceId == 7) {
                        binding.editTextTypeofMarketPlaceEnter.visibility = View.VISIBLE
                    } else {
                        binding.editTextTypeofMarketPlaceEnter.visibility = View.GONE
                    }
                }
            }

            editTextTypeofVending.singleClick {
                var index = 0
                val list = arrayOfNulls<String>(viewModel.itemVending.size)
                for (value in viewModel.itemVending) {
                    list[index] = value.name
                    index++
                }
                requireActivity().showDropDownDialog(type = 11, arrayList = list){
                    binding.editTextTypeofVending.setText(name)
                    viewModel.vendingId = viewModel.itemVending[position].vending_id
                    if (viewModel.vendingId == 11) {
                        binding.editTextTypeofVendingEnter.visibility = View.VISIBLE
                    } else {
                        binding.editTextTypeofVendingEnter.visibility = View.GONE
                    }
                }
            }

            editTextTotalYearsofVending.singleClick {
                requireActivity().showDropDownDialog(type = 12){
                    binding.editTextTotalYearsofVending.setText(name)
                }
            }

            editTextVendingTimeOpen.singleClick {
                requireActivity().showDropDownDialog(type = 14){
                    binding.editTextVendingTimeOpen.setText(name)
                    viewModel.data.open = title
                }
            }
            editTextVendingTimeClose.singleClick {
                requireActivity().showDropDownDialog(type = 14){
                    binding.editTextVendingTimeClose.setText(name)
                    viewModel.data.close = title
                }
            }

            if(networkFailed) {
                viewModel.stateCurrent(view)
            } else {
                requireContext().callNetworkDialog()
            }

            editTextSelectState.singleClick {
                if(viewModel.itemStateVending.size > 0){
                    var index = 0
                    val list = arrayOfNulls<String>(viewModel.itemStateVending.size)
                    for (value in viewModel.itemStateVending) {
                        list[index] = value.name
                        index++
                    }
                    requireActivity().showDropDownDialog(type = 6, arrayList = list){
                        binding.editTextSelectState.setText(name)
                        viewModel.stateIdVending = viewModel.itemStateVending[position].id
                        if(networkFailed) {
                            view?.let { viewModel.districtCurrent(it, viewModel.stateIdVending) }
                            if (!IS_LANGUAGE_ALL){
                                view?.let { viewModel.panchayatCurrent(it, viewModel.stateIdVending) }
                            }
                        } else {
                            requireContext().callNetworkDialog()
                        }

                        if(viewModel.stateIdVending != 0 && viewModel.districtIdVending != 0){
                            if(networkFailed) {
                                view?.let { viewModel.localOrganisation(it, JSONObject().apply {
                                    put(state_id, viewModel.stateIdVending)
                                    put(district_id, viewModel.districtIdVending)
                                })}
                            } else {
                                requireContext().callNetworkDialog()
                            }
                        }
                        binding.editTextSelectDistrict.setText("")
                        binding.editTextMunicipalityPanchayat.setText("")
                        viewModel.districtIdVending = 0
                        viewModel.panchayatIdVending = 0
                    }
                } else {
                    showSnackBar(getString(R.string.not_state))
                }
            }

            editTextSelectDistrict.singleClick {
                if (!(viewModel.stateIdVending > 0)){
                    showSnackBar(getString(R.string.select_state_))
                }else{
                    if(viewModel.itemDistrictVending.size > 0){
                        var index = 0
                        val list = arrayOfNulls<String>(viewModel.itemDistrictVending.size)
                        for (value in viewModel.itemDistrictVending) {
                            list[index] = value.name
                            index++
                        }
                        requireActivity().showDropDownDialog(type = 7, arrayList = list){
                            binding.editTextSelectDistrict.setText(name)
                            viewModel.districtIdVending = viewModel.itemDistrictVending[position].id
                            view?.let { viewModel.pincodeCurrent(it, viewModel.districtIdVending) }
                            if(viewModel.stateIdVending != 0 && viewModel.districtIdVending != 0){
                                if(networkFailed) {
                                    view?.let { viewModel.localOrganisation(it, JSONObject().apply {
                                        put(state_id, viewModel.stateIdVending)
                                        put(district_id, viewModel.districtIdVending)
                                    })}
                                } else {
                                    requireContext().callNetworkDialog()
                                }
                            }

                            binding.editTextSelectPincode.setText("")
                            viewModel.pincodeIdVending = ""
                        }
                    } else {
                        showSnackBar(getString(R.string.not_district))
                    }
                }
            }

            editTextMunicipalityPanchayat.singleClick {
                if (!(viewModel.stateIdVending > 0)){
                    showSnackBar(getString(R.string.select_state_))
                }else{
                    if(viewModel.itemPanchayatVending.size > 0){
                        var index = 0
                        val list = arrayOfNulls<String>(viewModel.itemPanchayatVending.size)
                        for (value in viewModel.itemPanchayatVending) {
                            list[index] = value.name
                            index++
                        }
                        requireActivity().showDropDownDialog(type = 8, arrayList = list){
                            binding.editTextMunicipalityPanchayat.setText(name)
                            viewModel.panchayatIdVending = viewModel.itemPanchayatVending[position].id
                        }
                    } else {
                        showSnackBar(getString(R.string.not_municipality_panchayat))
                    }
                }
            }

            editTextSelectPincode.singleClick {
                if (!(viewModel.districtIdVending > 0)){
                    showSnackBar(getString(R.string.select_district_))
                } else {
                    if(viewModel.itemPincodeVending.size > 0){
                        var index = 0
                        val list = arrayOfNulls<String>(viewModel.itemPincodeVending.size)
                        for (value in viewModel.itemPincodeVending) {
                            list[index] = value.pincode
                            index++
                        }
                        requireActivity().showDropDownDialog(type = 9, arrayList = list){
                            binding.editTextSelectPincode.setText(name)
                            viewModel.pincodeIdVending = binding.editTextSelectPincode.text.toString()
                        }
                    } else {
                        showSnackBar(getString(R.string.not_pincode))
                    }
                }
            }


            ivRdLocalOrgnaizationYes.singleClick {
                editTextLocalOrganisation.visibility = View.VISIBLE
                setScrollPosition(1, true)
            }

            ivRdLocalOrgnaizationNo.singleClick {
                editTextLocalOrganisation.visibility = View.GONE
                setScrollPosition(1, false)
            }


            editTextLocalOrganisation.singleClick {
                if (viewModel.itemLocalOrganizationVending.size > 0){
                    var index = 0
                    val list = arrayOfNulls<String>(viewModel.itemLocalOrganizationVending.size)
                    for (value in viewModel.itemLocalOrganizationVending) {
                        list[index] = value.local_organisation_name
                        index++
                    }
                    requireActivity().showDropDownDialog(type = 13, arrayList = list){
                        binding.editTextLocalOrganisation.setText(name)
                        viewModel.localOrganizationIdVending = viewModel.itemLocalOrganizationVending[position].id
                    }
                } else {
                    showSnackBar(getString(R.string.not_Organisation))
                }
            }


            layoutShopImage.singleClick {
                imagePosition = 11
                callMediaPermissionsShop()
            }


            ivRdDocumentYes.singleClick {
                viewModel.documentDetails = true
                inclideDocuments.layoutDocuments.visibility = View.VISIBLE
                setScrollPosition(1, true)
            }
            ivRdDocumentNo.singleClick {
                viewModel.documentDetails = false
                inclideDocuments.layoutDocuments.visibility = View.GONE
                setScrollPosition(1, false)
            }

            inclideDocuments.layoutImageUploadCOV.singleClick {
                imagePosition = 1
                callMediaPermissions()
            }
            inclideDocuments.layoutImageUploadLOR.singleClick {
                imagePosition = 2
                callMediaPermissions()
            }
            inclideDocuments.layoutUploadSurveyReceipt.singleClick {
                imagePosition = 3
                callMediaPermissions()
            }
            inclideDocuments.layoutUploadChallan.singleClick {
                imagePosition = 4
                callMediaPermissions()
            }
            inclideDocuments.layoutUploadApprovalLetter.singleClick {
                imagePosition = 5
                callMediaPermissions()
            }



            binding.scroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                    scrollPoistion = scrollY
            })
            Handler(Looper.getMainLooper()).postDelayed({
                inclideGovernment.layoutGovernmentScheme.visibility = View.GONE
                cbClick.visibility = View.VISIBLE
            }, 100)

            btApplyPM.singleClick {
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.pmsvanidhi.mohua.gov.in/")
                )
                try {
                    requireActivity().startActivity(webIntent)
                } catch (ex: ActivityNotFoundException) {
                }
            }

            ivRdGovernmentYes.singleClick {
                viewModel.governmentScheme = true
                inclideGovernment.layoutGovernmentScheme.visibility = View.VISIBLE
                cbClick.visibility = View.GONE
                setScrollPosition(2, true)
            }

            ivRdGovernmentNo.singleClick {
                viewModel.governmentScheme = false
                inclideGovernment.layoutGovernmentScheme.visibility = View.GONE
                cbClick.visibility = View.VISIBLE
                setScrollPosition(2, false)
            }
        }
    }


    private fun setScrollPosition(type : Int, ifTrue: Boolean) {
        when(type){
            1 -> {
                ObjectAnimator.ofInt(binding.scroll, "scrollY",  scrollPoistion).setDuration(100).start()
            }

            2 -> {
                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    val itemHeight =
                        binding.inclideGovernment.layoutGovernmentScheme.height
                    binding.scroll.smoothScrollTo(0,scrollPoistion * itemHeight)
                }, 50)
            }
        }
    }




    private fun callMediaPermissions() {
        try {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    activityResultLauncher.launch(
                        arrayOf(
                            Manifest.permission.CAMERA)
                    )
                } else{
                    activityResultLauncher.launch(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    )
                }
        }catch (e : Exception){

        }
    }



    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            try {
                if(!permissions.entries.toString().contains("false")){
                    requireActivity().showOptions {
                        when(this){
                            1 -> forCamera()
                            2 -> forGallery()
                        }
                    }
                } else {
                    requireActivity().callPermissionDialog{
                        someActivityResultLauncher.launch(this)
                    }
                }
            }catch (e : Exception){

            }
        }



    var someActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        callMediaPermissions()
    }




    private fun forCamera() {
        requireActivity().getCameraPath {
            uriReal = this
            captureMedia.launch(uriReal)
        }
    }

    private fun forGallery() {
        requireActivity().runOnUiThread() {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }







    private fun callMediaPermissionsShop() {
        try {
            if (isLocationEnabled(requireActivity())) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    activityResultLauncherShop.launch(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                } else{
                    activityResultLauncherShop.launch(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                    )
                }
            } else {
                requireActivity().callPermissionDialogGPS {
                    someActivityResultLauncherWithLocationGPS.launch(this)
                }
            }

        }catch (e : Exception){

        }

    }


    private val activityResultLauncherShop =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            try {
                if (!permissions.entries.toString().contains("false")) {
                    requireActivity().showOptions {
                        when(this){
                            1 -> forCameraShop()
                            2 -> forGalleryShop()
                        }
                    }
                } else {
                    requireActivity().callPermissionDialog{
                        someActivityResultLauncherShop.launch(this)
                    }
                }
            }catch (e : Exception){

            }
        }


    var someActivityResultLauncherShop = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            callMediaPermissionsShop()
        }catch (e : Exception){

        }
    }


    var someActivityResultLauncherWithLocationGPS = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            Log.e("TAG", "result.resultCode "+result.resultCode)
            callMediaPermissionsShop()
        } catch (e : Exception){

        }
    }


    @SuppressLint("MissingPermission")
    private var pickMediaShop =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            try {
                lifecycleScope.launch @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) {
                    if (uri != null) {
                        val compressedImageFile = Compressor.compress(
                            requireContext(),
                            File(requireContext().getMediaFilePathFor(uri))
                        )

                        var isLocation = false
                        val locationRequest = LocationRequest().setInterval(1000).setFastestInterval(1000)
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        fusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            object : LocationCallback() {
                                override fun onLocationResult(locationResult: LocationResult) {
                                    super.onLocationResult(locationResult)
                                    for (location in locationResult.locations) {
                                        if (!isLocation) {
                                            isLocation = true
                                            imagePath = compressedImageFile.path
                                            var latLong = LatLng(location!!.latitude, location.longitude)
                                            Log.e("TAG", "addOnSuccessListener111111 " + latLong.toString())
//                                            readData(LOGIN_DATA) { loginUser ->
//                                                Log.e("TAG", "addOnSuccessListener111111loginUser " + loginUser.toString())
//                                                if (loginUser != null) {
//                                                    val data = Gson().fromJson(loginUser, Login::class.java)
                                                    binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
                                                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                                    val currentDate = sdf.format(Date())

                                                    binding.textClickByTxt.text = getString(R.string.geoClickby) + " "+ viewModel.data.vendor_first_name +" "+viewModel.data.vendor_last_name

                                                    binding.textTimeTxt.text = getString(R.string.geoDateTime) + " "+ currentDate
                                                    binding.textLatLongTxt.text = getString(R.string.geoLatLng) + " "+ latLong.latitude+","+latLong.longitude

                                                    mainThread {
                                                        binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong)
                                                        dispatchTakePictureIntent(binding.layoutMainCapture) {
                                                            viewModel.data.shopImage = this
                                                            Log.e("TAG", "viewModel.foodItemImageAAAAAAA " + this)
//                                                            binding.ivImageShopImage.loadImage(type = 1, url = { this })
                                                            binding.textViewlayoutShopImage.setText(File(viewModel.data.shopImage!!).name)
                                                        }
                                                    }
//                                                }
//                                            }
                                        }
                                    }
                                }
                            },
                            Looper.myLooper()
                        )
                    }
                }
            }catch (e : Exception){

            }
        }


    var uriRealShop: Uri? = null
    @SuppressLint("MissingPermission")
    val captureMediaShop = registerForActivityResult(ActivityResultContracts.TakePicture()) { uri ->
        try {
            lifecycleScope.launch @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) {
                if (uri != null) {
                    val compressedImageFile = Compressor.compress(
                        requireContext(),
                        File(requireContext().getMediaFilePathFor(uriRealShop!!))
                    )

                    var isLocation = false
                    val locationRequest = LocationRequest().setInterval(1000).setFastestInterval(1000)
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult) {
                                super.onLocationResult(locationResult)
                                for (location in locationResult.locations) {
                                    if (!isLocation) {
                                        isLocation = true
                                        imagePath = compressedImageFile.path
                                        var latLong = LatLng(location!!.latitude, location.longitude)
                                        Log.e("TAG", "addOnSuccessListener111111 " + latLong.toString())
//                                            readData(LOGIN_DATA) { loginUser ->
//                                                Log.e("TAG", "addOnSuccessListener111111loginUser " + loginUser.toString())
//                                                if (loginUser != null) {
//                                                    val data = Gson().fromJson(loginUser, Login::class.java)
                                        binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
                                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                        val currentDate = sdf.format(Date())

                                        binding.textClickByTxt.text = getString(R.string.geoClickby) + " "+ viewModel.data.vendor_first_name +" "+viewModel.data.vendor_last_name

                                        binding.textTimeTxt.text = getString(R.string.geoDateTime) + " "+ currentDate
                                        binding.textLatLongTxt.text = getString(R.string.geoLatLng) + " "+ latLong.latitude+","+latLong.longitude

                                        mainThread {
                                            binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong)
                                            dispatchTakePictureIntent(binding.layoutMainCapture) {
                                                viewModel.data.shopImage = this
                                                Log.e("TAG", "viewModel.foodItemImageAAAAAAA " + this)
//                                                            binding.ivImageShopImage.loadImage(type = 1, url = { this })
                                                binding.textViewlayoutShopImage.setText(File(viewModel.data.shopImage!!).name)
                                            }
                                        }
//                                                }
//                                            }
                                    }
                                }
                            }
                        },
                        Looper.myLooper()
                    )
                }
            }
        }catch (e : Exception){

        }

    }









//
//    private var pickMediaShop =
//        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
//            try {
//                lifecycleScope.launch {
//                    if (uri != null) {
//                        val compressedImageFile = Compressor.compress(requireContext(), File(requireContext().getMediaFilePathFor(uri)))
//                        viewModel.data.shopImage = compressedImageFile.path
//                        binding.textViewlayoutShopImage.setText(File(viewModel.data.shopImage!!).name)
//                    }
//
//                }
//            }catch (e : Exception){
//
//            }
//
//
//        }
//
//
//
//    var uriRealShop: Uri? = null
//    val captureMediaShop = registerForActivityResult(ActivityResultContracts.TakePicture()) { uri ->
//        try {
//            lifecycleScope.launch {
//                if (uri == true) {
//                    val compressedImageFile = Compressor.compress(requireContext(), File(requireContext().getMediaFilePathFor(uriReal!!)))
//                    viewModel.data.shopImage = compressedImageFile.path
//                    binding.textViewlayoutShopImage.setText(compressedImageFile.name)
//                }
//            }
//        }catch (e : Exception){
//
//        }
//    }





    private fun forCameraShop() {
        try {
            requireActivity().getCameraPath {
                uriRealShop = this
                captureMediaShop.launch(uriRealShop)
            }
        }catch (e : Exception){

        }
    }

    private fun forGalleryShop() {
        try {
            requireActivity().runOnUiThread() {
                pickMediaShop.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }catch (e : Exception){

        }
    }






    private fun dispatchTakePictureIntent(imageView: View, callBack: String.() -> Unit) {
        val bitmap: Bitmap = getBitmapFromView(imageView)
//        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        // val filename = System.currentTimeMillis().toString() + "." + "png" // change png/pdf
        val path = requireActivity().externalCacheDir ?: Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(path, getImageName())
        try {
            if (!path.exists()) path.mkdirs()
            if (!file.exists()) file.createNewFile()
            val ostream: FileOutputStream = FileOutputStream(file)
            bitmap.compress(CompressFormat.PNG, 10, ostream)
            ostream.close()
            val data = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", file)
            }else{
                val imagePath: File = File(file.absolutePath)
                FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", imagePath)
            }
//            Log.e("TAG", "viewModel.foodSignature "+viewModel.foodSignature)
            callBack(file.toString())

        } catch (e: IOException) {
            Log.w("ExternalStorage", "Error writing $file", e)
        }
    }

    fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width, view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


    override fun onCallBack(pos: Int) {
        binding.apply {
            if (pos == 3) {
                if (editTextTypeofMarketPlace.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.type_of_market_place))
                } else if (viewModel.marketplaceId == 7 && editTextTypeofMarketPlaceEnter.text.toString()
                        .isEmpty()
                ) {
                    showSnackBar(getString(R.string.enter_market_place))
                } else if (editTextTypeofVending.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.type_of_vending))
                } else if (viewModel.vendingId == 11 && editTextTypeofVendingEnter.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.enter_vending))
                } else if (editTextTotalYearsofVending.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.total_years_of_vending))
                } else if (editTextVendingTimeOpen.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.open_time))
                } else if (editTextVendingTimeClose.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.close_time))
                } else if (!(viewModel.stateIdVending > 0)) {
                    showSnackBar(getString(R.string.select_state))
                } else if (!(viewModel.districtIdVending > 0)) {
                    showSnackBar(getString(R.string.select_district))
                } else if (!(viewModel.panchayatIdVending > 0)) {
                    showSnackBar(getString(R.string.municipality_panchayat))
                } else if (editTextAddress.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.address_mention_village))
                } else if (binding.ivRdLocalOrgnaizationYes.isChecked == true && editTextLocalOrganisation.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.localOrganisation))
                } else if (viewModel.data.shopImage == null) {
                    showSnackBar(getString(R.string.upload_shop_image))
                } else {
                    val schemeName = StringBuffer()
                    if(binding.ivRdGovernmentYes.isChecked == true && binding.ivRdGovernmentNo.isChecked == false){
                        //Log.e(TAG, "schemeNameAA ")
                        viewModel.data.governmentScheme = binding.ivRdGovernmentYes.isChecked
                        if(binding.inclideGovernment.cbRememberPMSwanidhiScheme.isChecked == true || binding.inclideGovernment.cbRememberOthersPleaseName.isChecked == true){
                            if(binding.inclideGovernment.cbRememberOthersPleaseName.isChecked == true && binding.inclideGovernment.editTextSchemeName.text.toString().isEmpty()){
                                showSnackBar(getString(R.string.scheme_name))
                            }else{
                                if(binding.inclideGovernment.cbRememberPMSwanidhiScheme.isChecked == true){
                                    schemeName.append(getString(R.string.pm_swanidhi_schemeSingle)+" ")
                                }
                                if(binding.inclideGovernment.cbRememberOthersPleaseName.isChecked == true){
                                    schemeName.append(binding.inclideGovernment.editTextSchemeName.text.toString())
                                }
                                viewModel.data.schemeName = schemeName.toString()
                                setAddData()
                            }
                        } else {
                            showSnackBar(getString(R.string.select_scheme))
                        }
                    } else if(binding.ivRdGovernmentYes.isChecked == false && binding.ivRdGovernmentNo.isChecked == true){
                        viewModel.data.governmentScheme = binding.ivRdGovernmentYes.isChecked
                        viewModel.data.schemeName = ""
                        setAddData()
                    }
                }
            }
        }
    }

    private fun setAddData() {
        binding.apply {
            viewModel.data.type_of_marketplace = viewModel.marketplaceId.toString()
            viewModel.data.marketpalce_others = editTextTypeofMarketPlaceEnter.text.toString()
            viewModel.data.type_of_vending = viewModel.vendingId.toString()
            viewModel.data.vending_others = editTextTypeofVendingEnter.text.toString()

            viewModel.data.total_years_of_business = editTextTotalYearsofVending.text.toString()
            viewModel.data.vending_state = ""+viewModel.stateIdVending
            viewModel.data.vending_district = ""+viewModel.districtIdVending
            viewModel.data.vending_municipality_panchayat = ""+viewModel.panchayatIdVending
            viewModel.data.vending_pincode = ""+viewModel.pincodeIdVending
            viewModel.data.vending_address = ""+editTextAddress.text.toString()
            viewModel.data.localOrganisation = ""+viewModel.localOrganizationIdVending

            viewModel.data.documentDetails = viewModel.documentDetails
            viewModel.data.ImageUploadCOVBoolean = binding.inclideDocuments.cbRememberImageUploadCOV.isChecked
            viewModel.data.ImageUploadLORBoolean = binding.inclideDocuments.cbRememberImageUploadLOR.isChecked
            viewModel.data.UploadSurveyReceiptBoolean = binding.inclideDocuments.cbRememberUploadSurveyReceipt.isChecked
            viewModel.data.UploadChallanBoolean = binding.inclideDocuments.cbRememberUploadChallan.isChecked
            viewModel.data.UploadApprovalLetterBoolean = binding.inclideDocuments.cbRememberUploadApprovalLetter.isChecked

            viewModel.data.governmentScheme = viewModel.governmentScheme
            viewModel.data.pmSwanidhiScheme = binding.inclideGovernment.cbRememberPMSwanidhiScheme.isChecked
            viewModel.data.otherScheme = binding.inclideGovernment.cbRememberOthersPleaseName.isChecked

            Register.callBackListener!!.onCallBack(4)
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}