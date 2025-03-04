package com.shaadow.tunes.ui.screens.album

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shaadow.tunes.Database
import com.shaadow.tunes.LocalPlayerPadding
import com.shaadow.tunes.LocalPlayerServiceBinder
import com.shaadow.tunes.R
import com.shaadow.tunes.models.ActionInfo
import com.shaadow.tunes.models.LocalMenuState
import com.shaadow.tunes.models.Song
import com.shaadow.tunes.ui.components.CoverScaffold
import com.shaadow.tunes.ui.components.ShimmerHost
import com.shaadow.tunes.ui.components.themed.NonQueuedMediaItemMenu
import com.shaadow.tunes.ui.items.ListItemPlaceholder
import com.shaadow.tunes.ui.items.LocalSongItem
import com.shaadow.tunes.ui.styling.Dimensions
import com.shaadow.tunes.utils.asMediaItem
import com.shaadow.tunes.utils.enqueue
import com.shaadow.tunes.utils.forcePlayAtIndex
import com.shaadow.tunes.utils.forcePlayFromBeginning

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun AlbumSongs(
    browseId: String,
    thumbnailContent: @Composable () -> Unit,
    onGoToArtist: (String) -> Unit
) {
    val binder = LocalPlayerServiceBinder.current
    val menuState = LocalMenuState.current
    val playerPadding = LocalPlayerPadding.current

    var songs: List<Song> by remember { mutableStateOf(emptyList()) }

    LaunchedEffect(Unit) {
        Database.albumSongs(browseId).collect { songs = it }
    }

    LazyColumn(
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp + playerPadding),
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item(key = "thumbnail") {
            CoverScaffold(
                primaryButton = ActionInfo(
                    enabled = songs.isNotEmpty(),
                    onClick = {
                        binder?.stopRadio()
                        binder?.player?.forcePlayFromBeginning(
                            songs.shuffled().map(Song::asMediaItem)
                        )
                    },
                    icon = Icons.Outlined.Shuffle,
                    description = R.string.shuffle
                ),
                secondaryButton = ActionInfo(
                    enabled = songs.isNotEmpty(),
                    onClick = {
                        binder?.player?.enqueue(songs.map(Song::asMediaItem))
                    },
                    icon = Icons.AutoMirrored.Outlined.PlaylistPlay,
                    description = R.string.enqueue
                ),
                content = thumbnailContent
            )
        }

        item(key = "spacer") {
            Spacer(modifier = Modifier.height(16.dp))
        }

        itemsIndexed(
            items = songs,
            key = { _, song -> song.id }
        ) { index, song ->
            LocalSongItem(
                song = song,
                onClick = {
                    binder?.stopRadio()
                    binder?.player?.forcePlayAtIndex(
                        songs.map(Song::asMediaItem),
                        index
                    )
                },
                onLongClick = {
                    menuState.display {
                        NonQueuedMediaItemMenu(
                            onDismiss = menuState::hide,
                            mediaItem = song.asMediaItem,
                            onGoToArtist = onGoToArtist
                        )
                    }
                },
                thumbnailContent = {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(Dimensions.mediumOpacity)
                    )
                }
            )
        }

        if (songs.isEmpty()) {
            item(key = "loading") {
                ShimmerHost {
                    repeat(4) {
                        ListItemPlaceholder()
                    }
                }
            }
        }
    }
}
