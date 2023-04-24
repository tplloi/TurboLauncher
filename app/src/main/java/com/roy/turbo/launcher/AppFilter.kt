package com.roy.turbo.launcher

import android.content.ComponentName
import android.text.TextUtils

abstract class AppFilter {
    abstract fun shouldShowApp(app: ComponentName?): Boolean

    companion object {
        @JvmStatic
        fun loadByName(className: String?): AppFilter? {
            return if (TextUtils.isEmpty(className)) null else try {
                val cls = className?.let {
                    Class.forName(it)
                }
                cls?.newInstance() as AppFilter?
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
                null
            } catch (e: InstantiationException) {
                e.printStackTrace()
                null
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
                null
            } catch (e: ClassCastException) {
                e.printStackTrace()
                null
            }
        }
    }
}
