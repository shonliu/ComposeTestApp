package com.example.genesistestproject


import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.media3.common.util.UnstableApi
import com.example.genesistestproject.ui.theme.GenesisTestProjectTheme
import java.io.IOException
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    private val pickVideo =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val path = FileUtils.getPath(this, uri).toUri()
                viewModel.initializePlayer(path)
            }
        }

    private val viewModel by viewModels<VideoPlayerViewModel>()

    private val shadowColor = Color(0x60000000)
    private val leftBoxColor = Color(0xff0ff000)
    private val rightBoxColor = Color(0xff0000ff)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GenesisTestProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RangeSelector(
                        modifier = Modifier
                            .background(Color.White)
                            .height(100.dp),
                        length = 100,
                        onStartChange = {},
                        onEndChange = {},
                        onCurrentPositionChange = {}
                    )
                }
            }
        }


    }

    @Composable
    fun VideoPlayerScreen(viewModel: VideoPlayerViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (viewModel.videoUri == Uri.EMPTY) {
                Button(
                    onClick = {
                        pickVideo.launch("video/*")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text("Pick a Video")
                }
            } else {
                VideoPlayer(viewModel)
            }
        }
    }

    @OptIn(UnstableApi::class) @Composable
    fun VideoPlayer(viewModel: VideoPlayerViewModel) {
        val context = LocalContext.current
        val mediaPlayer = remember { MediaPlayer() }

        DisposableEffect(viewModel.videoUri) {
            viewModel.initializePlayer(viewModel.videoUri!!)
            onDispose {
                viewModel.releasePlayer()
            }
        }

        LaunchedEffect(mediaPlayer) {
            try {

//                val surface = android.view.Surface(surfaceHolder.surface)
                mediaPlayer.setDataSource(context, viewModel.videoUri!!)
                mediaPlayer.prepare()
                mediaPlayer.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        DisposableEffect(mediaPlayer) {
            onDispose {
                mediaPlayer.release()
            }
        }

//        DisposableEffect(
//            AndroidView(factory = {
//                PlayerView(context).apply {
//                    hideController()
//                    useController = false
//                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
//
//                    player = mediaPlayer
//                    layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//                }
//            })
//        ) {
//            onDispose { exoPlayer.release() }
//        }

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(Color.Black)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Video Playing...")
            }
        }
    }


    @Composable
    fun RangeSelector(
        modifier: Modifier = Modifier,
        length: Long,
        onStartChange: (Long) -> Unit,
        onEndChange: (Long) -> Unit,
        onCurrentPositionChange: (Long) -> Unit
    ) {
        val startPosition = remember { mutableLongStateOf(0) }
        val endPosition = remember { mutableLongStateOf(length) }
        val view = LocalView.current

        var offsetLeftX by remember { mutableStateOf(0f) }

        ConstraintLayout(modifier = modifier) {
            val (leftShade, leftBox, rightBox, rightShade) = createRefs()

            ShadeBackground(
                modifier = Modifier
                    .constrainAs(leftShade) {
                        linkTo(
                            start = parent.start,
                            top = parent.top,
                            bottom = parent.bottom,
                            end = leftBox.start
                        )
                        height = Dimension.fillToConstraints
                        width = Dimension.fillToConstraints
                    }
            )
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .background(leftBoxColor)
                    .offset { IntOffset(offsetLeftX.roundToInt(), 0) }
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            offsetLeftX += delta
                            Log.d("LeftBox", "Offset $offsetLeftX")
                        }
                    ).clipToBounds()
                    .constrainAs(leftBox) {
//                        linkTo(
//                            top = parent.top,
//                            bottom = parent.bottom
//                        )
//                        start.linkTo(parent.start, margin = 30.dp)
                    }
            ) {

            }
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .fillMaxHeight()
                    .background(rightBoxColor)
                    .constrainAs(rightBox) {
                        linkTo(
                            top = parent.top,
                            bottom = parent.bottom
                        )
                        end.linkTo(parent.end, margin = 30.dp)
                    }
            ) {

            }
            ShadeBackground(
                modifier = Modifier
                    .constrainAs(rightShade) {
                        linkTo(
                            start = rightBox.end,
                            top = parent.top,
                            bottom = parent.bottom,
                            end = parent.end
                        )
                        height = Dimension.fillToConstraints
                        width = Dimension.fillToConstraints
                    }
            )
        }
    }

    @Composable
    fun ShadeBackground(modifier: Modifier) {
        Box(modifier = modifier.background(shadowColor))
    }

    @Preview(heightDp = 40, widthDp = 200)
    @Composable
    fun RangeSelectorPreview() {
        RangeSelector(
            modifier = Modifier.background(Color.White),
            length = 100,
            onStartChange = {},
            onEndChange = {},
            onCurrentPositionChange = {}
        )
    }


}

class VideoPlayerViewModel : ViewModel() {
    var videoUri: Uri? by mutableStateOf(Uri.EMPTY)
        private set

    fun initializePlayer(uri: Uri) {
        videoUri = uri
    }

    fun releasePlayer() {
        videoUri = Uri.EMPTY
    }
}
