package com.shaadow.innertube.models

import kotlinx.serialization.Serializable

@Serializable
data class ButtonRenderer(
    val navigationEndpoint: com.shaadow.innertube.models.NavigationEndpoint?
)
