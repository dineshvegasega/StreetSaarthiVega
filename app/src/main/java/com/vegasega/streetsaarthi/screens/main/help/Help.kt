package com.vegasega.streetsaarthi.screens.main.help

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import com.vegasega.streetsaarthi.R
import com.vegasega.streetsaarthi.databinding.HelpBinding
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Help : Fragment() {
    private var _binding: HelpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HelpBinding.inflate(inflater, container, false)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED)
//        } else {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(0)

        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.help)
//            idDataNotFound.textDesc.text = getString(R.string.currently_no_complaints)
            val typeface: Typeface? = ResourcesCompat.getFont(requireContext(), R.font.roboto_medium)
            inclideHeaderSearch.textHeaderTxt.typeface = typeface
            inclideHeaderSearch.btClose.visibility = View.GONE
            inclideHeaderSearch.editTextSearch.visibility = View.GONE

            vBottom.visibility = View.VISIBLE
        }
    }



    override fun onDestroyView() {
//        _binding = null
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onDestroyView()
    }
}