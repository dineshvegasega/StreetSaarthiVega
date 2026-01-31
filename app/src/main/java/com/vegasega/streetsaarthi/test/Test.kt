package com.vegasega.streetsaarthi.test

import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.vegasega.streetsaarthi.databinding.InvoiceViewBinding

class Test : AppCompatActivity() {
    private var _binding: InvoiceViewBinding? = null
    val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = InvoiceViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
//
//        setContentView(R.layout.invoice_view)

        binding.apply {
            textWords.setText(Html.fromHtml("<p><b><font color=#0173B7>Amount In Words: </font><font color=#000000>Ten Lakhs Seventy Eight Thousands Nine Rupees</font></b></p>", Html.FROM_HTML_MODE_COMPACT));
        }
//        Html.fromHtml("<p><font color=#666666>I agree to</font><font color=#0173B7>  <b><u>Terms & Conditions</u></b></font><font color=#666666> and the <u></font><b><font color=#0173B7>Privacy Policy</font></u></b></font></p>")

    }
}