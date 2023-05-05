package com.devcode.storyapp.ui.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.devcode.storyapp.R

class CustomEditText: AppCompatEditText{
    private lateinit var editTextBackground: Drawable
    private lateinit var editTextErrorBackground: Drawable
    private var isError = false
    private var isEmail: Boolean = false
    private var isPassword: Boolean = false

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
    }
    private fun init() {
        val email: CustomEditText? = findViewById(R.id.txt_email)
        email?.isEmail  = true
        val password: CustomEditText? = findViewById(R.id.txt_pass)
        password?.isPassword = true
        editTextBackground = ContextCompat.getDrawable(context, R.drawable.style_bg_edittext) as Drawable
        editTextErrorBackground = ContextCompat.getDrawable(context, R.drawable.style_edittext_error) as Drawable
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (isEmail) {
                    if (s.toString().isNotEmpty() && !isValidEmail(s.toString())) {
                        error = resources.getString(R.string.email_invalid)
                        isError = true
                    } else {
                        error = null
                        isError = false
                    }
                } else if (isPassword) {
                    if (s.toString().isNotEmpty() && s.toString().length < 8 && compoundDrawables[DRAWABLE_RIGHT] != null) {
                        error = resources.getString(R.string.password_minimum_character)
                        isError = true
                    } else {
                        error = null
                        isError = false
                    }
                }
            }
            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    companion object {
        const val DRAWABLE_RIGHT = 2
    }
}
