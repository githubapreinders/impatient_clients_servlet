package afr.iterson.impatient_patient.retrofit;

import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;
import afr.iterson.impatient_patient.activities.LoginActivity;
import android.content.Context;
import android.content.Intent;

public class PatientSvc
{
	public static final String CLIENT_ID = "mobile";

	private static PatientSvcApi proxy;

	public static synchronized PatientSvcApi getOrShowLogin(Context ctx) {
		if (proxy != null) {
			return proxy;
		} else {
			Intent i = new Intent(ctx, LoginActivity.class);
			ctx.startActivity(i);
			return null;
		}
	}

	public static synchronized PatientSvcApi init(String server, String user,
			String pass) {

		proxy = new SecuredRestBuilder()
				.setLoginEndpoint(server + PatientSvcApi.TOKEN_PATH)
				.setUsername(user)
				.setPassword(pass)
				.setClientId(CLIENT_ID)
				.setClient(
						new ApacheClient(new UnsafeHttpClient()))
				.setEndpoint(server).setLogLevel(LogLevel.NONE).build()
				.create(PatientSvcApi.class);

		return proxy;
	}

}
