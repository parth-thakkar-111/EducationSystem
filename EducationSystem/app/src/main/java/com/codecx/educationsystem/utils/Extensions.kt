package com.codecx.educationsystem.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.codecx.educationsystem.R
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import java.text.DecimalFormat

fun Context.getResourceColor(colorId: Int): Int {
    return ContextCompat.getColor(this, colorId)
}

fun Context.showToast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.getMyColorStateList(colorId: Int): ColorStateList {
    return ColorStateList.valueOf(getResourceColor(colorId))
}

inline fun <reified T> Activity.startNewActivity(
    isFinish: Boolean = false,
    intent: (Intent) -> Unit = {}
) {
    startActivity(Intent(this, T::class.java).also(intent))
    if (isFinish) {
        finish()
    }
}

fun ImageView.setImage(imageId: Any?) {
    animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in).also {
        it.duration = 200L
        it.start()
    }
    val shimmer =
        Shimmer.AlphaHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
            .setDuration(1800) // how long the shimmering animation takes to do one full sweep
            .setBaseAlpha(0.7f) //the alpha of the underlying children
            .setHighlightAlpha(0.6f) // the shimmer alpha amount
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .build()
    val shimmerDrawable = ShimmerDrawable().apply {
        setShimmer(shimmer)
    }
    Glide.with(this).load(imageId).placeholder(shimmerDrawable).error(R.drawable.ic_launcher_foreground).into(this)
}

fun ImageView.setSimpleImage(imageId: Any?) {
    Glide.with(this).load(imageId).into(this)
}

fun View.beGone() {
    if (isVisible) {
        visibility = View.GONE
    }
}

fun View.beVisible() {
    if (!isVisible) {
        visibility = View.VISIBLE
    }
}

fun Long.getSizeFormat(): String {
    val decimalFormat = DecimalFormat("#.##")
    val fileSizeInKB = this / 1024
    val fileSizeInMB = fileSizeInKB / 1024
    val fileSizeInGB = fileSizeInMB / 1024
    if (fileSizeInGB != 0L) {
        return decimalFormat.format(fileSizeInGB) + " GB "
    }
    if (fileSizeInMB != 0L) {
        return decimalFormat.format(fileSizeInMB) + " MB "
    }
    return if (fileSizeInKB != 0L) {
        decimalFormat.format(fileSizeInKB) + " KB "
    } else {
        ""
    }
}

fun String.getFileNameWithOutExtension():String {
    return if (this.indexOf(".") > 0) {
        this.substring(0, this.lastIndexOf("."))
    } else {
        this
    }
}

fun String.showLog(message: String) {
    Log.d(this, message)
}