package top.greendami.mykotlinapp

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Greendami on 2017/6/2.
 */
class MainActivity  : Activity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            button.setBackgroundColor(PPColorPicker21.chooseColor)
            button.text = "RGB:${PPColorPicker21.rgb}"
        }
    }
}