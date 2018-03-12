package com.techapp.james.textviewdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.View


class MainActivity : AppCompatActivity() {
    var data: String = ""
    var maxLine: Int = 5
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        for (i in 0..100) {
            data += " Long Text"
        }
        textView.setBackgroundResource(R.color.colorYellow)
        println("onStart TextView's width " + textView.width)
        limitTextView(data, textView, null,maxLine)
    }

    fun getLastCharIndexForLimitTextView(textView: TextView, content: String, width: Int, maxLine: Int): Int {
        println("String's width: " + width)
        var textPaint: TextPaint = textView.getPaint();
        var staticLayout: StaticLayout = StaticLayout(content, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false);
        //StaticLayout("需要分行的字串","畫筆目標","Layout寬度","
        // layout的對齊方式，ALIGN_CENTER， ALIGN_NORMAL， ALIGN_OPPOSITE",
        // "相對行間距，相對字體大小，1.5f表示行間距為1.5倍的字體高度",
        // "在基礎行距上加多少 實際行間距等於這兩者的和","是否包含padding")
        if (staticLayout.lineCount > maxLine) {
            return staticLayout.getLineStart(maxLine)
            //Return the text offset(游標) of the beginning of the specified line
        }
        return -1
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        println("onWindowFocusChanged TextView's width " + textView.width) //here!!!  It can get TextView's Width
        limitTextView(data, textView, null,maxLine)

    }

    fun limitTextView(originData: String, textView: TextView?, clickListener: View.OnClickListener?, maxLine:Int) {
        if (textView == null) {
            return
        }
        val showmore = "...show more"
        var width = textView.width//取得textView的寬度
        textView.measuredWidth
        println("TextView's width " + width)
        var lastCharIndex = getLastCharIndexForLimitTextView(textView, originData, width, maxLine)
        // println("LastCharIndex " + lastCharIndex + " sumIdx " + originData[lastCharIndex])
        if (lastCharIndex < 0) {//如果行數沒超過限制
            textView.text = originData
            return
        }
        //如果超出了行數限制
        textView.movementMethod = LinkMovementMethod.getInstance()//this will deprive the recyclerView's focus

        var explicitText: String? = ""
        if (originData[lastCharIndex] == '\n') {//manual enter
            explicitText = originData.substring(0, lastCharIndex)
        } else if (lastCharIndex > 12) {//TextView auto enter
            explicitText = originData.substring(0, lastCharIndex - showmore.length)
        }
        val sourceLength = explicitText!!.length

        explicitText = "$explicitText$showmore"
        val mSpan = SpannableString(explicitText)
        mSpan.setSpan(object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = textView.textColors.defaultColor //default text color
                //it will make show more tag Red
                ds.isAntiAlias = true  //抗鋸齒
                ds.isUnderlineText = false //show more under line
            }

            override fun onClick(widget: View) {//"...show more" click event
                println("click showmore")
                textView.text = originData
                textView.setOnClickListener(null)
                Handler().postDelayed(Runnable {  //UI thread
                    if (clickListener != null)
                        textView.setOnClickListener(clickListener)//prevent the double click
                }, 20)
            }
        }, sourceLength, explicitText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = mSpan
//        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE --- 不包含start和end所在的端點        (a,b)
//        Spanned.SPAN_EXCLUSIVE_INCLUSIVE --- 不包含端start，但包含end所在的端点     (a,b]
//        Spanned.SPAN_INCLUSIVE_EXCLUSIVE --- 包含start，但不包含end所在的端点   [a,b)
//        Spanned.SPAN_INCLUSIVE_INCLUSIVE --- 包含start和end所在的端点          [a,b]

    }
}
