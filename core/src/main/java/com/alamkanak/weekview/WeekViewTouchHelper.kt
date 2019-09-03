package com.alamkanak.weekview

import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityEvent
import androidx.core.view.accessibility.AccessibilityEventCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.customview.widget.ExploreByTouchHelper

internal class WeekViewTouchHelper(host: View) : ExploreByTouchHelper(host) {

    private val weekView = host as WeekView<*>

    override fun getVirtualViewAt(x: Float, y: Float): Int {
        return if (weekView.selectedEventChip != null)
            weekView.selectedEventChip?.event!!.id.toInt()
        else
            INVALID_ID
    }

    override fun getVisibleVirtualViews(virtualViewIds: MutableList<Int>?) {
        val count = weekView.eventChipCache.size()
        if (count > 0 && weekView.selectedEventChip != null) {
            for (eventChip in weekView.eventChipCache.allEventChips) {
                virtualViewIds?.add(eventChip.event.id.toInt())
            }
        }
    }

    override fun onPerformActionForVirtualView(virtualViewId: Int, action: Int, arguments: Bundle?): Boolean {
        return when (action) {
            AccessibilityNodeInfoCompat.ACTION_CLICK -> {
                sendEventForVirtualView(virtualViewId, AccessibilityEvent.TYPE_VIEW_CLICKED)
                true
            }
            else -> false
        }
    }

    override fun onPopulateNodeForVirtualView(virtualViewId: Int, node: AccessibilityNodeInfoCompat) {
        node.contentDescription = getContentDescriptionForEvent()
        node.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK)

        val rectF = weekView.selectedEventChip?.rect
        if (rectF != null) {
            val bounds = Rect(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt())
            node.setBoundsInParent(bounds)
        } else {
            node.setBoundsInParent(Rect(0,0,0,0))
        }
    }

    override fun onPopulateEventForVirtualView(virtualViewId: Int, event: AccessibilityEvent) {
        event.contentDescription = getContentDescriptionForEvent()
    }

    private fun getContentDescriptionForEvent(): String? {
        return weekView.selectedEventChip?.event?.title ?: ""
    }

}