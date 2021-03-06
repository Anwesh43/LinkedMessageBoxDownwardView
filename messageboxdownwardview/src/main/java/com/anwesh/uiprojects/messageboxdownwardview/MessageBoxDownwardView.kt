package com.anwesh.uiprojects.messageboxdownwardview

/**
 * Created by anweshmishra on 05/08/18.
 */

import android.app.Activity
import android.graphics.*
import android.view.View
import android.view.MotionEvent
import android.content.Context

val nodes : Int = 5

fun Canvas.drawMBDNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val hGap : Float = h / nodes
    val size = hGap/3
    val tSize = hGap/8
    paint.color = Color.parseColor("#EDEDED")
    val sc1 : Float = Math.min(0.5f, scale) * 2
    val sc2 : Float = Math.min(0.5f, Math.max(0f, scale - 0.5f)) * 2
    save()
    translate(w/2, hGap * i + hGap/2 + hGap * sc2)
    drawRoundRect(RectF(-size, -size/2, size, size/2), size/5, size/5, paint)
    val path : Path = Path()
    path.moveTo(-tSize/2, size/2)
    path.lineTo(0f, size/2 + tSize)
    path.lineTo(tSize/2, size/2)
    drawPath(path, paint)
    paint.color = Color.BLACK
    paint.strokeWidth = Math.min(w, h) / 60
    paint.strokeCap = Paint.Cap.ROUND
    val lineSize : Float = 2 * size / 3 * (1 - sc1)
    for (i in 0..2) {
        drawLine(-lineSize, (i - 1) * size/3, lineSize, (i - 1) * size/3, paint)
    }
    restore()
}

class MessageBoxDownwardView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    var animationListener : AnimationListener? = null

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    fun addAnimationListener(onComplete : (Int) -> Unit, onReset : (Int) -> Unit) {
        animationListener = AnimationListener(onComplete, onReset)
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += 0.05f * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(30)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }
    }

    data class MBDNode(var i : Int, val state : State = State()) {

        private var next : MBDNode? = null

        private var prev : MBDNode? = null

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = MBDNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawMBDNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        init {
            addNeighbor()
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update({cb(i, it)})
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : MBDNode {
            var curr : MBDNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class LinkedMBD(var i : Int) {

        private var curr : MBDNode = MBDNode(0)

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scale ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : MessageBoxDownwardView) {

        private val animator : Animator = Animator(view)

        private val lmbd : LinkedMBD = LinkedMBD(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            lmbd.draw(canvas, paint)
            animator.animate {
                lmbd.update {i, scl ->
                    animator.stop()
                    when(scl) {
                        0f -> view.animationListener?.onReset?.invoke(i)
                        1f -> view.animationListener?.onComplete?.invoke(i)
                    }
                }
            }

        }

        fun handleTap() {
            lmbd.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : MessageBoxDownwardView {
            val view : MessageBoxDownwardView = MessageBoxDownwardView(activity)
            activity.setContentView(view)
            return view
        }
    }

    data class AnimationListener(var onComplete : (Int) -> Unit, var onReset : (Int) -> Unit)
}