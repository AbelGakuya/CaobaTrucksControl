package com.example.caobatruckscontrol.presentation.composables.admin

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
fun AdministratorRoleSelectionScreen(
    navController: NavHostController,
    viewModel: SignInViewModel = hiltViewModel()
    ) {
    val context = LocalContext.current
    val activity = context as? Activity
    val firebaseAuth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(WEB_CLIENT_ID) // Replace with your web client ID
        .requestEmail()
        .build()

    val signInClient = GoogleSignIn.getClient(context, signInOptions)

    var signInResult by remember { mutableStateOf<GoogleSignInAccount?>(null) }
    var signInError by remember { mutableStateOf<String?>(null) }

    val state by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()

//    // Check if user is already signed in
//    firebaseAuth.currentUser?.let { user ->
//        LaunchedEffect(Unit) {
//            navController.navigate("admin") {
//                popUpTo(navController.graph.startDestinationId) {
//                    saveState = true
//                }
//                launchSingleTop = true
//                restoreState = true
//            }
//        }
//    }

    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var hidePassword by remember { mutableStateOf(true) }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            signInResult = account
        } catch (e: ApiException) {
            signInError = e.message
        }
    }

    LaunchedEffect(signInResult) {
        signInResult?.let { account ->
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            try {
                val authResult = firebaseAuth.signInWithCredential(credential).await()
                val user = authResult.user
                user?.let {
                    // Navigate to the pin screen
                    navController.navigate("pin")
                }
            } catch (e: Exception) {
                signInError = e.message
            }
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
        val logo: Painter = painterResource(id = R.drawable.logo_1_png) // Replace with your logo resource
        Image(
            painter = logo,
            contentDescription = "Logo",
            modifier = Modifier.size(128.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Welcome Text
        Text(
            text = "Welcome Administrator",
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
            isError = !isValidEmail(
                email.text
            ),
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
                if (isValidEmail(
                        email.text
                    ) && password.text.length >= 6) {
                    coroutineScope.launch {
                        viewModel.updateState(SignInState(isLoading = true))
                        signInWithEmailPassword(navController, viewModel, email.text, password.text,firestore)
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

        Spacer(modifier = Modifier.height(24.dp))

        // Sign in Button
        Button(
            onClick = {
                val signInIntent = signInClient.signInIntent
                launcher.launch(signInIntent)
            },
            colors = ButtonDefaults.buttonColors(Color(0xFF6200EE)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
        ) {
            Text(
                text = "Sign in with Google",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
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
            modifier = Modifier.clickable { navController.navigate("admin_sign_up") }
        )
    }
}

private suspend fun signInWithEmailPassword(
    navController: NavController,
    viewModel: SignInViewModel,
    email: String,
    password: String,
    firestore: FirebaseFirestore
) {
    try {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password).await()

        // Get FCM token
        val token = FirebaseMessaging.getInstance().token.await()

        // Update administrator's FCM token in Firestore
        auth.currentUser?.let { user ->
            firestore.collection("users").document(user.uid)
                .update("deviceToken", token)
                .await()
        }

        // Navigate to PIN screen or next destination
        navController.navigate("pin")

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

