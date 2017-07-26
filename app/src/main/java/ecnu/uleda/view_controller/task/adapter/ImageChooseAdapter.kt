package ecnu.uleda.view_controller.task.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import ecnu.uleda.R
import ecnu.uleda.tool.UPublicTool
import java.io.File

/**
 * Created by jimmyhsu on 2017/5/20.
 */
class ImageChooseAdapter(context: Context, val imgPaths: List<String>, var imgWidth: Int, val maxImg: Int):
        ArrayAdapter<String>(context, -1, imgPaths) {

    init {
        imgWidth = UPublicTool.dp2px(context, imgWidth).toInt()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView = ImageView(context)
        imageView.layoutParams = ViewGroup.LayoutParams(imgWidth, imgWidth)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        if (position == count - 1 && imgPaths.size < maxImg) {
            imageView.setImageResource(R.drawable.choose1)
        } else {
            val file = File(getItem(position))
            Glide.with(context).load(file).into(imageView)
        }
        return imageView
    }

    override fun getCount(): Int {
        if (imgPaths.size < maxImg) {
            return super.getCount() + 1
        }
        return maxImg
    }

}
inline fun <reified T> T.log(message: String) {
    Log.e(T::class.simpleName, message)
}