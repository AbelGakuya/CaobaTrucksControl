package com.example.caobatruckscontrol.presentation.composables.sign_in

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "AdminSignUpScreen"

@Composable
fun DriverSignUpScreen(
    navController: NavHostController,
    viewModel: SignInViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val activity = context as? Activity
    val firebaseAuth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var hidePassword by remember { mutableStateOf(true) }

    val state by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(WEB_CLIENT_ID) // Replace with your web client ID
        .requestEmail()
        .build()

    val signInClient = GoogleSignIn.getClient(context, signInOptions)

    var signInResult by remember { mutableStateOf<GoogleSignInAccount?>(null) }
    var signInError by remember { mutableStateOf<String?>(null) }

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
                    val email = it.email ?: ""

                    // Create driver data
                    val driver = mapOf(
                        "name" to name,
                        "age" to age,
                        "dateOfBirth" to dateOfBirth,
                        "location" to location
                    )

                    // Save to Firestore
                    val driversCollection = firestore.collection("drivers")
                    driversCollection.document(email).set(driver).await()

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
        val logo: Painter = painterResource(id = R.drawable.logo_2_png) // Replace with your logo resource
        Image(
            painter = logo,
            contentDescription = "Logo",
            modifier = Modifier.size(128.dp)
        )

        Text(text = "Sign Up as a Driver", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = { dateOfBirth = it },
            label = { Text("Date of Birth") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

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
                        // Toggle loading state
                        viewModel.updateState(SignInState(isLoading = true))
                        // Perform sign-up asynchronously
                        signUpWithEmailPassword(
                            navController,
                            viewModel,
                            email.text,
                            password.text,
                            name,
                            age,
                            dateOfBirth,
                            location
                        )
                        // Toggle loading state off
                        viewModel.updateState(SignInState(isLoading = false))
                    }
                } else {
                    Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(Color(0xFF6200EE)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Conditional content based on loading state
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text(
                    text = "Sign up with Email",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val signInIntent = signInClient.signInIntent
                launcher.launch(signInIntent)
            },
            colors = ButtonDefaults.buttonColors(Color(0xFF6200EE)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Text(
                text = "Sign up with Google",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }



    signInError?.let {
        Text(
            text = "Error: $it",
            color = Color.Red,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

private fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}


private suspend fun signUpWithEmailPassword(
    navController: NavController,
    viewModel: SignInViewModel,
    email: String,
    password: String,
    name: String,
    age: String,
    dateOfBirth: String,
    location: String
) {
    try {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        // Create user with email and password
        auth.createUserWithEmailAndPassword(email, password).await()

        // Sign in the user after sign-up
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val user = authResult.user

        user?.let {
            // Get the current FCM device token
            val token = FirebaseMessaging.getInstance().token.await()

            // Create user data including FCM token
            val userData = mapOf(
                "name" to name,
                "deviceToken" to token
            )

            // Save user data to the 'users' collection
            firestore.collection("users").document(user.uid).set(userData).await()

            // Create driver data
            val driver = mapOf(
                "name" to name,
                "age" to age,
                "dateOfBirth" to dateOfBirth,
                "location" to location,
                "deviceToken" to token  // Add the device token to the driver's document
            )

            // Save driver data to Firestore
            firestore.collection("drivers").document(user.uid).set(driver).await()
        }

        // Navigate to the pin screen
        navController.navigate("driver_dashboard")
    } catch (e: FirebaseAuthInvalidUserException) {
        Log.e(TAG, "Invalid user: ${e.message}")
        viewModel.updateState(SignInState(signInError = "Invalid email or password."))
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        Log.e(TAG, "Invalid credentials: ${e.message}")
        viewModel.updateState(SignInState(signInError = "Invalid email or password."))
    } catch (e: FirebaseAuthUserCollisionException) {
        Log.e(TAG, "User collision: ${e.message}")
        viewModel.updateState(SignInState(signInError = "Account already exists. Please sign in."))
    } catch (e: Exception) {
        Log.e(TAG, "Firebase Authentication exception: ${e.message}")
        viewModel.updateState(SignInState(signInError = "Authentication failed. Please try again later."))
    } finally {
        viewModel.updateState(SignInState(isLoading = false))
    }
}



