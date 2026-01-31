package com.vegasega.streetsaarthi

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.razorpay.Checkout
import com.vegasega.streetsaarthi.databinding.Main2Binding
import com.vegasega.streetsaarthi.networking.RAZORPAY_KEY
import com.vegasega.streetsaarthi.utils.singleClick
import org.json.JSONObject


class MainActivity : ComponentActivity() {

    private val REQUEST_CODE = 123


    private var _binding: Main2Binding? = null
    val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()
            .build()
        StrictMode.setThreadPolicy(policy)
        _binding = Main2Binding.inflate(layoutInflater)
        setContentView(binding.root)

//            val uri = "upi://pay?pa=paytmqr2810050501011ooqggb29a01@paytm&pn=Paytm%20Merchant&mc=5499&mode=02&orgid=000000&paytmqr=2810050501011OOQGGB29A01&am=1&sign=MEYCIQDq96qhUnqvyLsdgxtfdZ11SQP//6F7f7VGJ0qr//lF/gIhAPgTMsopbn4Y9DiE7AwkQEPPnb2Obx5Fcr0HJghd4gzo"


        val upiPaymentUri =
            Uri.parse("upi://pay?pa=paytmqr281005050101zysmsd3magaq@paytm&pn=PayeeName&tn=PaymentMessage&cu=INR")

//        val upiPaymentUri = Uri.Builder()
//            .scheme("upi")
//            .authority("pay")
//            .appendQueryParameter("pa", "kechamadavipul@okhdfcbank")
//            .appendQueryParameter("pn", "your-merchant-name")
//            .appendQueryParameter("mc", "your-merchant-code")
//            .appendQueryParameter("tr", "your-transaction-ref-id")
//            .appendQueryParameter("tn", "your-transaction-note")
//            .appendQueryParameter("am", "1")
//            .appendQueryParameter("cu", "INR")
//            .build()


        val genericUpiPaymentIntent = Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_VIEW
                data = upiPaymentUri
//                setPackage("com.google.android.apps.nbu.paisa.user")
            },
            "Pay with"
        )


