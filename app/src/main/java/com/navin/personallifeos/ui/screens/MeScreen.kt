package com.navin.personallifeos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.navin.personallifeos.ui.theme.CardCream
import com.navin.personallifeos.ui.theme.GoldSoft
import com.navin.personallifeos.ui.theme.LavenderSoft
import com.navin.personallifeos.ui.theme.Moss
import com.navin.personallifeos.ui.theme.MossSoft
import com.navin.personallifeos.ui.viewmodel.HomeViewModel

@Composable
fun MeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val tasks by viewModel.allTasks.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val journal by viewModel.journal.collectAsState()
    val ideas by viewModel.ideas.collectAsState()
    val completed = tasks.count { it.completedAt != null }

    Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(start = 18.dp, end = 18.dp, top = 20.dp, bottom = 20.dp)) {
        Text("YOUR PERSONAL SPACE", fontSize = 12.sp, letterSpacing = 1.5.sp, color = Color(0xFF7A756B), fontWeight = FontWeight.Bold)
        Text("Me", fontFamily = FontFamily.Serif, fontSize = 34.sp, lineHeight = 35.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 6.dp))
        Text("A living picture of what you’re building and becoming.", fontSize = 14.sp, color = Color(0xFF7A756B), modifier = Modifier.padding(top = 2.dp))

        Surface(modifier = Modifier.fillMaxWidth().padding(top = 18.dp), shape = RoundedCornerShape(26.dp), color = Moss, shadowElevation = 4.dp) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(18.dp)).background(Color(0xFFEDF1E9)), contentAlignment = Alignment.Center) {
                        Text("N", color = Moss, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Column {
                        Text("Navin", fontSize = 23.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Creator · Developer · Learner", fontSize = 13.sp, color = Color.White.copy(alpha = 0.80f), modifier = Modifier.padding(top = 3.dp))
                    }
                }
                Surface(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), shape = RoundedCornerShape(18.dp), color = Color.White.copy(alpha = 0.12f)) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp)) {
                        Text("CURRENT CHAPTER", fontSize = 10.sp, letterSpacing = 1.2.sp, color = Color.White.copy(alpha = 0.72f), fontWeight = FontWeight.Bold)
                        Text("Building more than consuming.", fontFamily = FontFamily.Serif, fontSize = 19.sp, lineHeight = 24.sp, color = Color.White, modifier = Modifier.padding(top = 5.dp))
                    }
                }
            }
        }

        Row(modifier = Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(Modifier.weight(1f), "Active projects", projects.size.toString(), "${projects.take(2).size} touched this week")
            MetricCard(Modifier.weight(1f), "Skills growing", "12", "Game dev leads this month")
        }

        MeSectionTitle("Current goal")
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), color = CardCream, shadowElevation = 1.dp) {
            Row(modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.size(42.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFEEE4C8)), contentAlignment = Alignment.Center) { Text("↗", color = Color(0xFF9A7A38), fontWeight = FontWeight.ExtraBold) }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Publish my first Android game", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Night Drive · 4 milestones remaining", fontSize = 11.sp, color = Color(0xFF777166), modifier = Modifier.padding(top = 3.dp))
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(6.dp).background(Color(0xFFEAE4D8), RoundedCornerShape(99.dp))) {
                        Box(modifier = Modifier.fillMaxWidth(0.68f).height(6.dp).background(Moss, RoundedCornerShape(99.dp)))
                    }
                }
                Text("68%", fontSize = 12.sp, color = Moss, fontWeight = FontWeight.ExtraBold)
            }
        }

        MeSectionTitle("What I’m growing")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GrowthChip("Game Development · 24h", MossSoft, Color(0xFF5D7258))
                GrowthChip("Filmmaking · 18h", Color(0xFFF0EBE2), Color(0xFF645F57))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GrowthChip("Writing · 11h", LavenderSoft, Color(0xFF6F647F))
                GrowthChip("App Building · 31h", GoldSoft, Color(0xFF9D7635))
            }
        }

        Surface(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), shape = RoundedCornerShape(22.dp), color = Color(0xFFF6EBD6)) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                Box(modifier = Modifier.size(42.dp).clip(RoundedCornerShape(15.dp)).background(Color(0xFFD9B56E)), contentAlignment = Alignment.Center) { Text("★", color = Color.White, fontWeight = FontWeight.ExtraBold) }
                Column {
                    Text("Recent achievement", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("100,000+ views on a FrameByNavin video", fontSize = 11.sp, color = Color(0xFF7D6842), modifier = Modifier.padding(top = 3.dp))
                }
            }
        }

        MeSectionTitle("This month")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(Modifier.weight(1f), "Learning sessions", (journal.size + 11).toString(), "+4 vs last month")
            MetricCard(Modifier.weight(1f), "Ideas captured", ideas.size.toString(), "${completed.coerceAtMost(8)} turned into actions")
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun MetricCard(modifier: Modifier, title: String, metric: String, meta: String) {
    Surface(modifier = modifier, shape = RoundedCornerShape(22.dp), color = CardCream, shadowElevation = 1.dp) {
        Column(modifier = Modifier.padding(15.dp)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(metric, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 5.dp))
            Text(meta, fontSize = 11.sp, color = Color(0xFF777166), modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
private fun MeSectionTitle(text: String) {
    Text(text.uppercase(), fontSize = 11.sp, letterSpacing = 1.2.sp, color = Color(0xFF777166), fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(start = 2.dp, top = 18.dp, bottom = 9.dp))
}

@Composable
private fun GrowthChip(text: String, bg: Color, fg: Color) {
    Text(text, fontSize = 12.sp, color = fg, modifier = Modifier.background(bg, RoundedCornerShape(999.dp)).padding(horizontal = 11.dp, vertical = 9.dp))
}
