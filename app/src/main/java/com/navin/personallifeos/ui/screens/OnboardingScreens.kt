package com.navin.personallifeos.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.VideogameAsset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.navin.personallifeos.ui.theme.CardCream
import com.navin.personallifeos.ui.theme.InkMuted
import com.navin.personallifeos.ui.theme.LavenderSoft
import com.navin.personallifeos.ui.theme.Moss
import com.navin.personallifeos.ui.theme.MossSoft
import kotlinx.coroutines.delay

private const val TotalSetupSteps = 4

@Composable
fun OnboardingFlow(
    onFinish: (
        name: String,
        focusAreas: Set<String>,
        lifeAreas: Set<String>,
        morningBrief: Boolean,
        eveningReflection: Boolean,
    ) -> Unit,
) {
    var step by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("Navin") }
    var focusAreas by remember { mutableStateOf(setOf("Build projects", "Learn & grow")) }
    var lifeAreas by remember { mutableStateOf(setOf("App Building", "Game Development")) }
    var customLifeArea by remember { mutableStateOf("") }
    var morningBrief by remember { mutableStateOf(true) }
    var eveningReflection by remember { mutableStateOf(true) }

    when (step) {
        0 -> WelcomeStep(onContinue = { step = 1 })
        1 -> FocusStep(
            selected = focusAreas,
            onToggle = { focusAreas = focusAreas.toggle(it) },
            onBack = { step = 0 },
            onContinue = { step = 2 },
        )
        2 -> LifeAreasStep(
            selected = lifeAreas,
            customArea = customLifeArea,
            onCustomAreaChange = { customLifeArea = it },
            onToggle = { lifeAreas = lifeAreas.toggle(it) },
            onAddCustom = {
                val clean = customLifeArea.trim()
                if (clean.isNotEmpty()) {
                    lifeAreas = lifeAreas + clean
                    customLifeArea = ""
                }
            },
            onBack = { step = 1 },
            onContinue = { step = 3 },
        )
        3 -> PersonalizeStep(
            name = name,
            onNameChange = { name = it },
            morningBrief = morningBrief,
            onMorningBriefChange = { morningBrief = it },
            eveningReflection = eveningReflection,
            onEveningReflectionChange = { eveningReflection = it },
            onBack = { step = 2 },
            onContinue = { step = 4 },
        )
        else -> ReadyStep(
            name = name,
            onReady = {
                onFinish(name, focusAreas, lifeAreas, morningBrief, eveningReflection)
            },
        )
    }
}

private fun Set<String>.toggle(value: String): Set<String> =
    if (contains(value)) this - value else this + value

@Composable
private fun WelcomeStep(onContinue: () -> Unit) {
    SetupScaffold(
        progress = null,
        onBack = null,
        trailing = null,
        bottomContent = {
            PrimarySetupButton("Get started", onContinue)
            Text(
                "Private by default · Built around your life",
                fontSize = 11.sp,
                color = InkMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            )
        },
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            "A place for the life\nyou’re building.",
            fontSize = 39.sp,
            lineHeight = 42.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.9).sp,
            color = Color(0xFF252521),
        )
        Text(
            "Plans, ideas, projects, memories and growth — connected without turning your life into a productivity dashboard.",
            fontSize = 16.sp,
            lineHeight = 24.sp,
            color = Color(0xFF6F6A61),
            modifier = Modifier.padding(top = 18.dp),
        )

        Surface(
            modifier = Modifier.fillMaxWidth().padding(top = 34.dp),
            color = CardCream,
            shape = RoundedCornerShape(28.dp),
            shadowElevation = 3.dp,
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(15.dp)) {
                SetupPreviewRow("01", "Today", "See what deserves your attention now.", MossSoft)
                SetupPreviewRow("02", "Capture", "Write or speak naturally. Sort it after.", LavenderSoft)
                SetupPreviewRow("03", "Journey", "Keep the story behind what you’re building.", Color(0xFFF4E8D0))
            }
        }
    }
}