//        binding.apply {
////            btApply.singleClick {
//////                startUpiPayment()
//////                val upiUri = Uri.parse(
//////                    "upi://pay?pa=paytmqr281005050101zysmsd3magaq@paytm&pn=Pankaj&mc=0000" +
//////                            "&tid=909009121&tr=909009121&tn=Payment%20for%20Order" +
//////                            "&am=1&cu=INR"
//////                )
////
////                val uri = "upi://pay?pa=paytmqr2810050501011ooqggb29a01@paytm&pn=Paytm%20Merchant&mc=5499&mode=02&orgid=000000&paytmqr=2810050501011OOQGGB29A01&am=11&sign=MEYCIQDq96qhUnqvyLsdgxtfdZ11SQP//6F7f7VGJ0qr//lF/gIhAPgTMsopbn4Y9DiE7AwkQEPPnb2Obx5Fcr0HJghd4gzo"
////
////                val upiUri = Uri.parse(uri)
////
//////                val upiUri = Uri.parse(
//////                    "upi://pay?pa=paytmqr281005050101zysmsd3magaq@paytm&pn=UserName&am=1&cu=INR&tn=Project Funding&mode=02"
////////                    "upi://pay?pa=paytmqr281005050101zysmsd3magaq@paytm&pn=Receiver%20Name&mc=0000" +
////////                            "&tid=1234567890&tr=9876543210&tn=Payment%20for%20Order" +
////////                            "&am=1&cu=INR"
//////                )
////
//////                val upiUri = Uri.parse(
//////                    "upi://pay?pa=8750713101@ptsbi&pn=Receiver%20Name&mc=0000" +
//////                            "&tid=1234567890&tr=9876543210&tn=Payment%20for%20Order" +
//////                            "&am=1&cu=INR"
//////                )
////
//////                val uri =
//////                    Uri.Builder()
//////                        .scheme("upi")
//////                        .authority("pay")
//////                        .appendQueryParameter("pa", "8750713101@ptsbi")
//////                        .appendQueryParameter("pn", "your-merchant-name")
//////                        .appendQueryParameter("mc", "your-merchant-code")
//////                        .appendQueryParameter("tr", "your-transaction-ref-id")
//////                        .appendQueryParameter("tn", "your-transaction-note")
//////                        .appendQueryParameter("am", "1.00")
//////                        .appendQueryParameter("cu", "INR")
//////                        .build()
//////
//////
////////// Sign the uri as specified
////////                val signature: String? = someSignatureFunction(uri)
////////
////////                uri.buildUpon()
////////                    .appendQueryParameter("sign", signature)
//////
////                val intent = Intent(Intent.ACTION_VIEW).apply {
////                    data = upiUri
////                }
////
////// Show all UPI apps that can handle the intent
////                val chooser = Intent.createChooser(intent, "Pay with")
////
////// Verify that there are apps available
////                if (intent.resolveActivity(packageManager) != null) {
////                    startActivityForResult(chooser, 101)
////                } else {
////                    Toast.makeText(this@MainActivity, "No UPI app found!", Toast.LENGTH_SHORT).show()
////                }
////
//////                activityResultLauncher.launch(genericUpiPaymentIntent)
////
////
////
////            }
//
//
//
//
//            val BHIM_UPI = "in.org.npci.upiapp"
//            val GOOGLE_PAY = "com.google.android.apps.nbu.paisa.user"
//            val PHONE_PE = "com.phonepe.app"
//            val PAYTM = "net.one97.paytm"
//
//            /*1.2 Combining the UPI app package name variables in a list */
//            val upiApps = listOf<String>(GOOGLE_PAY)
//
//            /*2.1 Defining button elements for generic UPI OS intent and specific UPI Apps */
//            var upiButton = findViewById(R.id.btApply) as MaterialButton
////            var paytmButton = findViewById(R.id.paytm) as Button
////            var gpayButton = findViewById(R.id.gpay) as Button
////            var phonepeButton = findViewById(R.id.phonepe) as Button
////            var bhimButton = findViewById(R.id.bhim) as Button
//
//            /*2.2 Combining button elements of specific UPI Apps in a list in the same order as the above upiApps list of UPI app package names */
//            val upiAppButtons = listOf<MaterialButton>(upiButton)
//
//            /*3. Defining a UPI intent with a Paytm merchant UPI spec deeplink */
//            val uri = "upi://pay?pa=paytmqr2810050501011ooqggb29a01@paytm&pn=Paytm%20Merchant&mc=5499&mode=02&orgid=000000&paytmqr=2810050501011OOQGGB29A01&am=1&sign=MEYCIQDq96qhUnqvyLsdgxtfdZ11SQP//6F7f7VGJ0qr//lF/gIhAPgTMsopbn4Y9DiE7AwkQEPPnb2Obx5Fcr0HJghd4gzo"
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
//            intent.data = Uri.parse(uri)
//
//            /*4.1 Defining an on click action for the UPI generic OS intent chooser. This is just for reference, not needed in case of UPI Smart Intent.
//                - This will display a list of all apps available to respond to the UPI intent
//                in a chooser tray by the Android OS */
//            upiButton.setOnClickListener{
//                val chooser = Intent.createChooser(intent, "Pay with...")
//                startActivityForResult(chooser, REQUEST_CODE)
//            }
//
//            for(i in upiApps.indices){
//                val b = upiAppButtons[i]
//                val p = upiApps[i]
//                Log.d("UpiAppVisibility", p + " | " + isAppInstalled(p).toString() + " | " + isAppUpiReady(p))
//                if(isAppInstalled(p)&&isAppUpiReady(p)) {
//                    b.visibility = View.VISIBLE
//                    b.setOnClickListener{
//                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
//                        intent.data = Uri.parse(uri)
//                        intent.setPackage(p)
//                        startActivityForResult(intent, REQUEST_CODE)
//                    }
//                }
//                else{
//                    b.visibility = View.INVISIBLE
//                }
//            }
//
//        }


        binding.apply {
            btApply.singleClick {
//                activityResultLauncher.launch(genericUpiPaymentIntent)

//                try {
//                    var paytmIntent = Intent(Intent.ACTION_VIEW);
//                    var bundle = Bundle()
//                    bundle.putDouble("nativeSdkForMerchantAmount", 1.0);
//                    bundle.putString("orderid", "daasff");
//                    bundle.putString("txnToken", "txnToken");
//                    bundle.putString("mid", "paytmqr281005050101zysmsd3magaq@paytm");
//                    paytmIntent.setComponent(
//                        ComponentName(
//                            "net.one97.paytm",
//                            "net.one97.paytm.AJRJarvisSplash"
//                        )
//                    )
//                    paytmIntent.putExtra("paymentmode", 2);
//                    paytmIntent.putExtra("bill", bundle);
//                    startActivityForResult(paytmIntent, 12);
//                }catch (e : Exception){
//
//                    }


//                val checkout = Checkout()
//                    .upiTurbo(this@MainActivity)
//                checkout.upiTurbo?.linkNewUpiAccount("9988397522", "#000000", object: Any() {
//
//                })
//                checkout.setKeyID(RAZORPAY_KEY)

                val checkout = Checkout()
                checkout.setKeyID(RAZORPAY_KEY)
                try {
                    val options = JSONObject()
                    options.put("name", "Your Company Name")
                    options.put("description", "Payment for your order")
                    options.put("image", "https://example.com/your_logo.png")
                    options.put("currency", "INR")
                    options.put("amount", "100") // Amount in paise (e.g., 10000 for ₹100)
                    options.put(
                        "prefill",
                        JSONObject().put("email", "customer@example.com")
                            .put("contact", "9999999999")
                    )

//                    // To enable only UPI
//                    val config = JSONObject()
//                    config.put("display", JSONObject().put("payment_methods", "upi"))
//                    options.put("config", config)

                    val method = JSONObject()
                    method.put("upi", true)
                    method.put("card", false)
                    method.put("wallet", false)
                    method.put("netbanking", false)
                    method.put("paylater", false)
                    options.put("method", method)

                    checkout.open(this@MainActivity, options)
                } catch (e: java.lang.Exception) {
                    Log.e("TAG", "Error in starting Razorpay Checkout", e)
                }




//                val co = Checkout()
//                co.setKeyID(RAZORPAY_KEY)
//                try {
////                Log.e(
////                    "TAG",
////                    "totalXXXCC: " + gstTotalPrice
////                )
//                    val total: Double =
//                        12.00 * 100
//                    Log.e(
//                        "TAG",
//                        "totalXXXDD: " + total
//                    )
//
////                                                            val sss = 130361.0 * 100
////                                                            Log.e("TAG", "totalXXXEE: "+sss)
//                    val totalX =
//                        total.toInt()
//
//                    val options =
//                        JSONObject()
//                    options.put(
//                        "name",
//                        resources.getString(
//                            R.string.app_name
//                        )
//                    )
////                                                            options.put("name","Razorpay Corp")
//                    options.put(
//                        "description",
//                        "6 months subs"
//                    )
//                    options.put(
//                        "image",
//                        R.drawable.main_logo
//                    )
//                    options.put(
//                        "currency",
//                        "INR"
//                    )
//                    options.put(
//                        "amount",
//                        "" + totalX
//                    )
//                    options.put(
//                        "send_sms_hash",
//                        true
//                    )
//                    options.put(
//                        "readOnlyName",
//                        "com.klifora.shop"
//                    )
//                    options.put(
//                        "orderId",
//                        ""
//                    )
//
////                    val prefill =
////                        JSONObject()
//////                                                    prefill.put("email", "test@razorpay.com")
//////                                                    prefill.put("contact", "9988397522")
////                    prefill.put(
////                        "name",
////                        "data.vendor_first_name"
////                    )
//////                prefill.put(
//////                    "email",
//////                    data.vendor_first_name
//////                )
////                    prefill.put(
////                        "contact",
////                        "ata.mobile_no"
////                    )
////                    options.put(
////                        "prefill",
////                        prefill
////                    )
//
//                    val config = JSONObject()
//                    config.put("display", JSONObject().put("payment_methods", "upi"))
//                    options.put("config", config)
//
//
//                    co.open(
//                        this@MainActivity,
//                        options
//                    )
//                } catch (e: Exception) {
////                                                        onPaymentError 0 ::: undefined ::: com.razorpay.PaymentData@dc1cb00
//                    Toast.makeText(
//                        this@MainActivity,
//                        "Error in payment: " + e.message,
//                        Toast.LENGTH_LONG
//                    ).show()
//                    e.printStackTrace()
//                }

            }
        }


    }


    fun isAppInstalled(packageName: String): Boolean {
        val pm = getPackageManager()
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return false;
    }

    /*
        This function checks if the app with this package name is responding to UPI intent
        - i.e. the app has a ready UPI user (as per the NPCI recommended implementation)
        - Circular: https://www.npci.org.in/sites/default/files/circular/Circular-73-Payer_App_behaviour_for_Intent_based_transaction_on_UPI.pdf
    */
    fun isAppUpiReady(packageName: String): Boolean {
        var appUpiReady = false
        val upiIntent = Intent(Intent.ACTION_VIEW, Uri.parse("upi://pay"))
        val pm = getPackageManager()
        val upiActivities: List<ResolveInfo> = pm.queryIntentActivities(upiIntent, 0)
        for (a in upiActivities) {
            if (a.activityInfo.packageName == packageName) appUpiReady = true
        }
        return appUpiReady
    }


    //Activity
    val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val status = it.data?.getStringExtra("Status")
            Toast.makeText(this, status, Toast.LENGTH_LONG).show()
        }
