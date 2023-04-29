package com.roy.turbo.launcher

import android.content.ContentValues
import java.util.Arrays

/**
 * Represents a folder containing shortcuts or apps.
 */
internal class FolderInfo : ItemInfo() {
    /**
     * Whether this folder has been opened
     */
    @JvmField
    var opened = false

    /**
     * The apps and shortcuts and hidden status
     */
    @JvmField
    var contents = ArrayList<ShortcutInfo>()

    @JvmField
    var hidden = false
    var listeners = ArrayList<FolderListener>()

    init {
        itemType = LauncherSettings.Favorites.ITEM_TYPE_FOLDER
    }

    /**
     * Add an app or shortcut
     */
    fun add(item: ShortcutInfo) {
        contents.add(item)
        for (i in listeners.indices) {
            listeners[i].onAdd(item)
        }
        itemsChanged()
    }

    /**
     * Remove an app or shortcut. Does not change the DB.
     */
    fun remove(item: ShortcutInfo) {
        contents.remove(item)
        for (i in listeners.indices) {
            listeners[i].onRemove(item)
        }
        itemsChanged()
    }

    var title: CharSequence?
        get() = super.title
        set(title) {
            this.title = title
            for (i in listeners.indices) {
                listeners[i].onTitleChanged(title)
            }
        }

    public override fun onAddToDatabase(values: ContentValues) {
        super.onAddToDatabase(values)
        values.put(LauncherSettings.Favorites.TITLE, title.toString())
        values.put(LauncherSettings.Favorites.HIDDEN, if (hidden) 1 else 0)
    }

    fun addListener(listener: FolderListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: FolderListener) {
        listeners.remove(listener)
    }

    fun itemsChanged() {
        for (i in listeners.indices) {
            listeners[i].onItemsChanged()
        }
    }

    public override fun unbind() {
        super.unbind()
        listeners.clear()
    }

    internal interface FolderListener {
        fun onAdd(item: ShortcutInfo?)
        fun onRemove(item: ShortcutInfo?)
        fun onTitleChanged(title: CharSequence?)
        fun onItemsChanged()
    }

    override fun toString(): String {
        return ("FolderInfo(id=" + id + " type=" + itemType
                + " container=" + container + " screen=" + screenId
                + " cellX=" + cellX + " cellY=" + cellY + " spanX=" + spanX
                + " spanY=" + spanY + " dropPos=" + Arrays.toString(dropPos) + ")")
    }
}
