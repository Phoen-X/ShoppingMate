package com.phoenix.shopping.util;

import java.util.ArrayList;
import java.util.List;

import android.location.Address;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class AddressUtils {
  public static String buildAddress(final Address place) {
    List<String> parts = new ArrayList<>();
    for (int i = place.getMaxAddressLineIndex(); i >= 0; i--) {
      parts.add(place.getAddressLine(i));
    }
    StringBuilder bldr = new StringBuilder();
    for (String part : parts) {
      if (part != null && !StringUtils.isEmpty(part)) {
        if (bldr.length() > 0) {
          bldr.append(", ");
        }
        bldr.append(part);
      }
    }
    return bldr.toString();
  }
}
