package com.phoenix.shopping.adaptor;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.phoenix.shopping.data.model.ShopAddress;

/**
 * Class description here.
 * @author Vadim Vygulyarniy. 13.10.2014 22:48
 */
public class ShopListAdapter extends ArrayAdapter<ShopAddress> {
  private final LayoutInflater inflater;

  public ShopListAdapter(Context context, List<ShopAddress> addresses) {
    super(context, 0, addresses);
    this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  @Override
  public long getItemId(final int position) {
    return position;
  }

  @Override
  public View getView(final int position, final View convertView, final ViewGroup parent) {
    View view = convertView;
    if (view == null) {
      view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
      view.setTag(new ViewHolder(view));
    }

    ViewHolder itemView = (ViewHolder) view.getTag();

    final ShopAddress item = getItem(position);
    fillAddressData(itemView, item);

    return view;
  }

  private void fillAddressData(final ViewHolder itemView, final ShopAddress item) {
    itemView.getAddress().setText(item.getDescription());
  }

  static class ViewHolder {
    private TextView  address;

    public ViewHolder(View view) {
      this.address = (TextView) view.findViewById(android.R.id.text1);
    }

    public TextView getAddress() {
      return address;
    }
  }
}
