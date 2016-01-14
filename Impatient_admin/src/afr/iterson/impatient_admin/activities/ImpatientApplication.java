package afr.iterson.impatient_admin.activities;

import afr.iterson.impatient_admin.retrofit.AdminSvcApi;
import android.app.Application;

public class ImpatientApplication extends Application
{
	public  AdminSvcApi api;

	public  AdminSvcApi getApi()
	{
		return api;
	}

	public void setApi(AdminSvcApi api2)
	{
		this.api = api2;
	}
}
