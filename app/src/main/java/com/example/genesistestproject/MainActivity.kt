package com.example.genesistestproject


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.genesistestproject.slider.LabeledRangeSlider
import com.example.genesistestproject.ui.theme.GenesisTestProjectTheme
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GenesisTestProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }

    }

    @Composable
    fun MainScreen(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val result = remember { mutableStateOf<Uri>(Uri.EMPTY) }
        val isPlaying = remember {
            mutableStateOf<Boolean>(false)
        }

        val videoDuration = remember {
            mutableLongStateOf(-1)
        }
        val videoCurrentPosition = remember {
            mutableLongStateOf(-1)
        }

        var upperSliderBound = remember {
            mutableLongStateOf(0)
        }


        val videoPicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri ->
                uri?.let {
//                    val file = viewModel.createTmpFileFromUri(this, uri, "tempVid")
//                    Log.d("VideoPicker", file.toString())
//                    result.value = file?.toUri() ?: Uri.EMPTY
//                    result.value = FileUtilit.getPath(context, uri).toUri()
//                    val path: String? = FileUtilKt.getValidatedFileUri(context, uri)
//                    val filePath = Uri.parse(path)
                    result.value = uri
                }
            }
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            if (result.value == Uri.EMPTY) {
                VideoPicker {
                    videoPicker.launch("video/*")
                }
            } else {
                Box(contentAlignment = Alignment.Center) {
                    VideoPlayer(uri = result.value) {
                        videoDuration.longValue = viewModel.getDuration()
                    }
                    Button(
                        onClick = { isPlaying.value = viewModel.playPause() },
                        modifier = modifier
                    ) {
                        Text(if (isPlaying.value) "Pause" else "Play")
                    }
                }
            }

            if (videoDuration.longValue > 0) {

                val mainHandler = Handler(Looper.getMainLooper())
                mainHandler.post(object : Runnable {
                    override fun run() {
                        val curPos = viewModel.getCurrentPosition()

                        if (curPos >= upperSliderBound.longValue) {
                            viewModel.playerPause()
                        }

                        videoCurrentPosition.longValue = viewModel.getCurrentPosition()
                        mainHandler.postDelayed(this, 500)


                    }
                })

                Box(contentAlignment = Alignment.Center) {
                    val steps = (0..videoDuration.longValue).step(1000).toList()
                    var lowerBound by rememberSaveable { mutableStateOf(steps[1]) }
                    var upperBound by rememberSaveable { mutableStateOf(steps[steps.size - 2]) }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.stub_img),
                            contentDescription = "first"
                        )
                        Image(
                            painter = painterResource(id = R.drawable.stub_img),
                            contentDescription = "first"
                        )
                        Image(
                            painter = painterResource(id = R.drawable.stub_img),
                            contentDescription = "first"
                        )
                        Image(
                            painter = painterResource(id = R.drawable.stub_img),
                            contentDescription = "first"
                        )
                        Image(
                            painter = painterResource(id = R.drawable.stub_img),
                            contentDescription = "first"
                        )
                        Image(
                            painter = painterResource(id = R.drawable.stub_img),
                            contentDescription = "first"
                        )
                        Image(
                            painter = painterResource(id = R.drawable.stub_img),
                            contentDescription = "first"
                        )
                        Image(
                            painter = painterResource(id = R.drawable.stub_img),
                            contentDescription = "first"
                        )
                    }

                    Slider(
                        value = videoCurrentPosition.longValue.toFloat(),
                        onValueChange = {

                        },
                        valueRange = 0f .. videoDuration.longValue.toFloat()
                    )

                    LabeledRangeSlider(
                        selectedLowerBound = lowerBound,
                        selectedUpperBound = upperBound,
                        steps = steps,
                        onRangeChanged = { lower, upper ->
                            lowerBound = lower
                            upperBound = upper
                            upperSliderBound.longValue = upper
                            Log.i("LabeledRangeSlider", "Updated selected range ${lowerBound..upperBound}")
                            viewModel.scrollTo(lower)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )


                }
            }

        }
    }

    @Composable
    fun VideoPicker(modifier: Modifier = Modifier, onClick: () -> Unit) {
        Button(
            onClick = onClick,
            modifier = modifier
        ) {
            Text("Pick video")
        }
    }

    @Composable
    fun VideoPlayer(uri: Uri, onPlayerReady: () -> Unit) {
        val context = LocalContext.current
        val exoPlayer = remember { getSimpleExoPlayer(context, uri) }

        exoPlayer.addListener(object: Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == STATE_READY) {
                    onPlayerReady()
                }
            }
        })

        viewModel.initExoPlayer(exoPlayer)
        AndroidView(
            modifier = Modifier
                .padding(20.dp),
            factory = { context1 ->
                PlayerView(context1).apply {
                    player = exoPlayer
                    useController = false
                }
            },
        )
    }

    private fun getSimpleExoPlayer(context: Context, uri: Uri): SimpleExoPlayer {
        return SimpleExoPlayer.Builder(context).build().apply {
            val dataSourceFactory = DefaultDataSource.Factory(context)
            val localVideoItem = MediaItem.fromUri(uri)
            val localVideoSource = ProgressiveMediaSource
                .Factory(dataSourceFactory)
                .createMediaSource(localVideoItem)
            this.addMediaSource(localVideoSource)
            this.prepare()
        }
    }

    @Preview()
    @Composable
    fun MainScreenPreview() {
        MainScreen()
    }

}

