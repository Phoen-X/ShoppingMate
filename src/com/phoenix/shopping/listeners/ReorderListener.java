package com.phoenix.shopping.listeners;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import com.phoenix.shopping.adaptor.ReorderableAdapter;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class ReorderListener implements View.OnTouchListener {

  public static final String TAG = "REORDER";

  private static class Point {
    private int x;
    private int y;

    private Point(final int x, final int y) {
      this.x = x;
      this.y = y;
    }

    public int getX() {
      return x;
    }

    public int getY() {
      return y;
    }

    @Override
    public String toString() {
      return String.format("(%s;%s)", x, y);
    }
  }

  private ListView listView;
  private View reorderingItem;
  private int reorderingIndex;
  private Point startPoint;
  private static final int pickedOffset = 10;

  public ReorderListener(ListView listView) {
    this.listView = listView;
  }

  private Point getViewCoords(View view, MotionEvent event) {
    int[] coords = new int[2];
    view.getLocationOnScreen(coords);
    int x = (int) event.getRawX() - coords[0];
    int y = (int) event.getRawY() - coords[1];

    return new Point(x, y);
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public synchronized boolean onTouch(final View v, final MotionEvent event) {
    switch (event.getActionMasked()) {
      case MotionEvent.ACTION_DOWN: {
        if (reorderingItem == null) {
          this.startPoint = getViewCoords(listView, event);
          this.reorderingItem = findListItem(startPoint);

          if (reorderingItem != null) {
            reorderingIndex = listView.getPositionForView(reorderingItem);
            reorderingItem.setTranslationX(pickedOffset);
          }
        }
        return true;
      }
      case MotionEvent.ACTION_MOVE: {
        if (reorderingItem == null || listView == null) {
          return false;
        }
        Point underPoint = getViewCoords(listView, event);
        View underItem = findListItem(underPoint, reorderingItem);
        int underIndex = getIndex(listView, underItem);
        ReorderableAdapter adapter = (ReorderableAdapter) listView.getAdapter();

        if (underIndex >= 0 &&
            underItem != null &&
            Math.abs(reorderingItem.getY() - underItem.getY()) < (reorderingItem.getHeight() / 2)) {
          //if (!underItem.equals(lastFrom) || !reorderingItem.equals(lastTo)) {
          Log.v(TAG, String.format("exchange item: %s -> %s", adapter.getItem(reorderingIndex), adapter.getItem(underIndex)));

          adapter.exchange(reorderingIndex, underIndex);
          //underItem.setY(startPoint.getY());
          reorderingItem.setTranslationX(0);
          reorderingItem.setTranslationY(0);
          reorderingItem = underItem;
          reorderingIndex = underIndex;
          startPoint = underPoint;
          //listView.invalidate();
          //adapter.notifyDataChanged();
          //}
        }

        if (reorderingItem != null) {
          //Log.v(TAG, "translate for " + (event.getY() - startPoint.getY()) + " px");
          reorderingItem.setTranslationY(event.getY() - startPoint.getY() - pickedOffset);
          reorderingItem.setTranslationX(pickedOffset);
        }
        return true;
      }
      case MotionEvent.ACTION_UP: {
        if (reorderingItem != null) {
          reorderingItem.setTranslationY(0);
          reorderingItem.setTranslationX(0);
          reorderingItem = null;
        }
        return true;
      }
      case MotionEvent.ACTION_CANCEL: {
        if (listView != null) {
          listView.invalidate();
          reorderingItem = null;
        }
        return true;
      }
    }
    return false;
  }

  private View findListItem(final Point point, final View exclude) {
    View child;
    Rect rect = new Rect();
    int childCount = listView.getChildCount();
    for (int i = 0; i < childCount; i++) {
      child = listView.getChildAt(i);
      if(child != null) {
        if (child.equals(exclude)) {
          continue;
        }

        child.getHitRect(rect);
        if (rect.contains(point.getX(), point.getY())) {
          return child;
        }
      }
    }

    return null;
  }

  private int getIndex(final ListView listView, final View item) {
    if (listView == null || item == null) {
      return -1;
    }
    for (int i = 0; i < listView.getChildCount(); i++) {
      View child = listView.getChildAt(i);
      if (child != null && child.equals(item)) {
        return i;
      }
    }

    return -1;
  }

  private View findListItem(Point point) {
    View child;
    Rect rect = new Rect();
    int childCount = listView.getChildCount();
    for (int i = 0; i < childCount; i++) {
      child = listView.getChildAt(i);
      if (child != null) {
        child.getHitRect(rect);
        if (rect.contains(point.getX(), point.getY())) {
          return child;
        }
      }
    }

    return null;
  }
}
