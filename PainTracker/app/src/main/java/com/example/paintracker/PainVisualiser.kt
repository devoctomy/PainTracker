package com.example.paintracker

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.paintracker.R

class PainVisualiser @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var imageView: ImageView
    private var frontButton: Button
    private var backButton: Button
    private var isFrontImage = true

    // Images for front and back
    private val frontImageRes = R.drawable.body_front // Replace with your front image
    private val backImageRes = R.drawable.body_back   // Replace with your back image

    init {
        // Inflate the layout
        LayoutInflater.from(context).inflate(R.layout.pain_visualiser, this, true)

        // Find views
        imageView = findViewById(R.id.imageView)
        frontButton = findViewById(R.id.buttonFront)
        backButton = findViewById(R.id.buttonBack)

        // Set initial image
        imageView.setImageResource(frontImageRes)

        // Button click listeners
        frontButton.setOnClickListener {
            isFrontImage = true
            imageView.setImageResource(frontImageRes)
        }

        backButton.setOnClickListener {
            isFrontImage = false
            imageView.setImageResource(backImageRes)
        }
    }
}
