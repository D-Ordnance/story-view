package com.deeosoft.storyview.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager
import java.lang.reflect.Field

class StoryViewPager: ViewPager {
    constructor( context: Context): super(context){
        setTangScroller()
    }

    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet){
        setTangScroller()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    private fun setTangScroller(){
        try {
            val viewpager: Class<*> = ViewPager::class.java
            val scroller: Field = viewpager.getDeclaredField("mScroller")
            scroller.isAccessible = true
            scroller.set(this, StoryScroller(context))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

class StoryScroller(context: Context): Scroller(context) {
    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(startX, startY, dx, dy, 350)
    }
}