@Composable
private fun FocusStep(
    selected: Set<String>,
    onToggle: (String) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
) {
    SetupScaffold(
        progress = 1,
        onBack = onBack,
        trailing = "Skip",
        onTrailing = onContinue,
        bottomContent = {
            PrimarySetupButton(
                if (selected.isEmpty()) "Continue" else "Continue · ${selected.size} selected",
                onContinue,
            )
        },
    ) {
        SetupQuestion(
            eyebrow = "BUILD YOUR SPACE",
            title = "What do you want more of in your life?",
            body = "Choose what you’d like this space to help you with. You can change everything later.",
        )

        val options = listOf(
            Triple("Build projects", "Turn ideas into things you actually finish.", Icons.Outlined.RocketLaunch),
            Triple("Remember my life", "Keep meaningful days, thoughts and memories.", Icons.Outlined.AutoStories),
            Triple("Learn & grow", "Make skills and hobbies visible over time.", Icons.Outlined.School),
            Triple("Stay organized", "Tasks and reminders without the clutter.", Icons.Outlined.TaskAlt),
            Triple("Build habits", "Create routines without guilt-heavy streaks.", Icons.Outlined.CheckCircle),
            Triple("Explore ideas", "Keep sparks, notes and possibilities connected.", Icons.Outlined.Lightbulb),
        )

        Column(modifier = Modifier.padding(top = 22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            options.chunked(2).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { (title, body, icon) ->
                        SelectableSetupCard(
                            title = title,
                            body = body,
                            icon = icon,
                            selected = selected.contains(title),
                            onClick = { onToggle(title) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LifeAreasStep(
    selected: Set<String>,
    customArea: String,
    onCustomAreaChange: (String) -> Unit,
    onToggle: (String) -> Unit,
    onAddCustom: () -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
) {
    SetupScaffold(
        progress = 2,
        onBack = onBack,
        trailing = "Skip",
        onTrailing = onContinue,
        bottomContent = {
            PrimarySetupButton(
                if (selected.isEmpty()) "Continue" else "Continue · ${selected.size} added",
                onContinue,
            )
        },
    ) {
        SetupQuestion(
            eyebrow = "YOUR LIFE, RIGHT NOW",
            title = "What’s already part of your world?",
            body = "Pick a few areas you care about. These become the first threads your app learns to connect.",
        )

        val areas = listOf(
            Triple("App Building", Icons.Outlined.Psychology, MossSoft),
            Triple("Game Development", Icons.Outlined.VideogameAsset, Color(0xFFF4E8D0)),
            Triple("Filmmaking", Icons.Outlined.Edit, LavenderSoft),
            Triple("Health", Icons.Outlined.FavoriteBorder, Color(0xFFF2E5DC)),
            Triple("Reading", Icons.Outlined.AutoStories, Color(0xFFE9EEE7)),
            Triple("Learning", Icons.Outlined.School, Color(0xFFEDE8F1)),
        )

        Column(modifier = Modifier.padding(top = 22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            areas.chunked(2).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { (title, icon, tint) ->
                        LifeAreaCard(
                            title = title,
                            icon = icon,
                            tint = tint,
                            selected = selected.contains(title),
                            onClick = { onToggle(title) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = customArea,
                onValueChange = onCustomAreaChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text("Add your own") },
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardCream,
                    unfocusedContainerColor = CardCream,
                    focusedBorderColor = Moss,
                    unfocusedBorderColor = Color(0xFFD8D1C6),
                ),
            )
            Surface(
                onClick = onAddCustom,
                shape = RoundedCornerShape(18.dp),
                color = MossSoft,
                modifier = Modifier.size(56.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("+", fontSize = 26.sp, color = Moss, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun PersonalizeStep(
    name: String,
    onNameChange: (String) -> Unit,
    morningBrief: Boolean,
    onMorningBriefChange: (Boolean) -> Unit,
    eveningReflection: Boolean,
    onEveningReflectionChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
) {
    SetupScaffold(
        progress = 3,
        onBack = onBack,
        trailing = null,
        bottomContent = { PrimarySetupButton("Make it mine", onContinue) },
    ) {
        SetupQuestion(
            eyebrow = "MAKE IT YOURS",
            title = "How should this space meet you each day?",
            body = "A little personalization makes the first week feel less like a new app and more like your own place.",
        )

        Text("What should I call you?", fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 28.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth().padding(top = 9.dp),
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardCream,
                unfocusedContainerColor = CardCream,
                focusedBorderColor = Moss,
                unfocusedBorderColor = Color(0xFFD8D1C6),
            ),
        )

        Text("Your rhythm", fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 24.dp, bottom = 9.dp))
        RhythmCard(
            title = "Morning brief",
            body = "A calm view of what matters today.",
            checked = morningBrief,
            onCheckedChange = onMorningBriefChange,
        )
        RhythmCard(
            title = "Evening reflection",
            body = "A quiet prompt to remember the day.",
            checked = eveningReflection,
            onCheckedChange = onEveningReflectionChange,
            modifier = Modifier.padding(top = 10.dp),
        )

        Surface(
            modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
            shape = RoundedCornerShape(20.dp),
            color = LavenderSoft,
        ) {
            Text(
                "We’ll ask for notifications or microphone access only when you first use a feature that needs it.",
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = Color(0xFF625A6D),
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Composable
private fun ReadyStep(name: String, onReady: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1700)
        onReady()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Surface(
            modifier = Modifier.size(92.dp),
            shape = CircleShape,
            color = Moss,
            shadowElevation = 6.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(46.dp))
            }
        }
        Text(
            if (name.isBlank()) "Your observatory is ready." else "Your observatory is ready, ${name.trim()}.",
            fontSize = 31.sp,
            lineHeight = 35.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 26.dp),
        )
        Text(
            "Goals connected · Life areas added · Journey ready · Today prepared",
            fontSize = 13.sp,
            lineHeight = 20.sp,
            color = InkMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 14.dp),
        )
        Text("Building your space…", fontSize = 12.sp, color = Moss, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 28.dp))
    }
}

@Composable
private fun SetupScaffold(
    progress: Int?,
    onBack: (() -> Unit)?,
    trailing: String?,
    onTrailing: (() -> Unit)? = null,
    bottomContent: @Composable () -> Unit,
    content: @Composable Column.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (onBack != null) {
                    Surface(onClick = onBack, shape = CircleShape, color = CardCream, modifier = Modifier.size(40.dp)) {
                        Box(contentAlignment = Alignment.Center) { Text("‹", fontSize = 25.sp, color = Color(0xFF4F4B45)) }
                    }
                }
            }
            if (progress != null) {
                Text("$progress of $TotalSetupSteps", fontSize = 12.sp, color = InkMuted, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                if (trailing != null && onTrailing != null) {
                    Surface(onClick = onTrailing, color = Color.Transparent) {
                        Text(trailing, fontSize = 12.sp, color = Moss, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }

        if (progress != null) {
            Row(modifier = Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(TotalSetupSteps) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(
                                if (index < progress) Moss else Color(0xFFE3DED4),
                                RoundedCornerShape(999.dp),
                            ),
                    )
                }
            }
        }

        Column(modifier = Modifier.weight(1f), content = content)
        Column(modifier = Modifier.fillMaxWidth(), content = bottomContent)
    }
}

@Composable
private fun SetupQuestion(eyebrow: String, title: String, body: String) {
    Text(eyebrow, fontSize = 11.sp, letterSpacing = 1.35.sp, color = Moss, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 24.dp))
    Text(title, fontSize = 31.sp, lineHeight = 34.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.6).sp, modifier = Modifier.padding(top = 8.dp))
    Text(body, fontSize = 14.sp, lineHeight = 21.sp, color = Color(0xFF6D685F), modifier = Modifier.padding(top = 10.dp))
}

@Composable
private fun SetupPreviewRow(index: String, title: String, body: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(13.dp)) {
        Box(modifier = Modifier.size(42.dp).background(tint, RoundedCornerShape(15.dp)), contentAlignment = Alignment.Center) {
            Text(index, fontSize = 11.sp, color = Moss, fontWeight = FontWeight.ExtraBold)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Text(body, fontSize = 12.sp, lineHeight = 17.sp, color = InkMuted, modifier = Modifier.padding(top = 3.dp))
        }
    }
}

@Composable
private fun SelectableSetupCard(
    title: String,
    body: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(105.dp),
        shape = RoundedCornerShape(21.dp),
        color = if (selected) MossSoft else CardCream,
        border = BorderStroke(1.dp, if (selected) Moss.copy(alpha = 0.35f) else Color(0xFFE7E0D5)),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = if (selected) Moss else Color(0xFF777168), modifier = Modifier.size(20.dp))
                if (selected) {
                    Spacer(Modifier.weight(1f))
                    Text("✓", color = Moss, fontWeight = FontWeight.ExtraBold)
                }
            }
            Text(title, fontSize = 13.sp, lineHeight = 16.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 8.dp))
            Text(body, fontSize = 10.5.sp, lineHeight = 14.sp, color = InkMuted, modifier = Modifier.padding(top = 3.dp), maxLines = 2)
        }
    }
}

@Composable
private fun LifeAreaCard(
    title: String,
    icon: ImageVector,
    tint: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(78.dp),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) tint else CardCream,
        border = BorderStroke(1.dp, if (selected) Moss.copy(alpha = 0.34f) else Color(0xFFE7E0D5)),
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(11.dp)) {
            Box(modifier = Modifier.size(38.dp).background(tint, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Moss, modifier = Modifier.size(20.dp))
            }
            Text(title, fontSize = 12.sp, lineHeight = 15.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
            if (selected) Text("✓", color = Moss, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun RhythmCard(
    title: String,
    body: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = CardCream, border = BorderStroke(1.dp, Color(0xFFE7E0D5))) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                Text(body, fontSize = 11.5.sp, lineHeight = 16.sp, color = InkMuted, modifier = Modifier.padding(top = 3.dp))
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Moss),
            )
        }
    }
}

@Composable
private fun PrimarySetupButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Moss),
    ) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
    }
}
