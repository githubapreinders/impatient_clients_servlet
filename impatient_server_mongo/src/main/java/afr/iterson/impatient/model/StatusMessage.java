package afr.iterson.impatient.model;

public class StatusMessage
{
	public enum messages
	{
		settings_changed, an_error, session_stopped, admin, patient
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
