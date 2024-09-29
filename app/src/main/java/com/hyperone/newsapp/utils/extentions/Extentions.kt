package com.hyperone.newsapp.utils.extentions

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.bumptech.glide.Glide
import com.hyperone.newsapp.R

inline fun View.onDebouncedListener(
    delayInClick: Long = 500L,
    crossinline listener: (View) -> Unit,
) {
    val enableAgain = Runnable { isEnabled = true }
    setOnClickListener {
        if (isEnabled) {
            isEnabled = false
            postDelayed(enableAgain, delayInClick)
            listener(it)
        }
    }
}

fun NavController.customNavigate(
    @IdRes sourceId: Int,
    @IdRes destinationId: Int,
    isInclusive: Boolean = false,
    @IdRes popUpTo: Int = -1,
    data: Bundle? = null,
) {
    val navOption = NavOptions.Builder().apply {
        setEnterAnim(R.anim.slide_from_out_right_to_center)
        setExitAnim(R.anim.slide_from_center_to_out_left)
        setPopEnterAnim(R.anim.slide_from_out_left_to_center)
        setPopExitAnim(R.anim.slide_from_center_to_out_right)
        if (isInclusive) {
            setPopUpTo(destinationId, true)
        }
    }.build()
    navigate(destinationId, data, navOption)
}

fun NavController.customNavigate(
    @IdRes destinationId: Int,
    isInclusive: Boolean = false,
    @IdRes popUpTo: Int = -1,
    data: Bundle? = null,
) {
    val navOption = NavOptions.Builder().apply {
        setEnterAnim(R.anim.slide_from_out_right_to_center)
        setExitAnim(R.anim.slide_from_center_to_out_left)
        setPopEnterAnim(R.anim.slide_from_out_left_to_center)
        setPopExitAnim(R.anim.slide_from_center_to_out_right)
        setPopUpTo(popUpTo, isInclusive)
    }.build()

    navigate(destinationId, data, navOption)
}

fun NavController.customNavigate(
    @IdRes destinationId: Int,
    isInclusive: Boolean = false,
    data: Bundle? = null,
) {
    val navOption = NavOptions.Builder().apply {
        setEnterAnim(R.anim.slide_from_out_right_to_center)
        setExitAnim(R.anim.slide_from_center_to_out_left)
        setPopEnterAnim(R.anim.slide_from_out_left_to_center)
        setPopExitAnim(R.anim.slide_from_center_to_out_right)
    }.build()

    navigate(destinationId, data, navOption)
}

fun Activity.hideKeyboard() {
    val imm: InputMethodManager =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = currentFocus ?: View(this)
    imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun ImageView.loadImage(
    url: String?, @DrawableRes placeHolder: Int = R.drawable.ic_launcher_foreground
) {
    Glide.with(this.context).load(url).placeholder(placeHolder).error(placeHolder).into(this)
}

fun <T : Parcelable> Bundle.getParcelableCompat(key: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, clazz)
    } else {
        @Suppress("DEPRECATION")
        getParcelable(key) as? T
    }
}
