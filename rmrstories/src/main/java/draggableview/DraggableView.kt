package draggableview

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.animation.PathInterpolatorCompat
import com.redmadrobot.stories.R
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min

class DraggableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // configurable attributes
    private var dragDismissDistance =
        context.resources.getDimensionPixelSize(R.dimen.default_drag_dismiss_distance).toFloat()
    private var dragDismissFraction = -1f
    private var dragDismissScale = 0.95f
    private var shouldScale = true
    private var dragElasticity = 0.8f

    // state
    private var totalDrag: Float = 0f
    private var draggingDown = false
    private var draggingUp = false
    private var mLastActionEvent: Int = 0

    private var onDragDismissed: ((dragDirection: DragDirection) -> Unit) = { }
    private var systemChromeFader: SystemChromeFader? = null

    private var isDragEnabled = true
    private var dragUpEnabled = false

    init {
        (context as? Activity)?.also {
            systemChromeFader = SystemChromeFader(it)
        }

        getContext().withStyledAttributes(set = attrs, attrs = R.styleable.DraggableView) {
            val distanceAvailable = hasValue(R.styleable.DraggableView_dragDismissDistance)
            val dismissFractionAvailable = hasValue(R.styleable.DraggableView_dragDismissFraction)

            if (distanceAvailable && dismissFractionAvailable) {
                throw IllegalStateException("Do not specify both dragDismissDistance and dragDismissFraction. Choose one.")
            } else if (distanceAvailable) {
                dragDismissDistance = getDimensionPixelSize(
                    R.styleable.DraggableView_dragDismissDistance,
                    0
                ).toFloat()
            } else if (dismissFractionAvailable) {
                dragDismissFraction =
                    getFloat(R.styleable.DraggableView_dragDismissFraction, dragDismissFraction)
            }

            dragDismissScale =
                getFloat(R.styleable.DraggableView_dragDismissScale, dragDismissScale)
            dragUpEnabled = getBoolean(R.styleable.DraggableView_dragUpEnabled, dragUpEnabled)
            dragElasticity = getFloat(R.styleable.DraggableView_dragElasticity, dragElasticity)
        }

        shouldScale = dragDismissScale != 1f
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean =
        nestedScrollAxes and View.SCROLL_AXIS_VERTICAL != 0

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        if (isDragEnabled.not()) {
            return super.onNestedPreScroll(target, dx, dy, consumed)
        }
        // if we're in a drag gesture and the user reverses up the we should take those events
        val draggingDownInProgress = draggingDown && dy > 0
        val draggingUpInProgress = draggingUp && dy < 0
        if (draggingDownInProgress || draggingUpInProgress) {
            dragScale(dy)
            consumed[1] = dy
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        if (isDragEnabled.not()) {
            return super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
        }
        dragScale(dyUnconsumed)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        mLastActionEvent = ev.action
        return super.onInterceptTouchEvent(ev)
    }

    override fun onStopNestedScroll(child: View) {
        if (isDragEnabled.not()) {
            return super.onStopNestedScroll(child)
        }

        val totalDragNormalized = if (dragUpEnabled) abs(totalDrag) else -totalDrag
        val dragDirection = if (totalDrag > 0) DragDirection.UP else DragDirection.DOWN

        if (totalDragNormalized >= dragDismissDistance) {
            dispatchDismissCallback(dragDirection)
        } else { // settle back to natural position
            if (mLastActionEvent == MotionEvent.ACTION_DOWN) {
                // this is a 'defensive cleanup for new gestures',
                // don't animate here
                // see also https://github.com/nickbutcher/plaid/issues/185
                translationY = 0f
                scaleX = 1f
                scaleY = 1f
            } else {
                animate()
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200L)
                    .setInterpolator(PathInterpolatorCompat.create(0.4f, 0f, 0.2f, 1f))
                    .setListener(null)
                    .start()
            }
            totalDrag = 0f
            draggingUp = false
            draggingDown = draggingUp
            dispatchDragCallback(0f, 0f)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (dragDismissFraction > 0f) {
            dragDismissDistance = h * dragDismissFraction
        }
    }

    /**
     * Set if drag/swipe up dismiss is enabled
     */
    fun setDragUpEnabled(dragUpEnabled: Boolean) {
        this.dragUpEnabled = dragUpEnabled
    }

    /**
     * Set lambda reference which is called when dismiss gesture has
     * been performed
     */
    fun setOnDragDismissedListener(onDragDismissedListener: (dragDirection: DragDirection) -> Unit) {
        onDragDismissed = onDragDismissedListener
    }

    /**
     * Set if drag gesture is enabled
     */
    fun setDragEnabled(isDragEnabled: Boolean) {
        this.isDragEnabled = isDragEnabled
    }

    private fun dragScale(scroll: Int) {
        if (scroll == 0) return

        totalDrag = if (scroll > 0) {
            min(totalDrag + scroll.toFloat(), dragDismissDistance * 2)
        } else {
            max(totalDrag + scroll.toFloat(), -dragDismissDistance * 2)
        }

        // track the direction & set the pivot point for scaling
        // don't double track i.e. if start dragging down and then reverse, keep tracking as
        // dragging down until they reach the 'natural' position
        if (scroll < 0 && !draggingUp && !draggingDown) {
            draggingDown = true
            if (shouldScale) pivotY = height.toFloat()
        } else if (scroll > 0 && !draggingDown && !draggingUp) {
            draggingUp = true
            if (shouldScale) {
                pivotY = 0f
            }
        }
        // how far have we dragged relative to the distance to perform a dismiss
        // (0–1 where 1 = dismiss distance). Decreasing logarithmically as we approach the limit
        var dragFraction = log10((1 + abs(totalDrag) / dragDismissDistance).toDouble()).toFloat()

        // calculate the desired translation given the drag fraction
        var dragTo = dragFraction * dragDismissDistance * dragElasticity

        if (draggingUp) {
            // as we use the absolute magnitude when calculating the drag fraction, need to
            // re-apply the drag direction
            dragTo *= -1f
        }
        translationY = dragTo

        if (shouldScale) {
            val scale = 1 - (1 - dragDismissScale) * dragFraction
            scaleX = scale
            scaleY = scale
        }

        // if we've reversed direction and gone past the settle point then clear the flags to
        // allow the list to get the scroll events & reset any transforms
        val downSettlePointReached = draggingDown && totalDrag >= 0
        val upSettlePointReached = draggingUp && totalDrag <= 0
        if (downSettlePointReached || upSettlePointReached) {
            dragFraction = 0f
            dragTo = dragFraction
            totalDrag = dragTo
            draggingUp = false
            draggingDown = draggingUp
            translationY = 0f
            scaleX = 1f
            scaleY = 1f
        }
        dispatchDragCallback(dragTo, min(1f, abs(totalDrag) / dragDismissDistance))
    }

    private fun dispatchDragCallback(elasticOffsetPixels: Float, rawOffset: Float) =
        systemChromeFader?.onDrag(elasticOffsetPixels, rawOffset)

    private fun dispatchDismissCallback(dragDirection: DragDirection) {
        systemChromeFader?.onDismiss()
        onDragDismissed.invoke(dragDirection)
    }
}
