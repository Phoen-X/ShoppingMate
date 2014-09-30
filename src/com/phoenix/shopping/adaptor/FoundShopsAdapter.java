package com.phoenix.shopping.adaptor;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.shopping.R;

import java.util.List;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class FoundShopsAdapter extends ArrayAdapter<Address> {
  private final LayoutInflater inflater;
  private int checkedAddress = -1;

  static class ViewHolder {
    private ImageView selectedBtn;
    private TextView mainAddress;
    private TextView secondaryAddress;

    public ViewHolder(View view) {
      this.selectedBtn = (ImageView) view.findViewById(R.id.selectedImg);
      this.mainAddress = (TextView) view.findViewById(R.id.mainAddress);
      this.secondaryAddress = (TextView) view.findViewById(R.id.secondaryAddress);
    }

    public ImageView getSelectedBtn() {
      return selectedBtn;
    }

    public TextView getMainAddress() {
      return mainAddress;
    }

    public TextView getSecondaryAddress() {
      return secondaryAddress;
    }
  }

  public FoundShopsAdapter(Context context, List<Address> addresses) {
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
      view = inflater.inflate(R.layout.found_address_item, parent, false);
      view.setTag(new ViewHolder(view));
    }

    ViewHolder itemView = (ViewHolder) view.getTag();

    final Address item = getItem(position);
    fillAddressData(itemView, item, checkedAddress == position);

    return view;
  }

  private void fillAddressData(final ViewHolder itemView, final Address item, final boolean checked) {
    itemView.getSecondaryAddress().setText(getSecondaryAddressLine(item));
    itemView.getMainAddress().setText(getMainAddressLine(item));
    if (checked) {
      itemView.getSelectedBtn().setImageState(new int[]{android.R.attr.state_checked}, false);
    } else {
      itemView.getSelectedBtn().setImageState(new int[]{}, false);
    }
    itemView.getSelectedBtn().invalidate();

  }

  private String getMainAddressLine(final Address address) {
    StringBuilder line = new StringBuilder();
    if (address.getThoroughfare() != null) {
      append(line, address.getThoroughfare());
      append(line, address.getSubThoroughfare());
    } else {
      append(line, address.getAddressLine(0));
    }
    return line.toString();
  }

  private String getSecondaryAddressLine(final Address address) {
    StringBuilder line = new StringBuilder();
    append(line, address.getCountryName());
    if (address.getLocality() == null) {
      append(line, address.getAdminArea());
      append(line, address.getSubAdminArea());
    } else {
      append(line, address.getLocality());
    }
    return line.toString();
  }

  private void append(final StringBuilder bldr, final String value) {
    if (value != null) {
      if (bldr.length() > 0) {
        bldr.append(", ");
      }
      bldr.append(value);
    }
  }

  public void check(int position) {
    checkedAddress = position;
    notifyDataSetChanged();
  }

  public Address getChecked() {
    return checkedAddress >= 0 ? getItem(checkedAddress) : null;
  }
}
