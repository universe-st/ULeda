package ecnu.uleda.function_module

import android.text.TextUtils
import android.util.Log
import ecnu.uleda.model.UActivity
import ecnu.uleda.tool.UPublicTool
import org.json.JSONException

/**
 * Created by jimmyhsu on 2017/5/22.
 */
object UActivityManager {
    private val TAG_ALL = ""
    private val TAG_SPORT = "运动"
    private val TAG_CLUB = "社团"
    private val TAG_CHARITY = "公益"
    var pageSize = 10
    private val uoc = UserOperatorController.getInstance()
    var activityList = arrayListOf<UActivity>()
    private var lastIndex = 0
    private var tag = ""
    private var hasMore = true

    fun refreshActivityInList(tag: String = TAG_ALL) {
        this.tag = tag
        lastIndex = 0
        hasMore = true
        val resp = ServerAccessApi.getActivities(uoc.id, uoc.passport, tag, lastIndex.toString(), pageSize.toString()) ?: return
        val length = resp.length()
        activityList.clear()
        (0 until length)
                .map { resp.getJSONObject(it) }
                .map {
                    val obj = it
                    val imgUrls = arrayListOf<String>()
                    (1 until 3).map {
                        if (obj.getString("pic" + it) != "null" && !TextUtils.isEmpty(obj.getString("pic" + it))) {
                            imgUrls.add(UPublicTool.BASE_URL_PICTURE + obj.getString("pic" + it))
                        }
                    }
                    var avatar = "no"
                    var username = "no"
                    try {
                        avatar = it.getString("avatar")
                        username = it.getString("username")
                    } catch (e: JSONException) {
                    }
                    UActivity(it.getString("act_title"),
                            it.getDouble("lat"),
                            it.getDouble("lon"),
                            it.getString("location"),
                            it.getString("tag"),
                            it.getInt("author_id"),
                            username,
                            avatar,
                            it.getString("description"),
                            System.currentTimeMillis() + it.getLong("active_time"),
                            it.getInt("taker_count_limit"),
                            imgUrls,
                            it.getInt("act_id"),
                            it.getInt("status"),
                            it.getInt("postdate"))
                }
                .forEach { activityList.add(it) }
        lastIndex += length
        if (length < pageSize) hasMore = false
    }

    fun loadMoreActivityInList(): Int {
        if (!hasMore) return 0
        val resp = ServerAccessApi.getActivities(uoc.id, uoc.passport, tag, lastIndex.toString(), pageSize.toString()) ?: return 0
        val length = resp.length()
        log(length.toString())
        (0..length - 1)
                .map { resp.getJSONObject(it) }
                .map {
                    val obj = it
                    val imgUrls = arrayListOf<String>()
                    (1 until 3).map {
                        if (obj.getString("pic" + it) != "null" && !TextUtils.isEmpty(obj.getString("pic" + it))) {
                            imgUrls.add(UPublicTool.BASE_URL_PICTURE + obj.getString("pic" + it))
                        }
                    }
                    var avatar = "no"
                    var username = "no"
                    try {
                        avatar = it.getString("avatar")
                        username = it.getString("username")
                    } catch (e: JSONException) {
                    }
                    UActivity(it.getString("act_title"),
                            it.getDouble("lat"),
                            it.getDouble("lon"),
                            it.getString("location"),
                            it.getString("tag"),
                            it.getInt("author_id"),
                            username,
                            avatar,
                            it.getString("description"),
                            System.currentTimeMillis() + it.getLong("active_time"),
                            it.getInt("taker_count_limit"),
                            imgUrls,
                            it.getInt("act_id"),
                            it.getInt("status"),
                            it.getInt("postdate"))
                }
                .forEach { activityList.add(it) }
        lastIndex += length
        if (length < pageSize) hasMore = false
        return length
    }

}

inline fun <reified T> T.log(message: String) {
    Log.e(T::class.simpleName, message)
}