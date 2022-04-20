package com.jooheon.clean_architecture.presentation.base.extensions

import android.app.Activity
import androidx.fragment.app.Fragment
import com.jooheon.clean_architecture.presentation.common.openActivityAndClearStack
import com.jooheon.clean_architecture.presentation.utils.hideSoftInput

fun Fragment.hideKeyboard() = hideSoftInput(requireActivity())

fun <A : Activity> Fragment.openActivityAndClearStack(activity: Class<A>) {
    requireActivity().openActivityAndClearStack(activity)
}