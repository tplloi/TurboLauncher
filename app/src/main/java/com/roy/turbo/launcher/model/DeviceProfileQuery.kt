package com.roy.turbo.launcher.model

import android.graphics.PointF

class DeviceProfileQuery internal constructor(
    var widthDps: Float,
    var heightDps: Float,
    var value: Float
) {
    var dimens: PointF = PointF(widthDps, heightDps)
}
