package com.shaadow.innertube.models

import kotlinx.serialization.Serializable

@Serializable
data class MusicShelfRenderer(
    val bottomEndpoint: com.shaadow.innertube.models.NavigationEndpoint?,
    val contents: List<Content>?,
    val continuations: List<Continuation>?,
    val title: Runs?
) {
    @Serializable
    data class Content(
        val musicResponsiveListItemRenderer: MusicResponsiveListItemRenderer?,
    ) {
        val runs: Pair<List<Runs.Run>, List<List<Runs.Run>>>
            get() = (musicResponsiveListItemRenderer
                ?.flexColumns
                ?.firstOrNull()
                ?.musicResponsiveListItemFlexColumnRenderer
                ?.text
                ?.runs
                ?: emptyList()) to
                    (musicResponsiveListItemRenderer
                        ?.flexColumns
                        ?.getOrNull(1)
                        ?.musicResponsiveListItemFlexColumnRenderer
                        ?.text
                        ?.splitBySeparator()
                        ?: emptyList()
                            )

        val thumbnail: Thumbnail?
            get() = musicResponsiveListItemRenderer
                ?.thumbnail
                ?.musicThumbnailRenderer
                ?.thumbnail
                ?.thumbnails
                ?.firstOrNull()
    }
}
