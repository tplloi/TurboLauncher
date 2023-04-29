package com.roy.turbo.launcher.itf;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;

import com.roy.turbo.launcher.DragController;
import com.roy.turbo.launcher.Launcher;
import com.roy.turbo.launcher.view.DragView;

public interface DropTarget {

	class DragObject {
		public int x = -1;
		public int y = -1;

		/** X offset from the upper-left corner of the cell to where we touched. */
		public int xOffset = -1;

		/** Y offset from the upper-left corner of the cell to where we touched. */
		public int yOffset = -1;

		/**
		 * This indicates whether a drag is in final stages, either drop or
		 * cancel. It differentiates onDragExit, since this is called when the
		 * drag is ending, above the current drag target, or when the drag moves
		 * off the current drag object.
		 */
		public boolean dragComplete = false;

		/** The view that moves around while you drag. */
		public DragView dragView = null;

		/** The data associated with the object being dragged */
		public Object dragInfo = null;

		/** Where the drag originated */
		public DragSource dragSource = null;

		/** Post drag animation runnable */
		public Runnable postAnimationRunnable = null;

		/** Indicates that the drag operation was cancelled */
		public boolean cancelled = false;

		/**
		 * Defers removing the DragView from the DragLayer until after the drop
		 * animation.
		 */
		public boolean deferDragViewCleanupPostAnimation = true;

		public DragObject() {
		}
	}

	public static class DragEnforcer implements DragController.DragListener {
		int dragParity = 0;

		public DragEnforcer(Context context) {
			Launcher launcher = (Launcher) context;
			launcher.getDragController().addDragListener(this);
		}

		public void onDragEnter() {
			dragParity++;
		}

		public void onDragExit() {
			dragParity--;
		}

		@Override
		public void onDragStart(DragSource source, Object info, int dragAction) {

		}

		@Override
		public void onDragEnd() {

		}
	}

	/**
	 * Used to temporarily disable certain drop targets
	 * 
	 * @return boolean specifying whether this drop target is currently enabled
	 */
	boolean isDropEnabled();

	void onDrop(DragObject dragObject);

	void onDragEnter(DragObject dragObject);

	void onDragOver(DragObject dragObject);

	void onDragExit(DragObject dragObject);

	/**
	 * Handle an object being dropped as a result of flinging to delete and will
	 * be called in place of onDrop(). (This is only called on objects that are
	 * set as the DragController's fling-to-delete target.
	 */
	void onFlingToDelete(DragObject dragObject, int x, int y, PointF vec);

	boolean acceptDrop(DragObject dragObject);

	// These methods are implemented in Views
	void getHitRectRelativeToDragLayer(Rect outRect);

	void getLocationInDragLayer(int[] loc);

	int getLeft();

	int getTop();
}
