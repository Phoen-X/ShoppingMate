package com.phoenix.shopping.util;

import android.widget.EditText;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class ValidationUtils {

  public static boolean isEmpty(EditText field) {
    return field == null || field.getText() == null || field.getText().toString().isEmpty();
  }

  public static boolean isDouble(final EditText field) {
    try {
      Double.parseDouble(field.getText().toString());
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
