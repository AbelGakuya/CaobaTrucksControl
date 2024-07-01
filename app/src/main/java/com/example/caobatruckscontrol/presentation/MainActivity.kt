package com.example.caobatruckscontrol.presentation

//import SignInViewModel
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.compose.rememberNavController
import com.example.caobatruckscontrol.presentation.composables.WelcomeScreen
import com.example.caobatruckscontrol.presentation.composables.sign_in.SignInViewModel
import com.example.caobatruckscontrol.presentation.ui.theme.CaobaTrucksControlTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException


import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check and request notification permission if not granted
        if (!isNotificationPermissionGranted()) {
            requestNotificationPermission()
        }

        setContent {
            CaobaTrucksControlTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigation(
                        this,
                        navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun isNotificationPermissionGranted(): Boolean {
        return NotificationManagerCompat.from(this).areNotificationsEnabled()
    }

    private fun requestNotificationPermission() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Notification Permission Required")
        builder.setMessage("Please grant notification permission to receive important updates.")

        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
            // Open application settings
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, which ->
            // Handle if user denies permission
        }

        builder.setOnDismissListener {
            // Handle dialog dismissal
        }

        builder.show()
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CaobaTrucksControlTheme {
        Greeting("Android")
    }
}