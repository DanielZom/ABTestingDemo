package hu.daniel.abtestingdemo

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


@Suppress("PrivatePropertyName")
class MainActivity : AppCompatActivity() {

    private val REMOTE_CONFIG_LOG_TAG = "REMOTE_CONFIG_LOG_TAG"
    private val remoteConfig: FirebaseRemoteConfig by lazy { FirebaseRemoteConfig.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (BuildConfig.DEBUG) getFirebaseInstanceId()

        remoteConfig.run {
            setup()
            setDefaults()
            fetchConfig()
        }
    }

    private fun FirebaseRemoteConfig.setup() {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 0 else TimeUnit.HOURS.toMillis(12))
                .build()
        setConfigSettingsAsync(configSettings)
    }

    private fun FirebaseRemoteConfig.setDefaults() {
        val defaults: Map<String, String> = mapOf(
                (RemoteConfigKey.WELCOME_TEXT_COLOR.name to R.color.teal_200.toStringColor()),
                (RemoteConfigKey.WELCOME_TEXT.name to getString(R.string.welcome))
        )
        setDefaultsAsync(defaults)
    }

    private fun FirebaseRemoteConfig.fetchConfig() {
        fetchAndActivate().addOnCompleteListener(this@MainActivity) { task ->
            if (task.isSuccessful) {
                val updated = task.result
                Log.d(REMOTE_CONFIG_LOG_TAG, "Config params updated: $updated")
                Toast.makeText(this@MainActivity, "Fetch and activate succeeded", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Fetch failed", Toast.LENGTH_SHORT).show()
            }
            applyRemoteConfigs()
        }
    }

    private fun FirebaseRemoteConfig.applyRemoteConfigs() {
        welcome.text = getString(RemoteConfigKey.WELCOME_TEXT.name)
        welcome.setTextColor(getString(RemoteConfigKey.WELCOME_TEXT_COLOR.name).toColor())
    }

    private fun String.toColor() = Color.parseColor(this)
    private fun Int.toStringColor() = "#" + Integer.toHexString(ContextCompat.getColor(this@MainActivity, this))

    @Suppress("DEPRECATION")
    private fun getFirebaseInstanceId() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) { instanceIdResult: InstanceIdResult ->
            Log.e("FIREBASE_ID", instanceIdResult.token)
        }
    }
}