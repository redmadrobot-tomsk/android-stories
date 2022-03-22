package draggableview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView

class LockableNestedScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

    private var isScrollable = true
    private var isPaused = false

    private var draggableViewListener: DraggableViewListener? = null

    init {
        (context as? Activity)?.also {
            draggableViewListener = it as? DraggableViewListener
        }
    }

    fun setScrollEnabled(isScrollEnabled: Boolean) {
        isScrollable = isScrollEnabled
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                if (!isPaused) {
                    isPaused = true
                    draggableViewListener?.onDrag()
                }
            }
            MotionEvent.ACTION_UP -> {
                isPaused = false
                draggableViewListener?.onStopDrag()
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent) =
        isScrollable && super.onInterceptTouchEvent(ev)

    interface DraggableViewListener {
        fun onDrag()
        fun onStopDrag()
    }
}
