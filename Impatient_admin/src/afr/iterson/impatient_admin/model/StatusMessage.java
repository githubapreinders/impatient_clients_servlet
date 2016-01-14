package afr.iterson.impatient_admin.model;

public class StatusMessage
{
	public enum messages
	{
		settings_changed, an_error, session_stopped
	}

	private messages message;

	public StatusMessage(StatusMessage.messages m)
	{
		this.message = m;
	}

	public messages getMessage()
	{
		return message;
	}

	public void setMessage(messages message)
	{
		this.message = message;
	}

	
	
	
}
