package com.sjd.library

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.IntDef
import androidx.customview.widget.ViewDragHelper
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Date: 20/09/22
 * Author: sjd753@gmail.com
 */

class SlidingPanelLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(
    context, attrs, defStyleAttr
) {
    @IntDef(value = [STATE_OPEN, STATE_PEAK, STATE_CLOSE], flag = true)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    internal annotation class PanelState

    @IntDef(value = [DIRECTION_RIGHT_PANEL, DIRECTION_LEFT_PANEL], flag = true)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    internal annotation class Direction

    /**
     * A view drag helper to manipulate and calculate drag states
     */
    private lateinit var mViewDragHelper: ViewDragHelper

    /**
     * A content view that belongs to the SlidingPanelLayout as a single child
     */
    private lateinit var mContentView: View
    private var mContentWidth = 0

    /**
     * A view peak slide percent resembles how much the view should slide to reach the peak set
     * default peak is 50 percent of the content view
     */
    private var mPeakSlidePercent = 0
    private var mPeakWidth = 0

    /**
     * A view current left position
     */
    private var mMoveLeft = 0

    /**
     * A view default panel direction
     */
    private var mDirection = DIRECTION_RIGHT_PANEL

    /**
     * A view default panel state
     */
    private var mPanelState = STATE_OPEN

    /**
     * A panel view optional callback
     */
    private var mCallBack: CallBack? = null
    private fun init() {
        val sensitivity = 1.0f
        mViewDragHelper =
            ViewDragHelper.create(this, sensitivity, object : ViewDragHelper.Callback() {
                override fun onViewDragStateChanged(state: Int) {
                    super.onViewDragStateChanged(state)
                    mCallBack?.onViewDragStateChanged(state)
                }

                override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                    return child === mContentView
                }

                override fun onViewPositionChanged(
                    changedView: View,
                    left: Int,
                    top: Int,
                    dx: Int,
                    dy: Int
                ) {
                    mMoveLeft = left
                    if (mPanelState == STATE_CLOSE && abs(left) == mContentWidth) {
                        mCallBack?.onViewPanelStateChanged(STATE_CLOSE)
                    } else if (mPanelState == STATE_PEAK && abs(left) == mPeakWidth) {
                        mCallBack?.onViewPanelStateChanged(STATE_PEAK)
                    } else if (mPanelState == STATE_OPEN && abs(left) == 0) {
                        mCallBack?.onViewPanelStateChanged(STATE_OPEN)
                    }
                }

                override fun onViewReleased(
                    releasedChild: View,
                    xVelocity: Float,
                    yVelocity: Float
                ) {
                    if (abs(mMoveLeft) >= abs(mContentWidth / 2)) {
                        if (mPeakWidth > 0) {
                            mPanelState = STATE_PEAK
                            val settleAt = getFinalLeft(mPeakWidth)
                            mViewDragHelper.settleCapturedViewAt(settleAt, releasedChild.top)
                        } else {
                            mPanelState = STATE_CLOSE
                            val settleAt = getFinalLeft(mContentWidth)
                            mViewDragHelper.settleCapturedViewAt(settleAt, releasedChild.top)
                        }
                    } else {
                        mPanelState = STATE_OPEN
                        mViewDragHelper.settleCapturedViewAt(0, releasedChild.top)
                    }
                    invalidate()
                }

                override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                    return when (mDirection) {
                        DIRECTION_RIGHT_PANEL -> min(
                            mContentWidth,
                            max(left, 0)
                        )
                        DIRECTION_LEFT_PANEL -> max(
                            min(0, left), -width
                        )
                        else -> 0
                    }
                }

                override fun getViewHorizontalDragRange(child: View): Int {
                    return mContentWidth
                }
            })
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        check(childCount == 1) { "SlidingPanelLayout must host one child." }
        mContentView = getChildAt(0)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mContentWidth = mContentView.width
        if (mPeakSlidePercent == 0) mPeakSlidePercent = 50
        mPeakWidth = mContentWidth * mPeakSlidePercent / 100
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (ev.x >= mContentView.left) mViewDragHelper.shouldInterceptTouchEvent(ev)
        else super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.x >= mContentView.left) {
            mViewDragHelper.processTouchEvent(event)
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mViewDragHelper.continueSettling(true)) {
            invalidate()
        }
    }

    /**
     * Set the sliding panel direction either DIRECTION_RIGHT_PANEL or DIRECTION_LEFT_PANE
     * Default direction is DIRECTION_RIGHT_PANEL
     *
     * @param panelSlideDirect Direction for the sliding panel layout
     */
    fun setPanelSlideDirect(@Direction panelSlideDirect: Int) {
        mDirection = panelSlideDirect
    }

    /**
     * Set the sliding panel peak percentage
     * Default peak is 50 percent of the content view
     *
     * @param percent Percentage for the sliding panel layout to peak at
     */
    fun setPeakAt(percent: Int) {
        check(!(percent > 100 || percent < 0)) { "Peak must not be greater than content width nor a negative value" }
        mPeakSlidePercent = 100 - percent
    }

    private fun getFinalLeft(settleAt: Int): Int {
        val checkLeft = mDirection and DIRECTION_RIGHT_PANEL == DIRECTION_RIGHT_PANEL
        val checkRight = mDirection and DIRECTION_LEFT_PANEL == DIRECTION_LEFT_PANEL
        if (checkLeft) {
            return settleAt
        }
        return if (checkRight) {
            -settleAt
        } else 0
    }

    /**
     * Set the sliding panel state
     * Default state is STATE_OPEN
     *
     * @see panelState State for the sliding panel layout to settle at
     */
    var panelState: Int
        get() = mPanelState
        set(@PanelState panelState) {
            when (panelState) {
                STATE_PEAK -> {
                    mPanelState = STATE_PEAK
                    val settleAt = getFinalLeft(mPeakWidth)
                    mViewDragHelper.smoothSlideViewTo(mContentView, settleAt, mContentView.top)
                }
                STATE_CLOSE -> {
                    mPanelState = STATE_CLOSE
                    val settleAt = getFinalLeft(mContentWidth)
                    mViewDragHelper.smoothSlideViewTo(mContentView, settleAt, mContentView.top)
                }
                else -> {
                    mPanelState = STATE_OPEN
                    mViewDragHelper.smoothSlideViewTo(mContentView, 0, mContentView.top)
                }
            }
            invalidate()
        }

    fun setCallBack(callBack: CallBack?) {
        mCallBack = callBack
    }

    interface CallBack {
        fun onViewPanelStateChanged(state: Int)
        fun onViewDragStateChanged(state: Int)
    }

    companion object {
        /**
         * A view is currently fully expanded state.
         * default panel state is STATE_OPEN
         */
        const val STATE_OPEN = 1

        /**
         * A view is currently at partially opened state. The position is currently at peak as a result
         * of user input or simulated user input.
         */
        const val STATE_PEAK = 1 shl 1

        /**
         * A view is currently fully collapsed as a result of a fling or
         * predefined non-interactive motion.
         */
        const val STATE_CLOSE = 0

        /**
         * A view slide direction is determined by the following flags
         * default direction is DIRECTION_RIGHT_PANEL
         */
        const val DIRECTION_RIGHT_PANEL = 1
        const val DIRECTION_LEFT_PANEL = 1 shl 1
    }

    init {
        init()
    }
}