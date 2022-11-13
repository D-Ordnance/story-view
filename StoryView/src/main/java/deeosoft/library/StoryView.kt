package deeosoft.library

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import deeosoft.library.adapter.StoryAdapter
import deeosoft.library.custom.StoryIndicatorView
import deeosoft.library.custom.StoryViewPager
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import kotlin.properties.Delegates

@SuppressLint("UseCompatLoadingForDrawables")
class StoryView (context: Context, attributeSet: AttributeSet):
    FrameLayout(context, attributeSet){
    private var currentPage = 0
    private var storyProgressTimer = 0
    private var delay by Delegates.notNull<Long>()
    private lateinit var job: Job
    private var viewPager: StoryViewPager
    private lateinit var listener: OnStoryActionListener
    private var fragmentSize by Delegates.notNull<Int>()
    private var listOfFragment = mutableListOf<Int>()
    private val storyIndicatorLayout = LinearLayout(context)
    private val outerLayout = RelativeLayout(context)
    private var marginLeft by Delegates.notNull<Int>()
    private var marginRight by Delegates.notNull<Int>()
    private var marginTop by Delegates.notNull<Int>()
    private var storyIndicatorMarginEnd by Delegates.notNull<Int>()
    private var storyIndicatorHeight by Delegates.notNull<Int>()

    init{
        val typedArray = context.theme.obtainStyledAttributes(attributeSet,
            R.styleable.StoryView,0,0)
        val fragmentsResourceId = typedArray.getResourceId(R.styleable.StoryView_storyFragments, 0)

        marginLeft = typedArray.getDimensionPixelSize(R.styleable.StoryView_storyLayoutMarginLeft, 20)
        marginRight = typedArray.getDimensionPixelSize(R.styleable.StoryView_storyLayoutMarginRight, 20)
        marginTop = typedArray.getDimensionPixelSize(R.styleable.StoryView_storyLayoutMarginTop, 20)

        storyIndicatorMarginEnd = typedArray.getDimensionPixelSize(R.styleable.StoryView_storyIndicatorViewMarginEnd, 8)
        storyIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.StoryView_storyIndicatorViewHeight, 8)

        storyIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.StoryView_storyIndicatorViewHeight, 8)

        delay = typedArray.getInt(R.styleable.StoryView_storyIndicatorViewDelay, 30).toLong()

        if(fragmentsResourceId != 0){
            val fragments = resources.obtainTypedArray(fragmentsResourceId)
            fragmentSize = fragments.length()
            for(i in 0 until fragmentSize){
                val fragmentResourceId = fragments.getResourceId(i,0)
                listOfFragment.add(fragmentResourceId)
                val storyProgressIndicator =
                    StoryIndicatorView(context, attributeSet)
                val storyIndicatorLayoutParams = LinearLayout.LayoutParams(0, storyIndicatorHeight)
                storyIndicatorLayoutParams.weight = 1/fragmentSize.toFloat()
                if(i != fragmentSize - 1)storyIndicatorLayoutParams.marginEnd = storyIndicatorMarginEnd
                storyProgressIndicator.layoutParams = storyIndicatorLayoutParams
                storyIndicatorLayout.addView(storyProgressIndicator)
            }

            val outerLayoutLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            outerLayout.layoutParams = outerLayoutLayoutParams

            storyIndicatorLayout.orientation = LinearLayout.HORIZONTAL

            viewPager = StoryViewPager(context)
            val viewPagerLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT)
            viewPager.layoutParams = viewPagerLayoutParams

            visibility = View.VISIBLE

            viewPager.id = View.generateViewId()
            viewPager.background = context.resources.getDrawable(R.color.teal_200, null)
            val viewPagerParams = RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            viewPagerParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            viewPagerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            outerLayout.addView(viewPager, viewPagerParams)
            addView(outerLayout)

            val storyIndicatorLayoutParams = RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            storyIndicatorLayoutParams.setMargins(marginLeft,marginTop,marginRight,0)
            storyIndicatorLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            outerLayout.addView(storyIndicatorLayout, storyIndicatorLayoutParams)

            viewPager.adapter = StoryAdapter((context as AppCompatActivity).supportFragmentManager, listOfFragment)

            fragments.recycle()
        }else{
            throw IllegalArgumentException("No fragment layout provided - check documentation")
        }

        typedArray.recycle()

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                currentPage = position
                determineStoryProgressOnScrollStateChanged(currentPage)
                (context).lifecycleScope.launch {
                    job.cancelAndJoin()
                    storyProgressTimer = 0
                    getEmittedValues(true)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
    }

    private fun determineStoryProgressOnScrollStateChanged(position: Int){
        for(i in 0 until storyIndicatorLayout.childCount){
            if(position > i){
                (storyIndicatorLayout.getChildAt(i) as ProgressBar).progress = 100
            }else{
                (storyIndicatorLayout.getChildAt(i) as ProgressBar).progress = 0
            }
        }
    }
    private fun storyWithKotlinFlow(continueStoryProgress: Boolean): Flow<Int> = flow {
        while(storyProgressTimer < 100 && continueStoryProgress){
            delay(delay)
            emit(storyProgressTimer++)
        }
    }
    private fun getEmittedValues(continueStoryProgress: Boolean){
        job = (context as AppCompatActivity).lifecycleScope.launchWhenStarted {
            storyWithKotlinFlow(continueStoryProgress).collect{
                (storyIndicatorLayout.getChildAt(currentPage) as ProgressBar).progress = it
                if(storyProgressTimer == 100){
                    moveToNextFragment(currentPage)
                }
            }
        }
    }
    private fun moveToNextFragment(fragmentType: Int){
        (context as AppCompatActivity).runOnUiThread {
            // I would use the strategy - factory decide pattern here to determine which
            // function to invoke base on the position.
            determineStoryNextActionFromState(fragmentType)?.invoke()
        }
    }
    private fun determineStoryStateFromPosition(position: Int): StoryState {
        return when {
            position >= fragmentSize - 1 -> StoryState.DONE
            else -> StoryState.PROCESSING
        }
    }
    private fun determineStoryNextActionFromState(position: Int): (() -> Unit)? {
        val nextActionUseCase = HashMap<StoryState, (() -> Unit)>()
        nextActionUseCase[StoryState.PROCESSING] = {processNextStoryMovementAction(position)}
        nextActionUseCase[StoryState.DONE] = {doneWithStory()}

        return nextActionUseCase[determineStoryStateFromPosition(position)]
    }
    private fun processNextStoryMovementAction(position: Int){
        viewPager.setCurrentItem(position + 1, true)
    }
    private fun doneWithStory(){
        listener.onStoryCompleted()
    }

    fun setStoryActionListener(listener: OnStoryActionListener){
        this.listener = listener
    }
    fun previousItemInStory(){
        if (currentPage != 0)moveToNextFragment(currentPage - 2)
    }
    fun nextItemInStory(){
        moveToNextFragment(currentPage)
    }
    fun pauseStory(){
        (context as AppCompatActivity).lifecycleScope.launch {
            job.cancelAndJoin()
            getEmittedValues(false)
        }
    }
    fun continueStory(){
        (context as AppCompatActivity).lifecycleScope.launch {
            job.cancelAndJoin()
            getEmittedValues(true)
        }
    }
    /*fun setUp(fragment: List<Int>){
        fragmentSize = fragment.size
        for(i in 0 until fragmentSize) {
            val storyProgressIndicator =
                StoryIndicatorView(context)
            val storyIndicatorLayoutParams = LinearLayout.LayoutParams(0, storyIndicatorHeight)
            storyIndicatorLayoutParams.weight = 1/fragmentSize.toFloat()
            if(i != fragmentSize - 1)storyIndicatorLayoutParams.marginEnd = storyIndicatorMarginEnd
            storyProgressIndicator.layoutParams = storyIndicatorLayoutParams
            storyIndicatorLayout.addView(storyProgressIndicator)
        }

        viewPager.id = View.generateViewId()
        viewPager.background = context.resources.getDrawable(R.color.teal_200, null)
        val viewPagerParams = RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        viewPagerParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        viewPagerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        outerLayout.addView(viewPager, viewPagerParams)
        addView(outerLayout)

        val storyIndicatorLayoutParams = RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        storyIndicatorLayoutParams.setMargins(marginLeft,marginTop,marginRight,0)
        storyIndicatorLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        outerLayout.addView(storyIndicatorLayout, storyIndicatorLayoutParams)

        viewPager.adapter = StoryAdapter((context as AppCompatActivity).supportFragmentManager, fragment)
    }*/
    fun startStory(){
        try {
            if (fragmentSize != 0) {
                (context as AppCompatActivity).lifecycleScope.launch {
                    getEmittedValues(true)
                }
            }
        }catch (ex: Exception){
            throw IllegalArgumentException("Fragments layout not setUp: Call the setUp function first")
        }
    }
}

interface OnStoryActionListener{
    fun onStoryCompleted(){}
}

enum class StoryState{
    PROCESSING, DONE
}