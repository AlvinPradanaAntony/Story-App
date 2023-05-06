package com.devcode.storyapp.ui.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.devcode.storyapp.R

class CustomEditText: AppCompatEditText, View.OnTouchListener {
    private lateinit var editTextBackground: Drawable
    private lateinit var editTextErrorBackground: Drawable
    private lateinit var eyeIcon: Drawable
    private lateinit var lock: Drawable
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
        if (isPassword){
            showDrawable()
        }
        background = if (isError) editTextErrorBackground else editTextBackground
    }

    private fun init() {
        val email: CustomEditText? = findViewById(R.id.txt_email)
        email?.isEmail  = true
        val password: CustomEditText? = findViewById(R.id.txt_pass)
        password?.isPassword = true

        editTextBackground = ContextCompat.getDrawable(context, R.drawable.style_bg_edittext) as Drawable
        editTextErrorBackground = ContextCompat.getDrawable(context, R.drawable.style_edittext_error) as Drawable
        eyeIcon = ContextCompat.getDrawable(context, R.drawable.ic_eye_off) as Drawable
        lock = ContextCompat.getDrawable(context, R.drawable.ic_lock) as Drawable

        if (isPassword){
            setOnTouchListener(this)
        }

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

    private fun showDrawable() {
        val paddingEnd = (8 * resources.displayMetrics.density).toInt()
        val lock = InsetDrawable(lock, 0, 0, paddingEnd, 0)
        setDrawables(startOfTheText = lock, endOfTheText = eyeIcon)
    }

    private fun hideEyeButton() {
        setDrawables()
    }


    private fun setDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }
    companion object {
        const val DRAWABLE_RIGHT = 2
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val eyeButtonStart: Float
            val eyeButtonEnd: Float
            var isEyeButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                eyeButtonEnd = (eyeIcon.intrinsicWidth + paddingStart).toFloat()
                if (event.x < eyeButtonEnd) isEyeButtonClicked = true
            } else {
                eyeButtonStart = (width - paddingEnd - eyeIcon.intrinsicWidth).toFloat()
                if (event.x > eyeButtonStart) isEyeButtonClicked = true
            }

            if (isEyeButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        hideEyeButton()
                        if (transformationMethod.equals(HideReturnsTransformationMethod.getInstance())) {
                            transformationMethod = PasswordTransformationMethod.getInstance() // hide password
                            eyeIcon = ContextCompat.getDrawable(context, R.drawable.ic_eye_off) as Drawable
                            showDrawable()
                        } else {
                            transformationMethod = HideReturnsTransformationMethod.getInstance() // show password
                            eyeIcon = ContextCompat.getDrawable(context, R.drawable.ic_eye) as Drawable
                            showDrawable()
                        }
                        return true
                    }
                    else -> return false
                }
            } else return false
        }
        return false
    }
}
