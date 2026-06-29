package com.akashicsoft.crm.platform

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

class AndroidPhoneCaller(
    private val context: android.content.Context,
    private val requestPermission: (String, (Boolean) -> Unit) -> Unit
) : PhoneCaller {
    override fun call(phoneNumber: String) {
        val permission = Manifest.permission.CALL_PHONE
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            makeCall(phoneNumber)
        } else {
            requestPermission(permission) { granted ->
                if (granted) {
                    makeCall(phoneNumber)
                }
            }
        }
    }

    private fun makeCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phoneNumber")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}

@Composable
actual fun rememberPhoneCaller(): PhoneCaller {
    val context = LocalContext.current
    var onPermissionResult by remember { mutableStateOf<((Boolean) -> Unit)?>(null) }
    
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onPermissionResult?.invoke(isGranted)
    }

    return remember(context, launcher) {
        AndroidPhoneCaller(context) { permission, callback ->
            onPermissionResult = callback
            launcher.launch(permission)
        }
    }
}
