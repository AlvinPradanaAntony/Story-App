package com.devcode.storyapp.ui.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.devcode.storyapp.R

class CustomEditText: AppCompatEditText{
    private lateinit var editTextBackground: Drawable
    private lateinit var editTextErrorBackground: Drawable
    private var isError = false

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isError) editTextErrorBackground else editTextBackground
        addTextChangedListener(onTextChanged = { text, _, _, _ ->
            if (!TextUtils.isEmpty(text) && text.toString().length < 8 && compoundDrawables[DRAWABLE_RIGHT] != null) {
                error = resources.getString(R.string.password_minimum_character)
                isError = true
            } else {
                error = null
                isError = false
            }
        })
    }
    private fun init() {
        editTextBackground = ContextCompat.getDrawable(context, R.drawable.style_bg_edittext) as Drawable
        editTextErrorBackground = ContextCompat.getDrawable(context, R.drawable.style_edittext_error) as Drawable
    }
    companion object {
        const val DRAWABLE_RIGHT = 2
    }
}
