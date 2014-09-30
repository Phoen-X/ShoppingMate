package com.phoenix.shopping.adaptor;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public abstract class AbstractReorderableAdapter<T> implements ReorderableAdapter {
  private final Context context;
  private List<T> items = new LinkedList<T>();
  private List<DataSetObserver> observers = new ArrayList<DataSetObserver>();
  private LayoutInflater inflater;

  public AbstractReorderableAdapter(final Context context, List<T> items) {
    synchronized (this) {
      this.items.addAll(items);
    }
    this.context = context;
    this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  @Override
  public void exchange(final int positionFrom, final int positionTo) {
    synchronized (this) {
      T first = getItem(positionFrom);
      T second = getItem(positionTo);

      List<T> newList = new LinkedList<T>();
      for (int i = 0; i < items.size(); i++) {
        if (i == positionTo) {
          newList.add(first);
        } else if (i == positionFrom) {
          newList.add(second);
        } else {
          newList.add(items.get(i));
        }
      }
      items = newList;
      notifyDataChanged();
    }
  }

  @Override
  public boolean areAllItemsEnabled() {
    return true;
  }

  @Override
  public boolean isEnabled(final int position) {
    return true;
  }

  @Override
  public void registerDataSetObserver(final DataSetObserver observer) {
    observers.add(observer);
  }

  @Override
  public void unregisterDataSetObserver(final DataSetObserver observer) {
    observers.remove(observer);
  }

  @Override
  public int getCount() {
    return items.size();
  }

  @Override
  public T getItem(final int position) {
    return items.get(position);
  }

  @Override
  public long getItemId(final int position) {
    return position;
  }

  @Override
  public boolean hasStableIds() {
    return false;
  }

  @Override
  public int getItemViewType(final int position) {
    return 0;
  }

  @Override
  public int getViewTypeCount() {
    return 1;
  }

  @Override
  public boolean isEmpty() {
    return items.isEmpty();
  }

  @Override
  public void notifyDataChanged() {
    for (DataSetObserver observer : observers) {
      observer.onChanged();
    }
  }
}
