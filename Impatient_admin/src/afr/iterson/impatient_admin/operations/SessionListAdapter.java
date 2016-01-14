package afr.iterson.impatient_admin.operations;

import java.util.ArrayList;
import java.util.List;

import afr.iterson.impatient_admin.R;
import afr.iterson.impatient_admin.aidl.Patient;
import afr.iterson.impatient_admin.aidl.PatientStatus;
import afr.iterson.impatient_admin.services.StatusService;
import afr.iterson.impatient_admin.utils.Utils;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Show the view for each Video's meta-data in a ListView.
 */
public class SessionListAdapter extends ArrayAdapter<Patient>
{
	/**
	 * Allows access to application-specific resources and classes.
	 */
	private final Context mContext;

	/**
	 * ArrayList to hold list of Videos that is shown in ListView.
	 */
	private List<Patient> sessionList = new ArrayList<>();
	
	private int selectedItem;

	private SessionViewOps mOps;
	
	static class ViewHolder
	{
		ImageView image;
		TextView name;
		TextView appointmenttime;
		TextView waitingtime;
		TextView min;
		PatientStatus status;
	}

	public SessionListAdapter(Context context, final SessionViewOps sessionViewOps)
	{
		super(context, 0);
		mContext = context;
		mOps = sessionViewOps;
	}

	/**
	 * 
	 */
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final ViewHolder holder;

		if (convertView == null)
		{
			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.cardview_listitem, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.patient_name);
			holder.appointmenttime = (TextView) convertView.findViewById(R.id.patient_appointment_time);
			holder.waitingtime = (TextView) convertView.findViewById(R.id.patient_waiting_time);
			holder.min = (TextView) convertView.findViewById(R.id.patient_min);
			holder.image = (ImageView) convertView.findViewById(R.id.patient_status);
			
			convertView.setTag(holder);
		} else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		final Patient patient = (Patient) getItem(position);
		if (patient != null)
		{
			holder.image.setImageResource(getImageItem(patient.getStatus()));
			holder.name.setText(patient.makeFullName());
			holder.appointmenttime.setText(Utils.convertTimeMillisToReadableFormatHoursAndMinutes(patient.getAppointmentDate()));
			holder.status = patient.getStatus();
			if(patient.getStatus() == PatientStatus.unavailable)
			{
				holder.min.setVisibility(View.INVISIBLE);
				holder.waitingtime.setVisibility(View.INVISIBLE);
			}
			else
			{
				holder.min.setVisibility(View.VISIBLE);
				holder.waitingtime.setVisibility(View.VISIBLE);
			}
			holder.waitingtime.setText(String.valueOf(Utils.convertTimeMillisToReadableFormatMinutes(patient.getWaitingTime())));
			holder.image.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					holder.status = holder.status.next();
					patient.setStatus(holder.status);
					holder.image.setImageResource(getImageItem(holder.status));
					
					Intent intent = StatusService.makeIntent(mContext, StatusService.REQ_CHANGESTATUS);
					patient.setStatus(holder.status);
					intent.putExtra(StatusService.KEY_PATIENT, patient);
					mContext.startService(intent);
				}
			});
		}
		highlightItem(position, convertView);
		
		
		return convertView;
	}

	private void highlightItem(int position, View result) {
        if(position == selectedItem) {
            // setting a drawable as the background for the selected item
            result.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.listitem_selected));
        } else {
            // setting drawables for all the non selected items
            result.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.listitem_normal));
        }
    }
    
    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
        notifyDataSetChanged();
    }
	
	
	
	
	private int getImageItem(PatientStatus status)
	{
				
		switch (status)
		{
		case unavailable:
		{
			return R.drawable.patient_waiting_unavailable;
		}
		case available:
		{
			return R.drawable.patient_waiting_checked_in;
		}
		case currently_unavailable:
		{
			return R.drawable.patient_waiting_temp_unavailable;
		}
		case under_treatment:
		{
			return R.drawable.patient_under_treatmentkopie;
		}
		default:
		{
			return R.drawable.patient_waiting_unavailable;
		}

		}
	}

	/**
	 * Adds a Patient to the Adapter and notify the change.
	 */
	public void add(Patient patient)
	{
		sessionList.add(patient);
		notifyDataSetChanged();
	}
	
	
	public void insert(Patient patient, int postiion)
	{
		sessionList.add(postiion, patient);
		notifyDataSetChanged();
	}
	

	/**
	 * Used to move patients around the list
	 */
	
	public boolean swap(int originalposition, int newposition)
	{
		if(originalposition == sessionList.size()-1 && newposition == sessionList.size()
				||originalposition == 0 && newposition ==-1)
		{
			return false;
		}
		Patient parking = sessionList.get(originalposition);
		sessionList.set(originalposition, sessionList.get(newposition));
		sessionList.set(newposition, parking);
		notifyDataSetChanged();
		return true;
	}
	
	
	/**
	 * Removes a Patient from the Adapter and notify the change.
	 */
	public void remove(Patient patient)
	{
		sessionList.remove(patient);
		notifyDataSetChanged();
	}

	/**
	 * Get the List of Patients from Adapter.
	 */
	public List<Patient> getSessionList()
	{
		return sessionList;
	}

	/**
	 * Set the Adapter to list of Patients.
	 */
	public void setSessionList(List<Patient> patients)
	{
		this.sessionList = patients;
		notifyDataSetChanged();
	}

	/**
	 * Get the no of Patients in adapter.
	 */
	public int getCount()
	{
		return sessionList.size();
	}

	/**
	 * Get Pqtient from a given position.
	 */
	public Patient getItem(int position)
	{
		return sessionList.get(position);
	}

		public long getItemId(int position)
	{
		return position;
	}

	public int getSelectedItem()
	{
		return selectedItem;
	}

	
}
