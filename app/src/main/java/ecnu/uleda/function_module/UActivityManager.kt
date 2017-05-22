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
        log(resp)
    }

}

inline fun <reified T> T.log(message: String) {
    Log.e(T::class.simpleName, message)
}