//
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        Toast.makeText(this, "Payment AAAAAAAA", Toast.LENGTH_SHORT).show()
//        if (requestCode == 101) {
//            if (data != null) {
//                val response = data.getStringExtra("response")
//                if (response != null) {
//                    // Example response: txnId=XXXXXXXX&Status=SUCCESS&txnRef=YYYYYY
//                    if (response.contains("SUCCESS")) {
//                        Log.e("TAG", "sssssss"+response.toString())
//                        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            } else {
//                Toast.makeText(this, "Cancelled or Failed", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//
//
//// ... inside your Activity class
//
//    private val UPI_PAYMENT_REQUEST_CODE = 123
//
//    fun startUpiPayment() {
//        val payeeVpa = "receiver@upi"
//        val payeeName = "Your Merchant Name"
//        val transactionId = "your_unique_transaction_id" // from your server
//        val transactionRefId = "your_unique_reference_id" // from your server
//        val amount = "1.00" // in decimal format
//        val transactionNote = "Payment for order #12345"
//
//        try {
//            val uri = Uri.Builder()
//                .scheme("upi")
//                .authority("pay")
//                .appendQueryParameter("pa", payeeVpa)
//                .appendQueryParameter("pn", payeeName)
//                .appendQueryParameter("tr", transactionRefId)
//                .appendQueryParameter("am", amount)
//                .appendQueryParameter("cu", "INR")
//                .appendQueryParameter("tn", transactionNote)
//                .build()
//
//            val upiIntent = Intent(Intent.ACTION_VIEW)
//            upiIntent.data = uri
//
//            // Create a chooser intent to show a list of all UPI apps
//            val chooser = Intent.createChooser(upiIntent, "Pay with...")
//
//            // Check if there is any app to handle the intent
//            if (chooser.resolveActivity(packageManager) != null) {
//                startActivityForResult(chooser, UPI_PAYMENT_REQUEST_CODE)
//            } else {
//                Toast.makeText(this, "No UPI app found. Please install one to proceed.", Toast.LENGTH_SHORT).show()
//            }
//
//        } catch (e: Exception) {
//            // Handle any exceptions, like a malformed URI
//            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
//        }
//    }
}

