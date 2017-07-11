package ecnu.uleda.function_module

import android.util.Log
import ecnu.uleda.model.UActivity

/**
 * Created by jimmyhsu on 2017/5/22.
 */
object UActivityManager {
    private val TAG_ALL = "全部"
    private val TAG_SPORT = "运动"
    private val TAG_CLUB = "社团"
    private val TAG_CHARITY = "公益"
    var pageSize = 10
    private val uoc = UserOperatorController.getInstance()
    var activityList = arrayListOf<UActivity>()
    private var lastIndex = 0

    fun refreshActivityInList(tag: String = TAG_ALL) {
        val resp = ServerAccessApi.getActivities(uoc.id, uoc.passport, tag, lastIndex, pageSize)
        val length = resp.length()
        activityList.clear()
        for (i in 0..length - 1) {
            val jsonObj = resp.getJSONObject(i)
            val activity = UActivity(jsonObj.getString("act_title"),
                    jsonObj.getDouble("lat"),
                    jsonObj.getDouble("lon"),
                    jsonObj.getString("location"),
                    jsonObj.getString("tag"),
                    jsonObj.getInt("author_id"),
                    "no",
                    "no",
                    jsonObj.getString("description"),
                    jsonObj.getLong("active_time"),
                    jsonObj.getInt("taker_count_limit"),
                    arrayListOf())
            activityList.add(activity)
        }
    }

}

inline fun <reified T> T.log(message: String) {
    Log.e(T::class.simpleName, message)
}