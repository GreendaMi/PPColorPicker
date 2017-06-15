package top.greendami.mykotlinapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 *
list.addItemDecoration(SectionDecoration(this, datas, R.layout.header, object : GroupListener() {
override fun isFirst(position: Int): Boolean {
return position % 5 == 0
}

override fun setContnt(contentView: ViewGroup, position: Int) {
(contentView.findViewById(R.id.text) as TextView).text = "标题：" + datas[position]
}

}))
 * Created by GreendaMi on 2017/6/14.
 */
class SectionDecoration<T>(var context: Context, var dataList: ArrayList<T>, val layoutId: Int, val groupListener: GroupListener,var isFloat : Boolean = true, var sectionLayout: ViewGroup = LayoutInflater.from(context).inflate(layoutId, null) as ViewGroup) : RecyclerView.ItemDecoration() {

    var lastBitmap: Bitmap? = null
    var firstTop: Int = 0

    init {
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        sectionLayout.layoutParams = layoutParams
        sectionLayout.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
    }

    //绘制分割区域
    override fun onDraw(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.onDrawOver(c, parent, state)
        val left = parent!!.paddingLeft
        for (i: Int in 0..parent.childCount) {
            if (groupListener.isFirst(parent.getChildAdapterPosition(parent.getChildAt(i)))) {
                //设置内容
                groupListener.setContnt(sectionLayout, parent.getChildAdapterPosition(parent.getChildAt(i)))
                //设置内容后重新测量（此处默认父布局宽度）
                sectionLayout.measure(
                        View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
                sectionLayout.layout(0, 0, parent.width - parent.paddingRight, sectionLayout.measuredHeight)
                sectionLayout.isDrawingCacheEnabled = true
                sectionLayout.buildDrawingCache(true)
                c!!.drawBitmap(sectionLayout.drawingCache, left.toFloat(), (parent.getChildAt(i).top - sectionLayout.measuredHeight).toFloat(), null)
                //获取图像(保存滚出屏幕的最后一个)
                if ((parent.getChildAt(i).top - sectionLayout.measuredHeight) <= 0) {
                    lastBitmap = Bitmap.createBitmap(sectionLayout.drawingCache)
                }
                sectionLayout.destroyDrawingCache()
                sectionLayout.isDrawingCacheEnabled = false
            } else {
                continue
            }
        }
    }

    //如果开启了悬浮，绘制悬浮的那个分割区域
    override fun onDrawOver(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.onDrawOver(c, parent, state)
        if(!isFloat){
            return
        }
        val left = parent!!.paddingLeft
        for (i: Int in 0..parent.childCount) {
            if (groupListener.isFirst(parent.getChildAdapterPosition(parent.getChildAt(i)))) {
                //parent.getChildAt(i).top是Item内容的高度，不包含Decoration的高度；sectionLayout.measuredHeight是Decoration的高度
                if (parent.getChildAt(i).top in sectionLayout.measuredHeight..sectionLayout.measuredHeight * 2) {
                    firstTop = parent.getChildAt(i).top - sectionLayout.measuredHeight * 2
                    c!!.drawBitmap(lastBitmap, left.toFloat(), firstTop.toFloat(), null)
                    //发现是交换的过程（后一个把前一个顶出的过程），绘制完交换后的Decoration后，不再绘制top位置是0的Decoration
                    return
                } else {
                    firstTop = 0
                }
            } else {
                firstTop = 0
            }
        }
        //绘制top位置是0的Decoration
        c!!.drawBitmap(lastBitmap, left.toFloat(), firstTop.toFloat(), null)
    }

    //每个Item给留出分割区域的绘制控件
    override fun getItemOffsets(outRect: Rect?, itemPosition: Int, parent: RecyclerView?) {
        super.getItemOffsets(outRect, itemPosition, parent)
        if (groupListener.isFirst(itemPosition)) {
            outRect!!.top = sectionLayout.measuredHeight
        }
    }

}

abstract class GroupListener {
    abstract fun isFirst(position: Int): Boolean
    abstract fun setContnt(contentView: ViewGroup, position: Int)
}