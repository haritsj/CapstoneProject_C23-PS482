package com.project.capstoneapp.ui.auth.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.project.capstoneapp.R

class MyEmailEditText : TextInputEditText {
    private var defaultBackground: Drawable? = null
    private var focusBackground: Drawable? = null
    private var errorBackground: Drawable? = null
    private var isError: Boolean = false

    companion object {
        private val format = Patterns.EMAIL_ADDRESS
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        defaultBackground = ContextCompat.getDrawable(context, R.drawable.bg_et_default)
        focusBackground = ContextCompat.getDrawable(context, R.drawable.bg_et_focus)
        errorBackground = ContextCompat.getDrawable(context, R.drawable.bg_et_error)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isError = if (s.isEmpty()) {
                    error = context.getString(R.string.invalid_text)
                    true
                } else if (s.isNotEmpty() && !format.matcher(s).matches()) {
                    error = context.getString(R.string.invalid_email)
                    true
                } else {
                    error = null
                    false
                }
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isError) {
            errorBackground
        } else if (isFocused){
            focusBackground
        } else {
            defaultBackground
        }
    }
}