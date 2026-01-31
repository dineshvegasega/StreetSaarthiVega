package com.vegasega.streetsaarthi.screens.onboarding.register

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.vegasega.streetsaarthi.R
import com.vegasega.streetsaarthi.databinding.Register1Binding
import com.vegasega.streetsaarthi.screens.interfaces.CallBackListener
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity.Companion.networkFailed
import com.vegasega.streetsaarthi.networking.IS_LANGUAGE_ALL
import com.vegasega.streetsaarthi.utils.callNetworkDialog
import com.vegasega.streetsaarthi.utils.callPermissionDialog
import com.vegasega.streetsaarthi.utils.getCameraPath
import com.vegasega.streetsaarthi.utils.getMediaFilePathFor
import com.vegasega.streetsaarthi.utils.showDropDownDialog
import com.vegasega.streetsaarthi.utils.showOptions
import com.vegasega.streetsaarthi.utils.showSnackBar
import com.vegasega.streetsaarthi.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class Register1 : Fragment(), CallBackListener {
    private var _binding: Register1Binding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterVM by activityViewModels()


    companion object {
        var callBackListener: CallBackListener? = null
    }


    var imagePosition = 0
    private var pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            try {
                lifecycleScope.launch {
                    if (uri != null) {
                        when (imagePosition) {
                            1 -> {
                                val compressedImageFile = Compressor.compress(
                                    requireContext(),
                                    File(requireContext().getMediaFilePathFor(uri))
                                )
                                viewModel.data.passportSizeImage = compressedImageFile.path
                                binding.textViewPassportSizeImage.setText(File(viewModel.data.passportSizeImage!!).name)
                            }

                            2 -> {
                                val compressedImageFile = Compressor.compress(
                                    requireContext(),
                                    File(requireContext().getMediaFilePathFor(uri))
                                )
                                viewModel.data.identificationImage = compressedImageFile.path
                                binding.textViewIdentificationImage.setText(File(viewModel.data.identificationImage!!).name)
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
                        1 -> {
                            val compressedImageFile = Compressor.compress(
                                requireContext(),
                                File(requireContext().getMediaFilePathFor(uriReal!!))
                            )
                            viewModel.data.passportSizeImage = compressedImageFile.path
                            binding.textViewPassportSizeImage.setText(compressedImageFile.name)
                        }

                        2 -> {
                            val compressedImageFile = Compressor.compress(
                                requireContext(),
                                File(requireContext().getMediaFilePathFor(uriReal!!))
                            )
                            viewModel.data.identificationImage = compressedImageFile.path
                            binding.textViewIdentificationImage.setText(compressedImageFile.name)
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
        _binding = Register1Binding.inflate(inflater)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callBackListener = this



        binding.apply {
            editTextGender.singleClick {
                requireActivity().showDropDownDialog(type = 1) {
                    binding.editTextGender.setText(name)
                    when (position) {
                        0 -> viewModel.data.gender = "Male"
                        1 -> viewModel.data.gender = "Female"
                        2 -> viewModel.data.gender = "Other"
                    }
                }
            }

            editTextDateofBirth.singleClick {
                requireActivity().showDropDownDialog(type = 2){
                    binding.editTextDateofBirth.setText(name)
                }
            }

            editTextSocialCategory.singleClick {
                requireActivity().showDropDownDialog(type = 3){
                    binding.editTextSocialCategory.setText(name)
                }
            }

            editTextEducationQualifacation.singleClick {
                requireActivity().showDropDownDialog(type = 4){
                    binding.editTextEducationQualifacation.setText(name)
                    when (position) {
                        0 -> viewModel.data.education_qualification = "No Education"
                        1 -> viewModel.data.education_qualification = "Primary Education(1st To 5th)"
                        2 -> viewModel.data.education_qualification = "Middle Education(6th To 9th)"
                        3 -> viewModel.data.education_qualification = "Higher Education(10th To 12th)"
                        4 -> viewModel.data.education_qualification = "Graduation"
                        5 -> viewModel.data.education_qualification = "Post Graduation"
                    }
                }
            }

            editTextMaritalStatus.singleClick {
                requireActivity().showDropDownDialog(type = 5){
                    binding.editTextMaritalStatus.setText(name)
                    if (name == getString(R.string.married)) {
                        binding.editTextSpouseName.visibility = View.VISIBLE
                    } else {
                        binding.editTextSpouseName.visibility = View.GONE
                    }
                    when (position) {
                        0 -> viewModel.data.marital_status = "Single"
                        1 -> viewModel.data.marital_status = "Married"
                        2 -> viewModel.data.marital_status = "Widowed"
                        3 -> viewModel.data.marital_status = "Divorced"
                        4 -> viewModel.data.marital_status = "Separated"
                    }
                }
            }

            if (networkFailed) {
                viewModel.state(view)
            } else {
                requireContext().callNetworkDialog()
            }

            editTextSelectState.singleClick {
                if (viewModel.itemState.size > 0) {
                    var index = 0
                    val list = arrayOfNulls<String>(viewModel.itemState.size)
                    for (value in viewModel.itemState) {
                        list[index] = value.name
                        index++
                    }
                    requireActivity().showDropDownDialog(type = 6, arrayList = list){
                        binding.editTextSelectState.setText(name)
                        viewModel.stateId = viewModel.itemState[position].id
                        if (networkFailed) {
                            view?.let { viewModel.district(it, viewModel.stateId) }
                            if (!IS_LANGUAGE_ALL) {
                                view?.let { viewModel.panchayat(it, viewModel.stateId) }
                            }
                        } else {
                            requireContext().callNetworkDialog()
                        }
                        binding.editTextSelectDistrict.setText("")
                        binding.editTextMunicipalityPanchayat.setText("")
                        viewModel.districtId = 0
                        viewModel.panchayatId = 0
                    }
                } else {
                    showSnackBar(getString(R.string.not_state))
                }
            }

            editTextSelectDistrict.singleClick {
                if (!(viewModel.stateId > 0)) {
                    showSnackBar(getString(R.string.select_state_))
                } else {
                    if (viewModel.itemDistrict.size > 0) {
                        var index = 0
                        val list = arrayOfNulls<String>(viewModel.itemDistrict.size)
                        for (value in viewModel.itemDistrict) {
                            list[index] = value.name
                            index++
                        }
                        requireActivity().showDropDownDialog(type = 7, arrayList = list){
                            binding.editTextSelectDistrict.setText(name)
                            viewModel.districtId = viewModel.itemDistrict[position].id
                            if (networkFailed) {
                                view?.let { viewModel.pincode(it, viewModel.districtId) }
                            } else {
                                requireContext().callNetworkDialog()
                            }
                            binding.editTextSelectPincode.setText("")
                            viewModel.pincodeId = ""
                        }
                    } else {
                        showSnackBar(getString(R.string.not_district))
                    }
                }
            }

            editTextMunicipalityPanchayat.singleClick {
                if (!(viewModel.stateId > 0)) {
                    showSnackBar(getString(R.string.select_state_))
                } else {
                    if (viewModel.itemPanchayat.size > 0) {
                        var index = 0
                        val list = arrayOfNulls<String>(viewModel.itemPanchayat.size)
                        for (value in viewModel.itemPanchayat) {
                            list[index] = value.name
                            index++
                        }
                        requireActivity().showDropDownDialog(type = 8, arrayList = list){
                            binding.editTextMunicipalityPanchayat.setText(name)
                            viewModel.panchayatId = viewModel.itemPanchayat[position].id
                        }
                    } else {
                        showSnackBar(getString(R.string.not_municipality_panchayat))
                    }
                }
            }

            editTextSelectPincode.singleClick {
                if (!(viewModel.districtId > 0)) {
                    showSnackBar(getString(R.string.select_district_))
                } else {
                    if (viewModel.itemPincode.size > 0) {
                        var index = 0
                        val list = arrayOfNulls<String>(viewModel.itemPincode.size)
                        for (value in viewModel.itemPincode) {
                            list[index] = value.pincode
                            index++
                        }
                        requireActivity().showDropDownDialog(type = 9, arrayList = list){
                            binding.editTextSelectPincode.setText(name)
                            viewModel.pincodeId = binding.editTextSelectPincode.text.toString()
                        }
                    } else {
                        showSnackBar(getString(R.string.not_pincode))
                    }
                }
            }

            layoutPassportSizeImage.singleClick {
                imagePosition = 1
                callMediaPermissions()
            }

            layoutIdentificationImage.singleClick {
                imagePosition = 2
                callMediaPermissions()
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
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            try {
                if (!permissions.entries.toString().contains("false")) {
                    requireActivity().showOptions {
                        when (this) {
                            1 -> forCamera()
                            2 -> forGallery()
                        }
                    }
                } else {
                    requireActivity().callPermissionDialog {
                        someActivityResultLauncher.launch(this)
                    }
                }
            }catch (e : Exception){

            }
        }


    var someActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            callMediaPermissions()
        }catch (e : Exception){

        }
    }




    private fun forCamera() {
        try {
            requireActivity().getCameraPath {
                uriReal = this
                captureMedia.launch(uriReal)
            }
        }catch (e : Exception){

        }
    }

    private fun forGallery() {
        try {
            requireActivity().runOnUiThread() {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }catch (e : Exception){

        }
    }


    override fun onCallBack(pos: Int) {
        binding.apply {
//            Register.callBackListener!!.onCallBack(2)
            if (pos == 1) {
                if (editTextFN.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.first_name))
                } else if (editTextLN.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.last_name))
                } else if (editTextFatherFN.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.father_s_first_name))
                } else if (editTextFatherLN.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.father_s_last_name))
                } else if (editTextGender.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.gender))
                } else if (editTextDateofBirth.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.date_of_birth))
                } else if (editTextSocialCategory.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.social_category))
                } else if (editTextEducationQualifacation.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.education_qualifacation))
                } else if (editTextMaritalStatus.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.marital_status))
                } else if (editTextMaritalStatus.text.toString() == getString(R.string.married) && editTextSpouseName.text.toString()
                        .isEmpty()
                ) {
                    showSnackBar(getString(R.string.spouse_s_name))
                } else {
                    if (!(viewModel.stateId > 0)) {
                        showSnackBar(getString(R.string.select_state))
                    } else if (!(viewModel.districtId > 0)) {
                        showSnackBar(getString(R.string.select_district))
                    } else if (!(viewModel.panchayatId > 0)) {
                        showSnackBar(getString(R.string.municipality_panchayat))
                    } else if (editTextAddress.text.toString().isEmpty()) {
                        showSnackBar(getString(R.string.address_mention_village))
                    } else if (viewModel.data.passportSizeImage == null) {
                        showSnackBar(getString(R.string.passport_size_image))
                    } else if (viewModel.data.identificationImage == null) {
                        showSnackBar(getString(R.string.identification_image))
                    } else {
                        viewModel.data.vendor_first_name = editTextFN.text.toString()
                        viewModel.data.vendor_last_name = editTextLN.text.toString()
                        viewModel.data.parent_first_name = editTextFatherFN.text.toString()
                        viewModel.data.parent_last_name = editTextFatherLN.text.toString()
                        viewModel.data.date_of_birth = editTextDateofBirth.text.toString()
                        viewModel.data.social_category = editTextSocialCategory.text.toString()
                        viewModel.data.spouse_name = editTextSpouseName.text.toString()
                        viewModel.data.current_state = "" + viewModel.stateId
                        viewModel.data.current_district = "" + viewModel.districtId
                        viewModel.data.municipality_panchayat_current = "" + viewModel.panchayatId
                        viewModel.data.current_pincode = "" + viewModel.pincodeId
                        viewModel.currentAddress = editTextAddress.text.toString()
                        viewModel.data.current_address = "" + viewModel.currentAddress
                        Register.callBackListener!!.onCallBack(2)
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