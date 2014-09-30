package com.phoenix.shopping.data;

import com.phoenix.shopping.data.model.Purchase;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public interface PurchaseFilter {
  boolean apply(Purchase purchase);
}
