package com.vegasega.streetsaarthi.test;

//import com.google.android.play.core.appupdate.AppUpdateInfo;
//import com.google.android.play.core.appupdate.AppUpdateManager;
//import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
//import com.google.android.play.core.common.IntentSenderForResultStarter;
//import com.google.android.play.core.install.InstallStateUpdatedListener;
//import com.google.android.play.core.install.model.AppUpdateType;
//import com.google.android.play.core.install.model.InstallStatus;
//import com.google.android.play.core.install.model.UpdateAvailability;
//import com.google.android.play.core.tasks.Task;
//import com.google.gson.Gson;
//import com.vegasega.streetsaarthi.R;
//import com.vegasega.streetsaarthi.utils.CheckIsUpdateReady;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.select.Elements;
//
//import java.io.IOException;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class AA extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.test);
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                .detectAll()
//                .penaltyLog()
//                .detectDiskReads()
//                .detectDiskWrites()
//                .detectNetwork()
//                .permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//        //inAppUpdates();
//
//      //  new VersionChecker().execute();
//
//        try {
//
//
////            String packageName = "com.vegasega.streetsaarthi";
////
////            Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id=com.vegasega.streetsaarthi/" + packageName).get();
////            Elements data = doc.select(".reAt0 ");
////
////            if (data.size() > 0) {
////                System.out.println("full text : " + data.get(0).text());
////                Pattern pattern = Pattern.compile("(.*)\\s+\\((\\d+)\\)");
////                Matcher matcher = pattern.matcher(data.get(0).text());
////                if (matcher.find()) {
////                    System.out.println("version name : " + matcher.group(1));
////                    System.out.println("version code : " + matcher.group(2));
////                }
////            }
//
//
//
//
////            Document doc = Jsoup
////                    .connect(
////                            "https://play.google.com/store/apps/details?id=com.vegasega.streetsaarthi")
////                   // .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
////                    .referrer("http://www.google.com")
////                    .get();
////            Elements element = doc.getElementsByClass(".q078ud");
////            //Elements element = doc.select(".q078ud");
////            Log.e("inAppUpdates","ffSS"+ element.toString());
//
////            for (int i = 0; i < 10; i++) {
//////                String ggg = Version.get(i).text();
//////                Log.e("inAppUpdates","ffSS"+ ggg);
//////                VersionMarket = Version.get(i).text();
//////                if (Pattern.matches("^[0-9]{1}.[0-9]{1}.[0-9]{1}$", VersionMarket)) {
//////                    break;
//////                }
////            }
//        }catch (Exception e){
//
//        }
////
//       String ff =  web_update();
//        Log.e("inAppUpdates","ffSS"+ ff);
//
////        new CheckIsUpdateReady("https://play.google.com/store/apps/details?id=" + "com.vegasega.streetsaarthi" + "&hl=en").execute();
//
//    }
//
//
//    private String web_update(){
//        try {
//            String curVersion = getPackageManager().getPackageInfo("com.vegasega.streetsaarthi", 0).versionName;
//            Log.e("curVersion","curVersion "+ curVersion);
//            String newVersion = curVersion;
//            newVersion = String.valueOf(Jsoup.connect("https://play.google.com/store/apps/details?id=" + "com.vegasega.streetsaarthi" + "&hl=en")
//                    .timeout(30000)
//                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
//                    .referrer("http://www.google.com")
//                    .get()
////                    .select(".sMUprd .reAt0")
////                    .get(5)
////                    .select("div[reAt0]")
////                    .first()
//                    .getAllElements());
//            return newVersion;
//
//           // return (value(curVersion) < value(newVersion)) ? true : false;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
//
//    private final int APP_UPDATE_REQUEST_CODE=1230;
//    private AppUpdateManager appUpdateManager;
//    private InstallStateUpdatedListener installStateUpdatedListener;
//
//    private void inAppUpdates() {
//        appUpdateManager = AppUpdateManagerFactory.create(this);
//        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
//
//        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
//            Log.e("inAppUpdates","appUpdateInfo.updateAvailability() "+ appUpdateInfo.updateAvailability());
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
//                try {
//                    appUpdateManager.startUpdateFlowForResult(
//                            appUpdateInfo,
//                            AppUpdateType.FLEXIBLE,
//                            this,
//                            APP_UPDATE_REQUEST_CODE
//                    );
//                } catch (IntentSender.SendIntentException e) {
//                    Log.e("inAppUpdates","IntentSender.SendIntentException"+ e);
//                }
//            }
//        });
//
//        appUpdateInfoTask.addOnCompleteListener(app -> {
//            Log.e("inAppUpdates","appUpdateInfoTask.addOnCompleteListener"+ app.toString());
//        });
//
//        installStateUpdatedListener = state -> {
//            Log.e("inAppUpdates",String.valueOf(state.installStatus()));
//            if (state.installStatus() == InstallStatus.DOWNLOADING) {
//                long bytesDownloaded = state.bytesDownloaded();
//                long totalBytesToDownload = state.totalBytesToDownload();
//                Log.e("inAppUpdates","bytesDownloaded "+ bytesDownloaded+" / "+totalBytesToDownload);
//                // Update UI to show download progress.
//            } else if (state.installStatus() == InstallStatus.DOWNLOADED) {
//                Log.e("inAppUpdates","Update is downloaded and ready to install ");
//
//                // Notify the user and request installation.
//            } else if (state.installStatus() == InstallStatus.INSTALLING) {
//                Log.e("inAppUpdates","Update is being installed");
//
//                // Update UI to show installation progress.
//            } else if (state.installStatus() == InstallStatus.INSTALLED) {
//                Log.e("inAppUpdates","Update is installed");
//
//                // Notify the user and perform any necessary actions.
//            } else if (state.installStatus() == InstallStatus.FAILED) {
//                Log.e("inAppUpdates","Update failed to install");
//                // Notify the user and handle the error.
//            }
//        };
//        appUpdateManager.registerListener(installStateUpdatedListener);
//
//    }
//
//
//
//
//    public class VersionChecker extends AsyncTask<String, Document, Document> {
//
//        private Document document;
//
//        @Override
//        protected Document doInBackground(String... params) {
//
//            try {
//                document = (Document) Jsoup.connect("https://play.google.com/store/apps/details?id=com.vegasega.streetsaarthi&hl=en")
//                        .timeout(30000)
//                        .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
//                        .get();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return document;
//        }
//
//        @Override
//        protected void onPostExecute(Document d) {
//            super.onPostExecute(d);
//
////            String dd = new Gson().toJson(d.body());
//
//            Elements es =  d.body().getElementsByClass("xyOfqd");
//
////                    .select(".hAyfc");
////            String newVersion = es.get(3).child(1).child(0).child(0).ownText();
//            Log.i("TAG", "newVersion==="+es.toString());
//        }
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        if (appUpdateManager != null) {
//            appUpdateManager.unregisterListener(installStateUpdatedListener);
//        }
//        super.onDestroy();
//    }
//}

//try{
//Intent paytmIntent = new Intent();
//Bundle bundle = new Bundle();gues
//    bundle.putDouble("nativeSdkForMerchantAmount", Amount);
//    bundle.putString("orderid", OrderID);
//    bundle.putString("txnToken", txnToken);
//    bundle.putString("mid", MID);
//    paytmIntent.setComponent(new ComponentName("net.one97.paytm", "net.one97.paytm.AJRJarvisSplash"));
//        paytmIntent.putExtra("paymentmode", 2); // You must have to pass hard coded 2 here, Else your transaction would not proceed.
//    paytmIntent.putExtra("bill", bundle);
//startActivityForResult(paytmIntent, ActivityRequestCode);
//   }
//           catch(Exception e){
//        //This can handle ActivityNotFoundException or any other exception if any.
//        //Handle this exception as the same in case of Paytm App doesn’t exist.
//        }
