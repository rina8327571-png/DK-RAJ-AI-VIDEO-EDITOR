package com.dkraj.videoeditor

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.runtime.DisposableEffect

private val Dark = Color(0xFF101116)
private val Panel = Color(0xFF20232D)
private val Accent = Color(0xFFFF1744)
private val Gold = Color(0xFFFFC107)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoEditorApp()
        }
    }
}

@Composable
fun VideoEditorApp() {
    val context = LocalContext.current
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    var videoName by remember { mutableStateOf("कोई वीडियो नहीं चुना") }

    val player = remember {
        ExoPlayer.Builder(context).build()
    }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            videoUri = uri
            videoName = uri.lastPathSegment ?: "Selected Video"
            player.setMediaItem(MediaItem.fromUri(uri))
            player.prepare()
            player.playWhenReady = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Dark)
    ) {
        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE50914))
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "DK RAJ",
                color = Color.White,
                fontSize = 21.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                "AI VIDEO EDITOR",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White
            )
        }

        // VIDEO PREVIEW
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(10.dp)
                .background(Color.Black, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (videoUri != null) {
                AndroidView(
                    factory = { viewContext ->
                        PlayerView(viewContext).apply {
                            this.player = player
                            useController = true
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Movie,
                        contentDescription = null,
                        tint = Gold,
                        modifier = Modifier.size(65.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "DK RAJ VIDEO STUDIO",
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "अपना वीडियो चुनकर एडिटिंग शुरू करें",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // VIDEO INFORMATION
        Text(
            text = videoName,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 14.dp),
            fontSize = 12.sp
        )

        // MAIN TOOL BUTTONS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ToolButton("Video", Icons.Default.VideoLibrary) {
                picker.launch(arrayOf("video/*"))
            }
            ToolButton("Audio", Icons.Default.MusicNote) {
                picker.launch(arrayOf("audio/*"))
            }
            ToolButton("Text", Icons.Default.TextFields) {}
            ToolButton("AI Tools", Icons.Default.AutoAwesome) {}
        }

        // TIMELINE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Panel)
                .padding(12.dp)
        ) {
            Text(
                "MULTI-LAYER TIMELINE",
                color = Gold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            TimelineTrack("VIDEO", Accent)
            TimelineTrack("TEXT", Color(0xFFFFC107))
            TimelineTrack("AUDIO", Color(0xFF00BFA5))

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "पहले वीडियो को चुनें और Preview देखें",
                color = Color.LightGray,
                fontSize = 11.sp
            )
        }

        // EXPORT BUTTON
        Button(
            onClick = {
                if (videoUri == null) {
                    picker.launch(arrayOf("video/*"))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Accent
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.FileDownload, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("EXPORT VIDEO", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ToolButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .background(Panel, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = title, tint = Gold)
        Spacer(modifier = Modifier.height(5.dp))
        Text(title, color = Color.White, fontSize = 10.sp)
    }
}

@Composable
fun TimelineTrack(name: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            name,
            color = Color.White,
            fontSize = 11.sp,
            modifier = Modifier.width(60.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(28.dp)
                .background(color.copy(alpha = 0.8f), RoundedCornerShape(5.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                "  + Add $name Layer",
                color = Color.Black,
                fontSize = 10.sp
            )
        }
    }
}
