package com.roy.turbo.launcher.itf

import com.roy.turbo.launcher.sv.AppsCustomizeAsyncTask
import com.roy.turbo.launcher.AsyncTaskPageData

interface AsyncTaskCallback {
    fun run(
        task: AppsCustomizeAsyncTask?,
        data: AsyncTaskPageData?
    )
}
