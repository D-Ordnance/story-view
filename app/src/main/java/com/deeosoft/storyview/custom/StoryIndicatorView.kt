package com.deeosoft.storyview.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar
import com.deeosoft.storyview.R

@SuppressLint("UseCompatLoadingForDrawables")
class StoryIndicatorView(context: Context, attributeSet: AttributeSet): ProgressBar(context, attributeSet, android.R.attr.progressBarStyleHorizontal) {
    init {
        this.progress = 0
        val typedArray = context.theme.obtainStyledAttributes(attributeSet,
            R.styleable.StoryView, 0, 0)
        val storyIndicatorDrawableId = typedArray.getResourceId(
            R.styleable.StoryView_storyIndicatorViewProgressDrawable,
            R.drawable.story_progress_indicator
        )
        val progressDrawable = context.resources.getDrawable(storyIndicatorDrawableId, null)
//        this.clipBounds = this.progressDrawable.bounds
        this.progressDrawable = progressDrawable
    }
}