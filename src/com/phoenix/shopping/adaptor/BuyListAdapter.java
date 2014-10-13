package com.phoenix.shopping.adaptor;

import java.util.List;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.phoenix.shopping.R;
import com.phoenix.shopping.data.model.Purchase;
import com.phoenix.shopping.util.StringUtils;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class BuyListAdapter extends ArrayAdapter<Purchase> {

  private final LayoutInflater inflater;

  public BuyListAdapter(Context context, final List<Purchase> items) {
    super(context, 0, items);
    this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  @Override
  public long getItemId(final int position) {
    return getItem(position).getId();
  }

  @Override
  public View getView(final int position, View convertView, final ViewGroup parent) {
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.shopping_list_item, parent, false);
      assert convertView != null;
      convertView.setTag(new ViewHolder(convertView));
    }

    ViewHolder itemView = (ViewHolder) convertView.getTag();

    final Purchase item = getItem(position);
    fillItemView(itemView, item);

    return convertView;
  }

  private void fillItemView(final ViewHolder itemView, final Purchase item) {
    assert itemView != null;
    if (item.isSold()) {
      itemView.getItemName().setTextAppearance(getContext(), R.style.item_name_bought);
      Spannable spannable = new SpannableString(item.getName());
      spannable.setSpan(new StrikethroughSpan(), 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      itemView.getItemName().setText(spannable);
    } else {
      itemView.getItemName().setTextAppearance(getContext(), R.style.item_name_not_bought);
      itemView.getItemName().setText(item.getName());
    }
    if (!StringUtils.isEmpty(item.getQuantity())) {
      itemView.getQuantity().setText(String.valueOf(item.getQuantity()));
      itemView.getItemType().setText(item.getType());
    } else {
      itemView.getQuantity().setText("");
      itemView.getItemType().setText("");
    }

  }

  public void remove(int position) {
    remove(getItem(position));
  }

  static class ViewHolder {
    private TextView itemName;
    private TextView quantity;
    private TextView itemType;

    public ViewHolder(View view) {
      this.itemName = (TextView) view.findViewById(R.id.itemName);
      this.quantity = (TextView) view.findViewById(R.id.itemQuantity);
      this.itemType = (TextView) view.findViewById(R.id.itemType);
    }

    public TextView getItemName() {
      return itemName;
    }

    public TextView getQuantity() {
      return quantity;
    }

    public TextView getItemType() {
      return itemType;
    }
  }
}
