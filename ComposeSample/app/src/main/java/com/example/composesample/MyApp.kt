package com.example.composesample

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.time.TrustedTimeClient

class MyApp : Application() {
    var trustedTimeClient: TrustedTimeClient? = null

    override fun onCreate() {
        super.onCreate()
        // アプリ内のActivityのライフサイクルを監視する
        // Monitor the lifecycle of activities in the app
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
                if (checkGooglePlayServices(activity)) {
                    // jp) Google Playサービスが正常の場合の処理
                    // en) Processing when Google Play services are normal
                    Toast.makeText(
                        activity,
                        "Google Play services is up to date",
                        Toast.LENGTH_SHORT
                    ).show()

                    Log.d("MyApp", "trustedTimeClient: $trustedTimeClient")
                    if (trustedTimeClient == null) {
                        TrustedTimeClientAccessor.getTrustedTimeClientTask(activity)
                            .addOnCompleteListener(OnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    trustedTimeClient = task.getResult()
                                } else {
                                    // Handle error
                                    // jp) エラーになるケースの具体例がわからないため、予期せぬ問題としてクラッシュさせる
                                    // en) Since I do not know the specific example of the case where an error occurs, I crash as an unexpected problem.
                                    val exception = task.exception
                                    Log.e("MyApp", "error", exception)
                                    throw IllegalStateException(
                                        "TrustedTimeClient is not available",
                                        exception
                                    )
                                }
                            })
                    }
                }
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }
        })
    }

    /**
     * jp) Google Playサービスが最新かどうかをチェックし、古い場合には更新を促す
     * en) Check if Google Play services are up to date and prompt for an update if they are old
     *
     * @return jp) Google Playサービスが利用可能かどうか en) Whether Google Play services are available
     */
    private fun checkGooglePlayServices(activity: Activity): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(activity)

        if (resultCode == ConnectionResult.SUCCESS) {
            // jp) Google Playサービスは正常 en) Google Play services is normal
            return true
        } else if (googleApiAvailability.isUserResolvableError(resultCode)) {
            // jp) 解決可能なエラーの場合 en) If it is a resolvable error
            showErrorDialog(activity, resultCode)
        } else {
            // jp) 解決不能なエラー en) Unresolvable error
            Toast.makeText(
                activity,
                "Google Play services are not supported on this device",
                Toast.LENGTH_LONG
            ).show()
            activity.finish() // jp) アプリを終了する場合 en) If you want to exit the app
        }
        return false
    }

    /**
     * jp) エラーダイアログを表示して、更新を促す
     * en) Display an error dialog and prompt for an update
     *
     * @param errorCode jp) Google Playサービスのエラーコード en) Google Play services error code
     */
    private fun showErrorDialog(activity: Activity, errorCode: Int) {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val errorDialog = googleApiAvailability.getErrorDialog(
            activity,
            errorCode,
            PLAY_SERVICES_RESOLUTION_REQUEST
        )
        errorDialog?.setCancelable(false)
        errorDialog?.show()
    }

    companion object {
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 1
    }
}