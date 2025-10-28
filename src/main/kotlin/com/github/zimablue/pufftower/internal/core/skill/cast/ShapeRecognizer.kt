package com.github.zimablue.pufftower.internal.core.skill.cast

import kotlin.math.*

// 简单点类型，避免频繁使用 Pair
data class Pt(val x: Double, val y: Double)
data class Template(val name: String, val points: List<Pt>)

class ShapeRecognizer(
    private val squareSize: Double = 250.0,       // 归一化尺度方框大小
    private val resampleCount: Int = 32,          // 重采样点数（64足够稳定）
    private val angleRange: Double = Math.PI / 4, // 允许旋转搜索的角范围（±45°）
    private val anglePrecision: Double = Math.toRadians(2.0), // 搜索精度（2°）
    private val minScore: Double = 0.60           // 最低可接受得分，低于则认为未知
) {

    private val templates = mutableMapOf<String,Template>()
    private val halfDiagonal = 0.5 * hypot(squareSize, squareSize)
    private val phi = 0.5 * (sqrt(5.0) - 1.0) // 黄金分割常数 ≈ 0.618

    init {
        // 预置模板
        addTemplate("Circle", CIRCLE(resampleCount))
        addTemplate("Triangle", TRIANGLE)
        addTemplate("Square", SQUARE)
        addTemplate("HorizontalLine", HORIZONTAL_LINE)
        addTemplate("Heart", HEART(resampleCount))
        addTemplate("Caret", CARET)
        addTemplate("InvertedCaret", INVERTED_CARET)
        addTemplate("Diamond", DIAMOND)
    }

    companion object {
        // ========= 预置模板 =========

        fun CIRCLE(resampleCount: Int): List<Pt> {
            val r = 100.0
            val cx = 0.0
            val cy = 0.0
            val list = ArrayList<Pt>(resampleCount)
            val total = resampleCount - 1 // 闭合
            for (i in 0..total) {
                val a = 2.0 * Math.PI * i / total
                list.add(Pt(cx + r * cos(a), cy + r * sin(a)))
            }
            return list
        }

        val TRIANGLE: List<Pt> by lazy {
            // 等边三角形，绕边一笔画闭合
            val s = 200.0
            val h = s * sqrt(3.0) / 2.0
            val a = Pt(0.0, -h / 2.0)          // 顶点
            val b = Pt(s / 2.0, h / 2.0)       // 右下
            val c = Pt(-s / 2.0, h / 2.0)      // 左下
            listOf(a, b, c, a)          // 闭合
        }

        val SQUARE: List<Pt> by lazy {
            val s = 200.0
            val half = s / 2.0
            val p1 = Pt(-half, -half)
            val p2 = Pt(half, -half)
            val p3 = Pt(half, half)
            val p4 = Pt(-half, half)
            listOf(p1, p2, p3, p4, p1) // 闭合
        }

        val HORIZONTAL_LINE: List<Pt> by lazy {
            val s = 200.0
            val half = s / 2.0
            val p1 = Pt(-half, 0.0)
            val p2 = Pt(half, 0.0)
            listOf(p1, p2) // 不闭合
        }

        // 心形：使用经典参数方程采样（点数由 resampleCount 决定）
        fun HEART(resampleCount: Int): List<Pt> {
            val k = 6.5 // 缩放系数，使整体尺寸与其它模板相近
            val list = ArrayList<Pt>(resampleCount)
            val total = resampleCount - 1
            for (i in 0..total) {
                val t = 2.0 * Math.PI * i / total
                val x = 16.0 * sin(t).pow(3.0)
                val y = 13.0 * cos(t) - 5.0 * cos(2.0 * t) - 2.0 * cos(3.0 * t) - cos(4.0 * t)
                list.add(Pt(k * x, -k * y)) // 取负 y 让心形尖朝下（视觉更常见）
            }
            return list
        }

        // 正角（形似 ^）：三个点，不闭合
        val CARET: List<Pt> by lazy {
            val s = 200.0
            val half = s / 2.0
            listOf(
                Pt(-half, half / 2.0), // 左底
                Pt(0.0, -half),        // 顶点
                Pt(half, half / 2.0)   // 右底
            )
        }

        // 倒角（形似 V）：正角上下反转
        val INVERTED_CARET: List<Pt> by lazy {
            CARET.map { Pt(it.x, -it.y) }
        }

        // 菱形：四个角并闭合
        val DIAMOND: List<Pt> by lazy {
            val s = 200.0
            val half = s / 2.0
            val top = Pt(0.0, -half)
            val right = Pt(half, 0.0)
            val bottom = Pt(0.0, half)
            val left = Pt(-half, 0.0)
            listOf(top, right, bottom, left, top)
        }

    }

    // 对外主接口：输入 List<Pair<Double, Double>>，输出图形类型中文字符串
    fun recognize(raw: List<Pair<Double, Double>>, useTemplates: Set<String> = emptySet()): String {
        if (raw.size < 3) return "未知"
        val pts = raw.map { Pt(it.first, it.second) }
        // 如果路径长度过短，直接未知
        if (pathLength(pts) < 1e-8) return "未知"

        val candidate = normalize(pts)

        var bestDist = Double.MAX_VALUE
        var bestName = "未知"

        for ((name,tpl) in templates) {
            if(useTemplates.isNotEmpty() && name !in useTemplates) continue
            val d = distanceAtBestAngle(candidate, tpl.points, -angleRange, angleRange, anglePrecision)
            if (d < bestDist) {
                bestDist = d
                bestName = tpl.name
            }
        }
        val score = 1.0 - bestDist / halfDiagonal
        return if (score >= minScore) bestName else "未知"
    }

    // 可扩展添加自定义模板（例如五角星等）
    fun addTemplate(name: String, rawPoints: List<Pt>) : ShapeRecognizer{
        val processed = normalize(rawPoints)
        templates[name] = Template(name, processed)
        return this
    }

    fun removeTemplate(name: String) : ShapeRecognizer{
        templates.remove(name)
        return this
    }

    private fun normalize(points: List<Pt>): List<Pt> {
        var pts = resample(points, resampleCount)
        val angle = indicativeAngle(pts)
        pts = rotateBy(pts, -angle)
        pts = scaleToSquare(pts, squareSize)
        pts = translateToOrigin(pts)
        return pts
    }

    // ========= $1 Recognizer核心方法 =========

    private fun resample(points: List<Pt>, n: Int): List<Pt> {
        val I = pathLength(points) / (n - 1)
        val newPts = ArrayList<Pt>(n)
        newPts.add(points.first())

        var d = 0.0
        var i = 1
        var prev = points[0]

        while (i < points.size) {
            val curr = points[i]
            val dist = distance(prev, curr)
            if (dist == 0.0) {
                i++
                continue
            }
            if (d + dist >= I) {
                val t = (I - d) / dist
                val nx = prev.x + t * (curr.x - prev.x)
                val ny = prev.y + t * (curr.y - prev.y)
                val q = Pt(nx, ny)
                newPts.add(q)
                prev = q
                d = 0.0
            } else {
                d += dist
                prev = curr
                i++
            }
        }
        // 若因精度问题不足 n，则补最后一个点
        while (newPts.size < n) {
            newPts.add(points.last())
        }
        return newPts
    }

    private fun scaleToSquare(points: List<Pt>, size: Double): List<Pt> {
        var minX = Double.POSITIVE_INFINITY
        var minY = Double.POSITIVE_INFINITY
        var maxX = Double.NEGATIVE_INFINITY
        var maxY = Double.NEGATIVE_INFINITY
        for (p in points) {
            if (p.x < minX) minX = p.x
            if (p.y < minY) minY = p.y
            if (p.x > maxX) maxX = p.x
            if (p.y > maxY) maxY = p.y
        }
        val width = maxX - minX
        val height = maxY - minY
        val scale = if (width > height) size / width else size / height
        return points.map { Pt(it.x * scale, it.y * scale) }
    }

    private fun translateToOrigin(points: List<Pt>): List<Pt> {
        val c = centroid(points)
        return points.map { Pt(it.x - c.x, it.y - c.y) }
    }

    private fun rotateBy(points: List<Pt>, angle: Double): List<Pt> {
        val cosA = cos(angle)
        val sinA = sin(angle)
        return points.map { p ->
            Pt(p.x * cosA - p.y * sinA, p.x * sinA + p.y * cosA)
        }
    }

    private fun indicativeAngle(points: List<Pt>): Double {
        val c = centroid(points)
        return atan2(c.y - points[0].y, c.x - points[0].x)
    }

    private fun centroid(points: List<Pt>): Pt {
        var sx = 0.0
        var sy = 0.0
        for (p in points) {
            sx += p.x
            sy += p.y
        }
        val n = points.size.toDouble()
        return Pt(sx / n, sy / n)
    }

    private fun distanceAtBestAngle(points: List<Pt>, template: List<Pt>, a: Double, b: Double, threshold: Double): Double {
        var a0 = a
        var b0 = b
        var x1 = phi * a0 + (1.0 - phi) * b0
        var x2 = (1.0 - phi) * a0 + phi * b0
        var f1 = distanceAtAngle(points, template, x1)
        var f2 = distanceAtAngle(points, template, x2)

        while (abs(b0 - a0) > threshold) {
            if (f1 < f2) {
                b0 = x2
                x2 = x1
                f2 = f1
                x1 = phi * a0 + (1.0 - phi) * b0
                f1 = distanceAtAngle(points, template, x1)
            } else {
                a0 = x1
                x1 = x2
                f1 = f2
                x2 = (1.0 - phi) * a0 + phi * b0
                f2 = distanceAtAngle(points, template, x2)
            }
        }
        return min(f1, f2)
    }

    private fun distanceAtAngle(points: List<Pt>, template: List<Pt>, angle: Double): Double {
        val rotated = rotateBy(points, angle)
        return pathDistance(rotated, template)
    }

    private fun pathDistance(a: List<Pt>, b: List<Pt>): Double {
        var d = 0.0
        val n = a.size
        for (i in 0 until n) {
            d += distance(a[i], b[i])
        }
        return d / n
    }

    private fun pathLength(points: List<Pt>): Double {
        var d = 0.0
        for (i in 1 until points.size) {
            d += distance(points[i - 1], points[i])
        }
        return d
    }

    private fun distance(p1: Pt, p2: Pt): Double {
        return hypot(p2.x - p1.x, p2.y - p1.y)
    }
}

// ================== 使用示例 ==================
// val recognizer = ShapeRecognizer()
// val input: List<Pair<Double, Double>> = listOf(/* 你的手绘点序列 */)
// val result = recognizer.recognize(input) // 返回："圆形"、"三角形"、"方形" 或 "未知"
