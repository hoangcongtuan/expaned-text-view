package com.example.expandabletextview

import android.content.Context
import android.graphics.Color
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.StaticLayout
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import com.example.expandabletextview.databinding.ViewExpandableTextBinding


class ExpandableTextView : LinearLayout {
    private lateinit var binding: ViewExpandableTextBinding

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        binding = ViewExpandableTextBinding.inflate(LayoutInflater.from(context), this)
        binding.textView.apply {
            movementMethod = LinkMovementMethod.getInstance();
            highlightColor = Color.TRANSPARENT;
        }
        orientation = LinearLayout.VERTICAL
        updateUI()

        binding.btnCollapse.setOnClickListener {
            collapse()
        }
    }

    private val MAX_LINE_COUNT = 5
    private val READ_MORE_STRING = "____read more"

    private val textPaint by lazy {
        TextPaint().apply {
            color = binding.textView.currentTextColor
            textSize = binding.textView.textSize
        }
    }
    private var staticLayout: StaticLayout? = null
    private var spannableString: SpannableString? = null
    var onClickReadMore: (() -> Unit)? = null
    private var originalText: String = ""
    private var isExpand: Boolean = false

    fun setText2(text: String) {
        originalText = text
        doOnPreDraw {
            var tempText = text
            var addReadMore: String
            val start = System.currentTimeMillis()
            while (true) {
                addReadMore = tempText + READ_MORE_STRING
                staticLayout = StaticLayout(
                    addReadMore, 0, addReadMore.length, textPaint,
                    measuredWidth,
                    Layout.Alignment.ALIGN_NORMAL,
                    1f, 0f, false
                )

                if (staticLayout!!.lineCount > MAX_LINE_COUNT)
                    tempText = tempText.substring(0, tempText.length - 1)

                if (staticLayout!!.lineCount <= MAX_LINE_COUNT)
                    break
            }
            Log.d("ExpandableTextView", "time=${System.currentTimeMillis() - start}")
            spannableString = SpannableString(addReadMore)
            spannableString?.setSpan(
                ForegroundColorSpan(Color.BLUE),
                addReadMore.length - READ_MORE_STRING.length,
                addReadMore.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    setExpand()
                    onClickReadMore?.invoke()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            }
            spannableString?.setSpan(
                clickableSpan, addReadMore.length - READ_MORE_STRING.length,
                addReadMore.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.textView.setText(spannableString)
            /*invalidate()*/
        }
    }

    private fun setExpand() {
        isExpand = true
        binding.textView.text = originalText
        updateUI()
    }

    private fun collapse() {
        setText2(originalText)
        isExpand = false
        updateUI()
    }

    private fun updateUI() {
        binding.btnCollapse.isVisible = isExpand
    }


    /* override fun onDraw(canvas: Canvas?) {
         super.onDraw(canvas)
         staticLayout?.draw(canvas)
     }*/
}