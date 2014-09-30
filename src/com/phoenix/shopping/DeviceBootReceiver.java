package com.phoenix.shopping;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.phoenix.shopping.service.LocationListenerService;

/**
 * Class description here.
 * @author Vadim Vygulyarniy (http://www.luxoft.com).
 */
public class DeviceBootReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    context.startService(new Intent(context, LocationListenerService.class));
  }
}
