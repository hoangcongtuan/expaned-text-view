package com.example.expandabletextview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Build.VERSION_CODES.ECLAIR_MR1
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
import android.widget.TextView
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
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD*/
        }
        orientation = LinearLayout.VERTICAL
        updateUI()

        binding.btnCollapse.setOnClickListener {
            collapse()
        }

        setWillNotDraw(false)
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
            var addReadMore: String = tempText + READ_MORE_STRING
            val start = System.currentTimeMillis()
            var left = 0
            var right = addReadMore.length
            var mid = (left + right) / 2
            var status = 0
            var cutPoint = 0
            while (true) {
                addReadMore = tempText + READ_MORE_STRING
                staticLayout = /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    StaticLayout.Builder.obtain(
                        addReadMore,
                        0,
                        addReadMore.length,
                        textPaint,
                        measuredWidth
                    )
                        .setLineSpacing(
                            binding.textView.lineSpacingMultiplier,
                            binding.textView.lineSpacingExtra
                        )
                        .setIncludePad(binding.textView.includeFontPadding)
                        .setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD)
                        .build()
                } else*/ StaticLayout(
                    addReadMore, 0, addReadMore.length, textPaint,
                    measuredWidth,
                    Layout.Alignment.ALIGN_NORMAL,
                    binding.textView.lineSpacingMultiplier, binding.textView.lineSpacingExtra,
                    binding.textView.includeFontPadding
                ).apply {
                }

                val bitmap = Bitmap.createBitmap(staticLayout?.width!!, staticLayout?.height!!, Bitmap.Config.RGB_565)
                val canvas = Canvas(bitmap)
                canvas.drawColor(Color.WHITE)
                staticLayout?.draw(canvas)

                if (staticLayout!!.lineCount > MAX_LINE_COUNT && status == 0) {
                    val temp = right
                    right = mid
                    mid = (left + temp) / 2
                    tempText = text.substring(0, mid)
                } else if (staticLayout!!.lineCount < MAX_LINE_COUNT && status == 0) {
                    left = mid
                    mid = (mid + right) / 2
                    tempText = text.substring(0, mid)
                } else {
                    if (status != 1) {
                        status = 1
                        cutPoint = mid
                        continue
                    }
                    if (staticLayout!!.lineCount > MAX_LINE_COUNT) {
                        --cutPoint
                        tempText = text.substring(0, cutPoint)
                        addReadMore = tempText + READ_MORE_STRING
                        break
                    } else {
                        cutPoint += 1
                        tempText = text.substring(0, cutPoint)
                    }
                }

                /* if (staticLayout!!.lineCount <= MAX_LINE_COUNT)
                     break*/
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
            invalidate()
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


     override fun onDraw(canvas: Canvas?) {
         super.onDraw(canvas)
         staticLayout?.draw(canvas)
     }
}