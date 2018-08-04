package com.anwesh.uiprojects.messageboxdownwardview

/**
 * Created by anweshmishra on 05/08/18.
 */

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
    drawRoundRect(RectF(-size/2, -size/2, size/2, size/2), size/5, size/5, paint)
    val path : Path = Path()
    path.moveTo(-tSize/2, size/2)
    path.lineTo(0f, size/2 + tSize)
    path.lineTo(tSize/2, size/2)
    drawPath(path, paint)
    paint.color = Color.BLACK
    paint.strokeWidth = Math.min(w, h) / 60
    paint.strokeCap = Paint.Cap.ROUND
    val lineSize : Float = size/3 * sc1
    for (i in 0..2) {
        drawLine(-lineSize, 0f, lineSize, (i - 1) * size/3, paint)
    }
    restore()
}