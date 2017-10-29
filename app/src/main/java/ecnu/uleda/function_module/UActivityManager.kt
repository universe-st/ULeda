package ecnu.uleda.function_module

import android.util.Log
import ecnu.uleda.model.UActivity

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
    private var tag = "全部"
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
                    UActivity(it.getString("act_title"),
                            it.getDouble("lat"),
                            it.getDouble("lon"),
                            it.getString("location"),
                            it.getString("tag"),
                            it.getInt("author_id"),
                            "no",
                            "no",
                            it.getString("description"),
                            it.getLong("active_time"),
                            it.getInt("taker_count_limit"),
                            arrayListOf(),
                            it.getInt("act_id"),
                            it.getInt("status"))
                }
                .forEach { activityList.add(it) }
        lastIndex += length
        if (length < pageSize) hasMore = false
    }

    fun loadMoreActivityInList(): Int {
        if (!hasMore) return 0
        val resp = ServerAccessApi.getActivities(uoc.id, uoc.passport, tag, lastIndex.toString(), pageSize.toString()) ?: return 0
        val length = resp.length()
        (0..length - 1)
                .map { resp.getJSONObject(it) }
                .map {
                    UActivity(it.getString("act_title"),
                            it.getDouble("lat"),
                            it.getDouble("lon"),
                            it.getString("location"),
                            it.getString("tag"),
                            it.getInt("author_id"),
                            "no",
                            "no",
                            it.getString("description"),
                            it.getLong("active_time"),
                            it.getInt("taker_count_limit"),
                            arrayListOf(),
                            it.getInt("act_id"),
                            it.getInt("status"))
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