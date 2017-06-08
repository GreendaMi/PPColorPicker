package top.greendami.mykotlinapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Created by zhaopy on 2017/6/7.
 */
class PPColorPicker(context: Context?, attrs: AttributeSet? = null) : View(context, attrs) {
    internal var mLayoutSize: Int = 0
    internal var pickedColor = 0xffffffff.toInt()
    internal var x: Float = 0.toFloat()//内部圆环触摸坐标
    internal var y: Float = 0.toFloat()
    internal var x1: Float = 0.toFloat()//外部圆环触摸坐标
    internal var y1: Float = 0.toFloat()
    var mColor: Int = 0
    var chooseColor: Int = 0
    var r1 = 2.8f
    var r2 = 3f//中间色盘
    var r3 = 10f
    var r4 = 18f//触摸圆
    var r5 = 12f//中间选中色圆
    var alpha: Int = 0
        get() = (chooseColor and 0xff000000.toInt()).ushr(24)
    var red: Int = 0
        get() = (chooseColor and 0x00ff0000).ushr(16)
    var green: Int = 0
        get() = (chooseColor and 0x0000ff00).ushr(8)
    var blue: Int = 0
        get() = (chooseColor and 0x000000ff)
    var rgb: String? = null
        get() = "0x" + if (Integer.toHexString(red).compareTo("16") < 0) {
            "0" + Integer.toHexString(red)
        } else {
            Integer.toHexString(red)
        } + if (Integer.toHexString(green).compareTo("16") < 0) {
            "0" + Integer.toHexString(green)
        } else {
            Integer.toHexString(green)
        } + if (Integer.toHexString(blue).compareTo("16") < 0) {
            "0" + Integer.toHexString(blue)
        } else {
            Integer.toHexString(blue)
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec)
        mLayoutSize = Math.min(widthSpecSize, heightSpecSize)
        if (mLayoutSize == 0) {
            mLayoutSize = Math.max(widthSpecSize, heightSpecSize)
        }
        setMeasuredDimension(mLayoutSize, mLayoutSize)
    }

    fun Paint.myreset() {
        this.reset()
        this.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        val mPaint = Paint()
        mPaint.myreset()
        super.onDraw(canvas)
        var bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val bufferCanvas = Canvas()
        bufferCanvas.setBitmap(bitmap)

        //最外层渐变圆环
        var mSweepGradient = SweepGradient((width / 2).toFloat(), (width / 2).toFloat(), intArrayOf(0xffffffff.toInt(), mColor, 0xff000000.toInt(), mColor, 0xffffffff.toInt()), null)
        mPaint.myreset()
        mPaint.shader = mSweepGradient
        bufferCanvas.drawCircle((width / 2).toFloat(), (width / 2).toFloat(), (width / 2).toFloat(), mPaint)

        //第一个空白环
        mPaint.shader = null
        mPaint.color = 0xffffffff.toInt()
        bufferCanvas.drawCircle((width / 2).toFloat(), (width / 2).toFloat(), (width / r1), mPaint)

        //内层渐变环
        mSweepGradient = SweepGradient((width / 2).toFloat(), (width / 2).toFloat(), intArrayOf(0xffff0000.toInt(), 0xffffff00.toInt(), 0xff00ff00.toInt(), 0xff00ffff.toInt(), 0xff0000ff.toInt(), 0xffff00ff.toInt(), 0xffff0000.toInt()), floatArrayOf(0f, 1 / 6f, 1 / 3f, 1 / 2f, 2 / 3f, 5 / 6f, 1f))
        mPaint.shader = mSweepGradient
        mPaint.color = 0xffffffff.toInt()
        bufferCanvas.drawCircle((width / 2).toFloat(), (width / 2).toFloat(), (width / r2).toFloat(), mPaint)

        //第二个空白环
        mPaint.color = pickedColor
        mPaint.shader = null
        bufferCanvas.drawCircle((width / 2).toFloat(), (width / 2).toFloat(), (width / r3).toFloat(), mPaint)


        canvas.drawBitmap(bitmap, 0f, 0f, null)

        mPaint.myreset()
        mColor = bitmap.getPixel(if (x < bitmap.width) x.toInt() else bitmap.width - 1, if (y < bitmap.height) y.toInt() else bitmap.height - 1)

        //触摸点颜色
        mPaint.color = 0xaaffffff.toInt()
        if (x > 0 && y > 0) {
            canvas.drawCircle(x, y, width / r4, mPaint)
        }
        if (x1 > 0 && y1 > 0) {
            canvas.drawCircle(x1, y1, width / r4, mPaint)
        }

        chooseColor = bitmap.getPixel(if (x1 < bitmap.width) x1.toInt() else bitmap.width - 1, if (y1 < bitmap.height) y1.toInt() else bitmap.height - 1)
        mPaint.color = chooseColor
        canvas.drawCircle((width / 2).toFloat(), (width / 2).toFloat(), (width / r5).toFloat(), mPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //在内部圆环内坐标
        if ((event.y - width / 2) * (event.y - width / 2) + (event.x - width / 2) * (event.x - width / 2) < (width / r2) * (width / r2) &&
                (event.y - width / 2) * (event.y - width / 2) + (event.x - width / 2) * (event.x - width / 2) > (width / r3) * (width / r3)) {
            y = event.y
            x = event.x
        } else if ((event.y - width / 2) * (event.y - width / 2) + (event.x - width / 2) * (event.x - width / 2) < (width / 2) * (width / 2) &&
                (event.y - width / 2) * (event.y - width / 2) + (event.x - width / 2) * (event.x - width / 2) > (width / r1) * (width / r1)) {
            //在外部圆环内坐标
            y1 = event.y
            x1 = event.x
        }
        postInvalidate()
        return true
    }


}