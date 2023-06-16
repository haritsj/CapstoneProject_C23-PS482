package com.dicoding.mystoryapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.project.capstoneapp.R

class MyAuthButton: MaterialButton {
    private var enabledColor: Int = 0
    private var disabledColor: Int = 0
    private var txtColor: Int = 0

    constructor(context: Context) : super(context) { init() }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init() }
    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) { init() }

    private fun init() {
        isEnabled = false
        txtColor = ContextCompat.getColor(context, android.R.color.white)
        enabledColor = ContextCompat.getColor(context, R.color.blue_500)
        disabledColor = ContextCompat.getColor(context, R.color.blue_50)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setBackgroundColor(if (isEnabled) enabledColor else disabledColor)
        setTextColor(txtColor)
        gravity = Gravity.CENTER
        isAllCaps = false
    }
}