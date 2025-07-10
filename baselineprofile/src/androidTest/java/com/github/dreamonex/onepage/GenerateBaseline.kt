package com.github.dreamonex.onepage

import androidx.benchmark.macro.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GenerateBaseline {
    @get:Rule val rule = BaselineProfileRule()
    @Test fun startup() = rule.collect(packageName = "com.github.dreamonex.onepage") {
        startActivityAndWait()
    }
}