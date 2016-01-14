package afr.iterson.impatient_patient.activities;

import afr.iterson.impatient_patient.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;

public class HelpFragment extends Fragment
{
	public static final Double PIC_WIDTH = 740d;
	public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

	public static final HelpFragment newInstance(String message)
	{
		HelpFragment f = new HelpFragment();
		Bundle bdl = new Bundle(1);
		bdl.putString(EXTRA_MESSAGE, message);
		f.setArguments(bdl);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	Bundle savedInstanceState) {
	String message = getArguments().getString(EXTRA_MESSAGE);
	View v = inflater.inflate(R.layout.activity_helpscreens_fragment, container, false);
	WebView contentview = (WebView)v.findViewById(R.id.webview1);
	contentview.setOnTouchListener(new View.OnTouchListener()
	{
		
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			return (event.getAction() == MotionEvent.ACTION_MOVE);
		}
	});
	
	
	
	contentview.setPadding(5, 0, 5, 0);
	contentview.setInitialScale(getScale());
	contentview.getSettings().setUserAgentString("AndroidWebView");
	contentview.loadUrl(message);
	return v;
	}

	private int getScale()
	{
		Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int width = display.getWidth();
		Double val = new Double(width) / new Double(PIC_WIDTH);
		val = val * 100d;
		return val.intValue();
	}

}
