package com.example.caobatruckscontrol.presentation.composables.sign_in
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.caobatruckscontrol.R
import com.example.caobatruckscontrol.presentation.composables.sign_in.SignInState
import com.example.caobatruckscontrol.presentation.composables.sign_in.SignInViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "AdminSignUpScreen"

@Composable
fun AdminSignUpScreen(
    navController: NavController,
    viewModel: SignInViewModel = hiltViewModel(),
    auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var hidePassword by remember { mutableStateOf(true) }

    var pin by remember { mutableStateOf("") }
    var showPinDialog by remember { mutableStateOf(false) }
    val firestore = FirebaseFirestore.getInstance()

    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                viewModel.signInWithGoogle(account)
            } else {
                viewModel.signInWithGoogle(null)
            }
        } catch (e: ApiException) {
            viewModel.signInWithGoogle(null)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val logo = painterResource(id = R.drawable.logo_1_png)
        Image(
            painter = logo,
            contentDescription = "Logo",
            modifier = Modifier.size(128.dp)
        )

        Text(text = "Sign Up as Administrator", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable section for text fields
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
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
                            viewModel.updateState(SignInState(isLoading = true))
                            val generatedPin = signUpWithEmailPassword(
                                navController, viewModel, email.text, password.text, name, age, dateOfBirth, location, firestore)
                            pin = generatedPin
                            showPinDialog = true
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
                        text = "Sign up with Email",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        if (showPinDialog) {
            AlertDialog(
                onDismissRequest = { showPinDialog = false },
                title = { Text(text = "PIN Generated") },
                text = { Text(text = "Your PIN is: $pin. Please note it down for future reference.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showPinDialog = false
                            navController.navigate("pin")
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("admin")
               // launcher.launch(viewModel.getSignInIntent())
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

        if (state.isSignInSuccessful) {
            LaunchedEffect(navController) {
                navController.navigate("pin")
            }
        } else if (state.signInError != null) {
           // Text(text = state.signInError ?: "Unknown error", color = Color.Red)
            Toast.makeText(context, state.signInError ?: "Unknown error", Toast.LENGTH_SHORT).show()
        }
    }
}

private suspend fun signUpWithEmailPassword(
    navController: NavController,
    viewModel: SignInViewModel,
    email: String,
    password: String,
    name: String,
    age: String,
    dateOfBirth: String,
    location: String,
    firestore: FirebaseFirestore
): String {
    var pin = ""
    try {
        val auth = FirebaseAuth.getInstance()

        // Create user with email and password
        auth.createUserWithEmailAndPassword(email, password).await()

        // Sign in the user after sign-up
        auth.signInWithEmailAndPassword(email, password).await()

        // Generate a PIN for the administrator
        pin = generatePin()

        // Get FCM token
        val token = FirebaseMessaging.getInstance().token.await()

        // Create user data including FCM token
        val userData = mapOf(
            "name" to name,
            "deviceToken" to token
        )

        // Save user data to the 'users' collection
        firestore.collection("users").document(auth.currentUser!!.uid)
            .set(userData).await()

        // Create administrator data
        val adminData = mapOf(
            "name" to name,
            "age" to age,
            "dateOfBirth" to dateOfBirth,
            "location" to location,
            "pin" to pin,
            "deviceToken" to token  // Store FCM token here
        )

        // Save administrator data to Firestore
        firestore.collection("administrators").document(auth.currentUser!!.uid)
            .set(adminData).await()

    } catch (e: FirebaseAuthUserCollisionException) {
        Log.e("SignUp", "User collision: ${e.message}")
        viewModel.updateState(SignInState(signInError = "Account already exists. Please sign in."))
    } catch (e: Exception) {
        Log.e("SignUp", "Firebase Authentication exception: ${e.message}")
        viewModel.updateState(SignInState(signInError = "Authentication failed. Please try again later."))
    } finally {
        viewModel.updateState(SignInState(isLoading = false))
    }
    return pin
}


private fun generatePin(): String {
    return (100000..999999).random().toString()
}

private fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}


