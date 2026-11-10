package com.example.features.auth.presentation.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.*
import com.example.core.common.LifeOSViewModel

enum class AuthMode {
    SIGN_IN, SIGN_UP, FORGOT_PASSWORD
}

@Composable
fun AuthScreen(
    viewModel: LifeOSViewModel,
    modifier: Modifier = Modifier
) {
    var authMode by remember { mutableStateOf(AuthMode.SIGN_IN) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val isGlobalLoading by viewModel.isGlobalLoading.collectAsState()
    val globalError by viewModel.globalError.collectAsState()

    // Global Interactive Overlays
    LifeOSLoadingOverlay(isLoading = isGlobalLoading, message = "Sincronizando com Supabase...")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val glowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glowColor, Color.Transparent),
                    center = Offset(size.width * 0.8f, size.height * 0.15f),
                    radius = size.width * 0.6f
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Container for tablet responsiveness (Limits max width and centers card)
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App Header Branding
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Adjust,
                            contentDescription = "Logo LifeOS AI",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LifeOS AI",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // New Gorgeous Hero Section
                Text(
                    text = "Sua vida muda",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1.5).sp,
                        lineHeight = 44.sp
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = "um hábito por vez.",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1.5).sp,
                        lineHeight = 44.sp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text(
                    text = "Organize sua rotina, acompanhe metas, evolua diariamente e deixe a IA criar o plano perfeito para você.",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    modifier = Modifier
                        .widthIn(max = 500.dp)
                        .padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                // Metric Grid requested by user
                MetricGrid()

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Dashboard Preview Mockup requested by user
                InteractiveDashboardPreview()

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive AI Analysis Card requested by user
                InteractiveAiAnalysisCard()

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Step Flow
                InteractiveStepFlow()

                Spacer(modifier = Modifier.height(24.dp))

                // Subscription Plans featuring the "Economize 30%" Badge
                PricingSection(
                    onPlanSelected = { plan ->
                        viewModel.showToast(
                            if (plan == "annual") "Excelente escolha! O plano Anual com 30% de desconto está pronto para você."
                            else "Plano Mensal selecionado! Crie sua conta ou faça login para continuar.",
                            com.example.core.common.ToastType.SUCCESS
                        )
                    },
                    onFooterLinkClicked = { link ->
                        val msg = when (link) {
                            "dashboard" -> "Visualizando visão geral das ferramentas do Dashboard..."
                            "roadmap" -> "Abrindo Roadmap oficial com recursos futuros..."
                            "blog" -> "Abrindo Blog do LifeOS: dicas de produtividade e foco..."
                            "contact" -> "Abrindo canal de atendimento e contato com suporte..."
                            "instagram" -> "Redirecionando para o Instagram do LifeOS..."
                            "linkedin" -> "Redirecionando para o LinkedIn do LifeOS..."
                            "discord" -> "Abrindo convite para a comunidade oficial no Discord..."
                            "privacy" -> "Carregando Política de Privacidade..."
                            "terms" -> "Carregando Termos de Uso e Condições..."
                            "lgpd" -> "Abrindo termos de conformidade e segurança LGPD..."
                            "cookies" -> "Gerenciando preferências de Cookies e rastreamento..."
                            else -> "Iniciando canal de Suporte com nossa equipe..."
                        }
                        viewModel.showToast(msg, com.example.core.common.ToastType.INFO)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Error Notification Banner
                LifeOSErrorBanner(
                    errorMessage = globalError,
                    onDismiss = { viewModel.dismissError() }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Auth Card with gorgeous glassmorphism, 3D hover scale and spring translations
                LifeOSCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_card"),
                    backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(28.dp),
                    contentPadding = 32.dp,
                    interactive = true,
                    onClick = {} // Enables interaction source tracking for hover & touch scale states
                ) {
                    // Modes Tab Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TabItem(
                            text = "Entrar",
                            isSelected = authMode == AuthMode.SIGN_IN,
                            onClick = { authMode = AuthMode.SIGN_IN }
                        )
                        TabItem(
                            text = "Criar Conta",
                            isSelected = authMode == AuthMode.SIGN_UP,
                            onClick = { authMode = AuthMode.SIGN_UP }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Email & Name & Password Fields
                    AnimatedVisibility(
                        visible = authMode == AuthMode.SIGN_UP,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            LifeOSInput(
                                value = name,
                                onValueChange = { name = it },
                                placeholder = "Seu Nome Inteiro",
                                leadingIcon = Icons.Default.Person,
                                modifier = Modifier
                                    .padding(bottom = 12.dp)
                                    .testTag("auth_name_input")
                            )
                        }
                    }

                    LifeOSInput(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Seu endereço de e-mail",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .testTag("auth_email_input")
                    )

                    AnimatedVisibility(
                        visible = authMode != AuthMode.FORGOT_PASSWORD,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            LifeOSInput(
                                value = password,
                                onValueChange = { password = it },
                                placeholder = "Senha de acesso",
                                leadingIcon = Icons.Default.Lock,
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                trailingIcon = {
                                    IconButton(
                                        onClick = { passwordVisible = !passwordVisible },
                                        modifier = Modifier.testTag("toggle_password_visibility")
                                    ) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Alternar visibilidade da senha",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .padding(bottom = 12.dp)
                                    .testTag("auth_password_input")
                            )
                        }
                    }

                    // Password recovery helper trigger
                    if (authMode == AuthMode.SIGN_IN) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = "Esqueci minha senha?",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier
                                    .clickable { authMode = AuthMode.FORGOT_PASSWORD }
                                    .testTag("forgot_password_link")
                            )
                        }
                    } else if (authMode == AuthMode.FORGOT_PASSWORD) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = "Voltar para o Login",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier
                                    .clickable { authMode = AuthMode.SIGN_IN }
                                    .testTag("back_to_login_link")
                            )
                        }
                    }

                    // Call-To-Action Button
                    LifeOSButton(
                        text = when (authMode) {
                            AuthMode.SIGN_IN -> "Entrar com Supabase"
                            AuthMode.SIGN_UP -> "Criar Conta Supabase"
                            AuthMode.FORGOT_PASSWORD -> "Recuperar via Supabase"
                        },
                        onClick = {
                            when (authMode) {
                                AuthMode.SIGN_IN -> {
                                    if (email.isNotBlank() && password.isNotBlank()) {
                                        viewModel.signIn(email, password)
                                    } else {
                                        viewModel.showToast("Preencha e-mail e senha para acessar!", com.example.core.common.ToastType.WARNING)
                                    }
                                }
                                AuthMode.SIGN_UP -> {
                                    if (email.isNotBlank() && password.isNotBlank() && name.isNotBlank()) {
                                        viewModel.signUp(name, email)
                                    } else {
                                        viewModel.showToast("Todos os campos são obrigatórios!", com.example.core.common.ToastType.WARNING)
                                    }
                                }
                                AuthMode.FORGOT_PASSWORD -> {
                                    if (email.isNotBlank()) {
                                        viewModel.resetPassword(email)
                                        authMode = AuthMode.SIGN_IN
                                    } else {
                                        viewModel.showToast("Preencha o e-mail cadastrado!", com.example.core.common.ToastType.WARNING)
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_submit_button")
                    )

                    // Separator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Text(
                            text = "ou continue com",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }

                    // Social Logins
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SocialButton(
                            text = "Google",
                            icon = Icons.Default.Grain, // Represents Google cleanly
                            onClick = {
                                viewModel.signIn("google.user@gmail.com", "Google Connect")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("social_google_button")
                        )
                        SocialButton(
                            text = "Apple",
                            icon = Icons.Default.FilterList, // Represents Apple cleanly
                            onClick = {
                                viewModel.signIn("apple.user@icloud.com", "Apple Connect")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("social_apple_button")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Instant Demo Quick Access
                    OutlinedButton(
                        onClick = {
                            viewModel.signIn("rafaeltj16@gmail.com", "Rafael")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("quick_demo_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Acesso Rápido Demo",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Acesso Rápido Demo • Explorar LifeOS",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.TabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent
    val textColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .weight(1f)
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .testTag("tab_${text.lowercase().replace(" ", "_")}"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = textColor
        )
    }
}

@Composable
fun SocialButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                RoundedCornerShape(10.dp)
            )
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = "Ícone de login social $text",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun MetricGrid(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Habit Streaks",
                description = "Sequências diárias",
                icon = Icons.Default.Whatshot,
                iconTint = Color(0xFFFF7043),
                bgColor = Color(0xFFFF7043).copy(alpha = 0.08f),
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "95%",
                description = "Retenção média",
                icon = Icons.Default.TrendingUp,
                iconTint = Color(0xFF66BB6A),
                bgColor = Color(0xFF66BB6A).copy(alpha = 0.08f),
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "+25 mil",
                description = "Hábitos criados",
                icon = Icons.Default.CheckCircle,
                iconTint = Color(0xFF29B6F6),
                bgColor = Color(0xFF29B6F6).copy(alpha = 0.08f),
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "4.9 Estrelas",
                description = "Avaliação da comunidade",
                icon = Icons.Default.Star,
                iconTint = Color(0xFFFFCA28),
                bgColor = Color(0xFFFFCA28).copy(alpha = 0.08f),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconTint: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isInteracted = isPressed || isHovered

    val scale by animateFloatAsState(
        targetValue = if (isInteracted) 1.03f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "cardScale"
    )

    val iconRotation by animateFloatAsState(
        targetValue = if (isInteracted) 12f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "iconRotation"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                this.scaleX = scale
                this.scaleY = scale
            }
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(
                width = 1.dp,
                color = iconTint.copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null, // keep it flat and clean
                onClick = {}
            )
            .padding(14.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(iconTint.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier
                        .size(16.dp)
                        .graphicsLayer {
                            this.rotationZ = iconRotation
                        }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun InteractiveStepFlow(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "COMO FUNCIONA",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Step 1
        StepRow(
            number = "1",
            title = "Crie um hábito",
            description = "Defina sua meta e frequência de forma simples",
            icon = Icons.Default.AddCircle,
            accentColor = MaterialTheme.colorScheme.primary,
            showNextArrow = true
        )

        // Step 2
        StepRow(
            number = "2",
            title = "A IA cria um plano",
            description = "O assistente inteligente personaliza sua jornada",
            icon = Icons.Default.AutoAwesome,
            accentColor = MaterialTheme.colorScheme.secondary,
            showNextArrow = true
        )

        // Step 3
        StepRow(
            number = "3",
            title = "Você faz check-in",
            description = "Registre sua evolução diária com facilidade",
            icon = Icons.Default.CheckCircle,
            accentColor = Color(0xFF66BB6A),
            showNextArrow = true
        )

        // Step 4
        StepRow(
            number = "4",
            title = "Ganhe XP e evolua",
            description = "Suba de nível e ganhe recompensas reais",
            icon = Icons.Default.TrendingUp,
            accentColor = Color(0xFFFFCA28),
            showNextArrow = false
        )
    }
}

@Composable
fun StepRow(
    number: String,
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    showNextArrow: Boolean,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isInteracted = isPressed || isHovered

    val scale by animateFloatAsState(
        targetValue = if (isInteracted) 1.04f else 1.00f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "stepScale"
    )

    val iconRotation by animateFloatAsState(
        targetValue = if (isInteracted) 15f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "stepIconRot"
    )

    Column(
        modifier = modifier
            .widthIn(max = 480.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    this.scaleX = scale
                    this.scaleY = scale
                }
                .clip(RoundedCornerShape(20.dp))
                .background(accentColor.copy(alpha = 0.05f))
                .border(
                    width = 1.dp,
                    color = if (isInteracted) accentColor.copy(alpha = 0.4f) else accentColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {}
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Number Circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(accentColor, accentColor.copy(alpha = 0.7f))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Step Icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer {
                            this.rotationZ = iconRotation
                        }
                )
            }
        }

        if (showNextArrow) {
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun InteractiveDashboardPreview(modifier: Modifier = Modifier) {
    var academiaChecked by remember { mutableStateOf(true) }
    var aguaChecked by remember { mutableStateOf(true) }
    var meditacaoChecked by remember { mutableStateOf(false) }

    val checkedCount = (if (academiaChecked) 1 else 0) + (if (aguaChecked) 1 else 0) + (if (meditacaoChecked) 1 else 0)
    val progressFraction = checkedCount / 3f

    // Animated dynamic values
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "progress"
    )

    val xpValue = 1000 + (checkedCount * 100)
    val animatedXp by animateIntAsState(
        targetValue = xpValue,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "xp"
    )

    LifeOSCard(
        modifier = modifier
            .widthIn(max = 480.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        shape = RoundedCornerShape(28.dp),
        contentPadding = 20.dp,
        interactive = true,
        onClick = {}
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📈",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1.0f)) {
                Text(
                    text = "Dashboard",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Sua evolução em tempo real",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            
            // Percentage Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Bar (representing ████████████████████)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(5.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = animatedProgress.coerceIn(0f, 1f))
                    .height(10.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        ),
                        RoundedCornerShape(5.dp)
                    )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hoje title
        Text(
            text = "HOJE",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // Task Items list
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InteractiveTaskItem(
                text = "Academia",
                checked = academiaChecked,
                onCheckedChange = { academiaChecked = it }
            )
            InteractiveTaskItem(
                text = "Água",
                checked = aguaChecked,
                onCheckedChange = { aguaChecked = it }
            )
            InteractiveTaskItem(
                text = "Meditação",
                checked = meditacaoChecked,
                onCheckedChange = { meditacaoChecked = it }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bottom Metrics (XP & Streak)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // XP Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFFFD54F).copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "XP",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "${animatedXp} XP",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Exp. total",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Streak Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFFF7043).copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = "Streak",
                            tint = Color(0xFFF4511E),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "31 dias",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Ofensiva",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveTaskItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isInteracted = isPressed || isHovered

    val scale by animateFloatAsState(
        targetValue = if (isInteracted) 1.02f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "itemScale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.scaleX = scale
                this.scaleY = scale
            }
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (checked) MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
            )
            .border(
                width = 1.dp,
                color = if (checked) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = { onCheckedChange(!checked) }
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Circle Checkbox
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    if (checked) Color(0xFF66BB6A).copy(alpha = 0.15f)
                    else Color.Transparent,
                    CircleShape
                )
                .border(
                    width = 2.dp,
                    color = if (checked) Color(0xFF66BB6A) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Checked",
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal
            ),
            color = if (checked) MaterialTheme.colorScheme.onBackground
            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun InteractiveAiAnalysisCard(modifier: Modifier = Modifier) {
    var selectedAnalysisIndex by remember { mutableStateOf(0) }

    val analyses = listOf(
        AnalysisItem(
            name = "Hábitos",
            icon = Icons.Default.CheckCircle,
            analysisText = "A IA detectou consistência em 'Academia' e 'Água'. Sugestão: adicione um hábito de leitura pós-almoço para otimizar momentos de baixo foco.",
            iconColor = Color(0xFF66BB6A)
        ),
        AnalysisItem(
            name = "Desempenho",
            icon = Icons.Default.TrendingUp,
            analysisText = "Seu desempenho cresceu 14% esta semana. Seus picos de conclusão ocorrem na terça e quinta de manhã. Mantenha as tarefas difíceis nestes dias!",
            iconColor = Color(0xFF29B6F6)
        ),
        AnalysisItem(
            name = "Horários",
            icon = Icons.Default.Schedule,
            analysisText = "Você costuma treinar às 07:30. A IA agendou lembretes proativos às 07:15 para garantir que você não perca o ritmo matinal.",
            iconColor = Color(0xFFFFCA28)
        ),
        AnalysisItem(
            name = "Procrastinação",
            icon = Icons.Default.Warning,
            analysisText = "Identificado padrão de atraso nas tarefas administrativas na sexta-feira. Sugestão: reduza essas metas para blocos menores de 15 min.",
            iconColor = Color(0xFFFF7043)
        ),
        AnalysisItem(
            name = "Evolução",
            icon = Icons.Default.Stars,
            analysisText = "Com 31 dias de ofensiva, você alcançou o ranking 'Mestre de Hábitos' de nível 4! Continue assim para desbloquear novos temas exclusivos.",
            iconColor = Color(0xFFAB47BC)
        )
    )

    val currentAnalysis = analyses[selectedAnalysisIndex]

    LifeOSCard(
        modifier = modifier
            .widthIn(max = 480.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
        shape = RoundedCornerShape(28.dp),
        contentPadding = 20.dp,
        interactive = true,
        onClick = {}
    ) {
        // Card Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🤖",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1.0f)) {
                Text(
                    text = "LifeOS AI",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "A inteligência artificial analisa:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Horizontal clickable indicators of analyzed points
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            analyses.forEachIndexed { index, item ->
                val isSelected = index == selectedAnalysisIndex
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val isHovered by interactionSource.collectIsHoveredAsState()
                val isInteracted = isPressed || isHovered

                val scale by animateFloatAsState(
                    targetValue = if (isInteracted) 1.08f else 1.00f,
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    label = "tabScale"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            this.scaleX = scale
                            this.scaleY = scale
                        }
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) item.iconColor.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) item.iconColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { selectedAnalysisIndex = index }
                        )
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name,
                            tint = if (isSelected) item.iconColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 9.sp
                            ),
                            color = if (isSelected) MaterialTheme.colorScheme.onBackground
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // AI Generated suggestion box (e gera sugestões automaticamente)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.04f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Suggestion",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SUGESTÃO DA IA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                // Crossfade animation for analysis text description
                AnimatedContent(
                    targetState = currentAnalysis.analysisText,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                    },
                    label = "textAnim"
                ) { text ->
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

data class AnalysisItem(
    val name: String,
    val icon: ImageVector,
    val analysisText: String,
    val iconColor: Color
)

