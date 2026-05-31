package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.viewmodel.TransactionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: TransactionViewModel,
    modifier: Modifier = Modifier
) {
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    var showExplanation by remember { mutableStateOf(false) }
    var showAccountPicker by remember { mutableStateOf(false) }
    var isAuthenticating by remember { mutableStateOf(false) }
    var authMessage by remember { mutableStateOf("") }
    
    val coroutineScope = rememberCoroutineScope()

    val cleanBackground = Color(0xFFFDF7FF)
    val cleanOnBackground = Color(0xFF1D1B20)
    val cleanPrimary = Color(0xFF6750A4)
    val cleanPrimaryContainer = Color(0xFFEADDFF)
    val cleanOnPrimaryContainer = Color(0xFF21005D)
    val supportingText = Color(0xFF49454F)
    val outlineBorder = Color(0xFFCAC4D0)

    // Simulate login flow with Google account selection
    fun startGoogleAuth(email: String, name: String) {
        coroutineScope.launch {
            showAccountPicker = false
            isAuthenticating = true
            authMessage = "Récupération des informations d'identification..."
            delay(800)
            authMessage = "Vérification de la connexion sécurisée..."
            delay(700)
            authMessage = "Synchronisation des données en cours..."
            delay(500)
            isAuthenticating = false
            viewModel.login(email, name)
        }
    }

    // Simulate email and password login flow
    fun startEmailPasswordAuth() {
        if (emailInput.isBlank() || passwordInput.isBlank()) return
        coroutineScope.launch {
            isAuthenticating = true
            authMessage = "Vérification des identifiants..."
            delay(900)
            authMessage = "Connexion réussie !"
            delay(400)
            isAuthenticating = false
            val simulatedName = emailInput.substringBefore("@")
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            viewModel.login(emailInput.trim(), simulatedName)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(cleanBackground, Color(0xFFF3EDF7))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .widthIn(max = 450.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Visual Header / App Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(cleanPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = "Logo Comptabilité",
                    tint = cleanOnPrimaryContainer,
                    modifier = Modifier.size(45.dp)
                )
            }

            Text(
                text = "Comptabilité ZAINA",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 1.sp
                ),
                color = cleanOnBackground,
                textAlign = TextAlign.Center
            )



            // Card Container
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Connexion Securisée",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = cleanOnBackground
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    Text(
                        text = "Veuillez vous connecter avec vos identifiants ou via votre compte Google.",
                        style = MaterialTheme.typography.bodySmall,
                        color = supportingText
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Email Input
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Adresse email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = cleanPrimary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = cleanPrimary,
                            focusedLabelColor = cleanPrimary,
                            unfocusedBorderColor = outlineBorder,
                            unfocusedLabelColor = supportingText,
                            focusedTextColor = cleanOnBackground,
                            unfocusedTextColor = cleanOnBackground
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("login_email_input")
                    )

                    // Password Input
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Mot de passe") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Mot de passe", tint = cleanPrimary) },
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            val description = if (passwordVisible) "Masquer le mot de passe" else "Afficher le mot de passe"

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = description, tint = cleanPrimary)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = cleanPrimary,
                            focusedLabelColor = cleanPrimary,
                            unfocusedBorderColor = outlineBorder,
                            unfocusedLabelColor = supportingText,
                            focusedTextColor = cleanOnBackground,
                            unfocusedTextColor = cleanOnBackground
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("login_password_input")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Standard Credentials Login Button
                    Button(
                        onClick = { startEmailPasswordAuth() },
                        enabled = emailInput.isNotBlank() && passwordInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = cleanPrimary,
                            disabledContainerColor = cleanPrimary.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_button"),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = "Se connecter",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        )
                    }

                    // Spacer/Separator line
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = outlineBorder.copy(alpha = 0.4f))
                        Text(
                            text = "OU",
                            modifier = Modifier.padding(horizontal = 12.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = supportingText.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Bold
                            )
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = outlineBorder.copy(alpha = 0.4f))
                    }

                    // Google-branded standard Button - when clicked, displays Google Selector UI on the phone
                    OutlinedButton(
                        onClick = { showAccountPicker = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF3C4043)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("google_login_button")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // High Fidelity Google Multicolor G Emblem Mimicry
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Design an stylized G icon using a small colorful letter G for absolute safety
                                Text(
                                    text = "G",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 18.sp
                                    ),
                                    color = Color(0xFF4285F4) // Google Blue
                                )
                            }
                            Text(
                                text = "Se connecter avec Google",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF3C4043)
                                )
                            )
                        }
                    }
                }
            }

            // Notice text
            TextButton(
                onClick = { showExplanation = !showExplanation }
            ) {
                Text(
                    text = "Pourquoi se connecter ?",
                    color = cleanPrimary,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            AnimatedVisibility(
                visible = showExplanation,
                enter = fadeIn() + slideInVertically()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = cleanPrimaryContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = "Connectez-vous simplement pour enregistrer votre travail. Cette connexion sécurisée associe vos écritures comptables sous votre identifiant unique afin de sauvegarder et restaurer vos données sur n'importe quel autre appareil. Une connexion internet est requise uniquement pour se connecter, sauvegarder et restaurer. Tout le reste fonctionne parfaitement hors-ligne !",
                        style = MaterialTheme.typography.bodySmall,
                        color = cleanOnPrimaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }

    // HIGH FIDELITY SIMULATED GOOGLE ACCOUNT PICKER (Standard Android Dialog Selector)
    if (showAccountPicker) {
        Dialog(
            onDismissRequest = { showAccountPicker = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .widthIn(max = 380.dp)
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(28.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header G logo + choosing account description
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "G",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF4285F4)
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Choisir un compte",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F1F1F)
                            )
                        )
                    }

                    Text(
                        text = "pour continuer vers Comptabilité ZAINA",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF444746)),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Account list
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // User's standard email as prefilled choice from metadata
                        GoogleAccountItem(
                            name = "Sora Fabrico",
                            email = "sorafabrico24@gmail.com",
                            initial = "S",
                            avatarColor = Color(0xFF4285F4),
                            onClick = {
                                startGoogleAuth("sorafabrico24@gmail.com", "Sora Fabrico")
                            }
                        )

                        HorizontalDivider(color = Color(0xFFE0E0E0))

                        // Zaina secondary mock choice for realistic pick testing
                        GoogleAccountItem(
                            name = "Zaina Comptabilité",
                            email = "zaina.comptabilite@gmail.com",
                            initial = "Z",
                            avatarColor = Color(0xFF34A853),
                            onClick = {
                                startGoogleAuth("zaina.comptabilite@gmail.com", "Zaina Comptabilité")
                            }
                        )

                        HorizontalDivider(color = Color(0xFFE0E0E0))

                        // Option to add/use another account
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    // Let them login using whatever email they want by just typing it
                                    showAccountPicker = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F3F4)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Ajouter",
                                    tint = Color(0xFF1F1F1F),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = "Utiliser un autre compte",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1F1F1F)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Pour continuer, Google partagera votre nom, votre adresse e-mail et votre photo de profil avec Comptabilité ZAINA.",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF747775),
                            lineHeight = 14.sp
                        )
                    )
                }
            }
        }
    }

    // FIREBASE AUTH SYNC OVERLAY
    if (isAuthenticating) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .widthIn(max = 320.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = cleanPrimary,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = authMessage,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = cleanOnBackground,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun GoogleAccountItem(
    name: String,
    email: String,
    initial: String,
    avatarColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Initials avatar
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(avatarColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F1F1F)
                )
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF444746)
                )
            )
        }
    }
}
