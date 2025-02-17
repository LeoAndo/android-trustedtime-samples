package com.example.composesample

import android.app.Activity
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

/**
 * jp) Google PlayサービスのAPIを利用する画面でのみ利用することを想定している
 * en) It is assumed that it is used only on screens that use the Google Play services API
 */
interface GoogleApiAvailabilityCheckable {
    /**
     * jp) Google Playサービスが最新かどうかをチェックし、古い場合には更新を促す
     * en) Check if Google Play services are up to date and prompt for an update if they are old
     *
     * @return jp) Google Playサービスが利用可能かどうか en) Whether Google Play services are available
     */
    fun Activity.checkGooglePlayServices(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)

        if (resultCode == ConnectionResult.SUCCESS) {
            // jp) Google Playサービスは正常 en) Google Play services is normal
            return true
        } else if (googleApiAvailability.isUserResolvableError(resultCode)) {
            // jp) 解決可能なエラーの場合 en) If it is a resolvable error
            showErrorDialog(resultCode)
        } else {
            // jp) 解決不能なエラー en) Unresolvable error
            Toast.makeText(
                this,
                "Google Play services are not supported on this device",
                Toast.LENGTH_LONG
            ).show()
            this.finish() // jp) アプリを終了する場合 en) If you want to exit the app
        }
        return false
    }

    /**
     * jp) エラーダイアログを表示して、更新を促す
     * en) Display an error dialog and prompt for an update
     *
     * @param errorCode jp) Google Playサービスのエラーコード en) Google Play services error code
     */
    private fun Activity.showErrorDialog(errorCode: Int) {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val errorDialog =
            googleApiAvailability.getErrorDialog(this, errorCode, PLAY_SERVICES_RESOLUTION_REQUEST)
        errorDialog?.setCancelable(false)
        errorDialog?.show()
    }

    companion object {
        const val PLAY_SERVICES_RESOLUTION_REQUEST = 1
    }
}