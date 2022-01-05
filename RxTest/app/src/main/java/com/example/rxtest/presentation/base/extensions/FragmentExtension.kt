package com.example.rxtest.presentation.base.extensions

import androidx.fragment.app.Fragment
import com.example.rxtest.presentation.utils.hideSoftInput

fun Fragment.hideKeyboard() = hideSoftInput(requireActivity())