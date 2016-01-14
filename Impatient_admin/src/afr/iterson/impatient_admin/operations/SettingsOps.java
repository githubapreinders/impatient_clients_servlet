package afr.iterson.impatient_admin.operations;

import java.lang.ref.WeakReference;

import afr.iterson.impatient_admin.model.AdminSettings;
import afr.iterson.impatient_admin.utils.ConfigurableOps;
import afr.iterson.impatient_admin.utils.ContextView;

public class SettingsOps implements ConfigurableOps<SettingsOps.View>
{
private static final String TAG = SettingsOps.class.getSimpleName();

	private WeakReference<SettingsOps.View> mSettingsOps;
	private AdminSettings currentSettings;

	public interface View extends ContextView
	{
		public void finish();

		public void displaySettings(AdminSettings settings);

	}

	@Override
	public void onConfiguration(View view, boolean firstTimeIn)
	{
		mSettingsOps = new WeakReference<>(view);
		if (firstTimeIn)
		{
			currentSettings = new AdminSettings();
		}
	mSettingsOps.get().displaySettings(currentSettings);
	}

	
	public AdminSettings getCurrentSettings()
	{
		return currentSettings;
	}


	public void setCurrentSettings(AdminSettings currentSettings)
	{
		this.currentSettings = currentSettings;
	}
	
	

}
