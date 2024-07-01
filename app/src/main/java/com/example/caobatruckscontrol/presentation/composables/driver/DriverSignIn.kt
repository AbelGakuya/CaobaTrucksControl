package com.example.caobatruckscontrol.presentation.composables.driver

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.caobatruckscontrol.R
import com.example.caobatruckscontrol.common.Constants.WEB_CLIENT_ID
import com.example.caobatruckscontrol.presentation.composables.sign_in.SignInState
import com.example.caobatruckscontrol.presentation.composables.sign_in.SignInViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Administrator(
    val name: String = "",
    val age: Int = 0,
    val dateOfBirth: String = "",
    val location: String = "",
    val isGeneralAdmin: Boolean = false
)

private const val TAG = "AdminSignUpScreen"

@Composable
fun DriverSignInScreen(
    navController: NavHostController,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val firebaseAuth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var hidePassword by remember { mutableStateOf(true) }
    var signInError by remember { mutableStateOf<String?>(null) }

    val state by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
//
//    // Check if user is already signed in
//    firebaseAuth.currentUser?.let { user ->
//        LaunchedEffect(Unit) {
//            navController.navigate("driver_dashboard") {
//                popUpTo(navController.graph.startDestinationId) {
//                    saveState = true
//                }
//                launchSingleTop = true
//                restoreState = true
//            }
//        }
//    }


    // State to control dialog visibility
    var showDialog by remember { mutableStateOf(false) }
    var signInSuccess by remember { mutableStateOf(false) }

    // Effect to show dialog after successful sign-in
    LaunchedEffect(signInSuccess) {
        if (signInSuccess) {
            showDialog = true
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        val logo: Painter = painterResource(id = R.drawable.logo_2_png) // Replace with your logo resource
        Image(
            painter = logo,
            contentDescription = "Logo",
            modifier = Modifier.size(128.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Welcome Text
        Text(
            text = "Welcome Driver!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6200EE)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sign in with your email and password to proceed.",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email.text,
            onValueChange = {
                email = email.copy(text = it)
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = !isValidEmail(email.text),
            visualTransformation = VisualTransformation.None // Show email as typed
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password.text,
            onValueChange = {
                password = password.copy(text = it)
            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (hidePassword) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                IconButton(onClick = { hidePassword = !hidePassword }) {
                    Icon(
                        imageVector = if (hidePassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (hidePassword) "Show Password" else "Hide Password"
                    )
                }
            },
            isError = password.text.length < 6 && password.text.isNotEmpty()
        )

        if (password.text.length in 1 until 6) {
            Text(
                text = "Password must be 6 or more characters",
                color = Color.Red,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isValidEmail(email.text) && password.text.length >= 6) {
                    coroutineScope.launch {
                        viewModel.updateState(SignInState(isLoading = true))
                        signInWithEmailPassword(navController, viewModel, email.text, password.text)
                        viewModel.updateState(SignInState(isLoading = false))
                    }
                } else {
                    Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(Color(0xFF6200EE)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text(
                    text = "Sign in with Email",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error Message
        signInError?.let {
            Text(
                text = "Error: $it",
                color = Color.Red,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Don't have an account? Sign up",
            color = androidx.compose.ui.graphics.Color.Blue,
            modifier = Modifier.clickable { navController.navigate("driver_sign_up") }
        )

        // Dialog to show "Coming Soon" message
        if (state.isSignInSuccessful) {
            AlertDialog(
                onDismissRequest = {
                    viewModel.updateState(SignInState(isSignInSuccessful = false))
                },
                title = {
                    Text(text = "Coming Soon")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.updateState(SignInState(isSignInSuccessful = false))
                        }
                    ) {
                        Text(text = "OK")
                    }
                }
            )
        }
    }
}


private suspend fun signInWithEmailPassword(
    navController: NavController,
    viewModel: SignInViewModel,
    email: String,
    password: String
) {
    try {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password).await()

        // Get the current signed-in user
        val user = auth.currentUser
        user?.let {
            // Get the current FCM device token
            val token = FirebaseMessaging.getInstance().token.await()

            // Update the driver's document with the new device token
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("users").document(user.uid).update("deviceToken",token).await()
        }

        // Indicate successful sign-in
        viewModel.updateState(SignInState(isSignInSuccessful = true))
        navController.navigate("driver_dashboard")
    } catch (e: FirebaseAuthInvalidUserException) {
        Log.e(TAG, "Invalid user: ${e.message}")
        viewModel.updateState(SignInState(signInError = "Invalid email or password."))
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        Log.e(TAG, "Invalid credentials: ${e.message}")
        viewModel.updateState(SignInState(signInError = "Invalid email or password."))
    } catch (e: Exception) {
        Log.e(TAG, "Firebase Authentication exception: ${e.message}")
        viewModel.updateState(SignInState(signInError = "Authentication failed. Please try again later."))
    } finally {
        viewModel.updateState(SignInState(isLoading = false))
    }
}


private fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

