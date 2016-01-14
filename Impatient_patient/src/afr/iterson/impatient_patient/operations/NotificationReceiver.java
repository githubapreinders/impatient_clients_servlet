package afr.iterson.impatient_patient.operations;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver
{

public static final String TAG = NotificationReceiver.class.getSimpleName();	
	
	@Override
	public void onReceive(Context context, Intent intent)
	{

		Log.d(TAG, "broadcast received...");
	}

}
