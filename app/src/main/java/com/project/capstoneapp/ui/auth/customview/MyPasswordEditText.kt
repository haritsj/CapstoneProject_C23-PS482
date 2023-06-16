package com.dicoding.mystoryapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.project.capstoneapp.R
import com.google.android.material.textfield.TextInputEditText

class MyPasswordEditText : TextInputEditText {
    private var defaultBackground: Drawable? = null
    private var focusBackground: Drawable? = null
    private var errorBackground: Drawable? = null
    private var isError: Boolean = false

    companion object {
        private const val MIN_LENGTH = 8
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

        transformationMethod = PasswordTransformationMethod.getInstance()

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isError = if (s.isEmpty()) {
                    setError(context.getString(R.string.invalid_text), null)
                    true
                } else if (s.isNotEmpty() && s.length < MIN_LENGTH) {
                    setError(context.getString(R.string.invalid_password), null)
                    true
                }
                else {
                    error = null
                    false
                }
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing
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