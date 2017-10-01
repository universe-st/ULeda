package ecnu.uleda.model

import java.io.Serializable

/**
 * Created by jimmyhsu on 2017/5/19.
 */

data class UActivity(
    var title: String,
    var lat: Double,
    var lon: Double,
    var location: String,
    var tag: String,
    var authorId: Int,
    var authorUsername: String,
    var avatar: String,
    var description: String?,
    var holdTime: Long,
    var takersCount: Int,
    var imgUrls: ArrayList<String>,
    var id: Int
): Serializable