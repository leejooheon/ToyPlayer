package com.jooheon.clean_architecture.presentation.base.extensions

import androidx.fragment.app.Fragment
import com.jooheon.clean_architecture.presentation.utils.hideSoftInput

fun Fragment.hideKeyboard() = hideSoftInput(requireActivity())