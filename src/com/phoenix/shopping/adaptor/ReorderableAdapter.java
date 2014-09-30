package com.phoenix.shopping.adaptor;

import android.widget.ListAdapter;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public interface ReorderableAdapter extends ListAdapter {

  public void exchange(int positionFrom, int positionTo);

  void notifyDataChanged();
}
