package afr.iterson.impatient_admin.retrofit;

import retrofit.ErrorHandler;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;
import retrofit.client.Response;
import afr.iterson.impatient_admin.activities.LoginActivity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AdminSvc
{
	private final static String TAG = AdminSvc.class.getSimpleName();

	public static final String CLIENT_ID = "mobile";

	private static AdminSvcApi proxy;
	
	private static Context context;

	public static synchronized AdminSvcApi getOrShowLogin(Context ctx)
	{
		if (proxy != null)
		{
			Log.d(TAG, "Proxy is okay...");
			return proxy;
		} else
		{
			Log.d(TAG, "Proxy = null...");
			Intent i = new Intent(ctx, LoginActivity.class);
			ctx.startActivity(i);
			return null;
		}
	}

	public static synchronized AdminSvcApi init(String server, String user, String pass)
	{

		Log.d(TAG, "Serverurl: " + server + "\n" + " Username: " + user + " Password: " + pass);
		proxy = new SecuredRestBuilder().setLoginEndpoint(server + AdminSvcApi.TOKEN_PATH).setUsername(user)
				.setPassword(pass).setClientId(CLIENT_ID).setClient(new ApacheClient(new UnsafeHttpClient()))
				.setEndpoint(server).setLogLevel(LogLevel.NONE).build().create(AdminSvcApi.class);

		return proxy;
	}
	
}
