package com.deeosoft.storyview

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.view.WindowCompat
import com.deeosoft.storyview.custom.OnStoryActionListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnStoryActionListener, View.OnTouchListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        storyView.setStoryActionListener(this)
        storyView.setOnTouchListener(this)
    }

    override fun onStoryCompleted() {
        super.onStoryCompleted()
        println("done with story view ...")
    }

    override fun onStart() {
        super.onStart()
        storyView.startStory()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when(v?.id){
            R.id.storyView -> {
                when(event?.action){
                    MotionEvent.ACTION_UP -> {
                        storyView.continueStory()
                    }
                    MotionEvent.ACTION_DOWN -> {
                        storyView.pauseStory()
                    }
                }
            }
        }
        return true
    }
}