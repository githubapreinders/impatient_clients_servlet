package afr.iterson.impatient_admin.retrofit;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import android.util.Log;

class MyErrorHandler implements ErrorHandler
{

	private final static String TAG = MyErrorHandler.class.getSimpleName();
  

	    @SuppressWarnings("deprecation")
		@Override
	    public Throwable handleError(RetrofitError cause) {
	        String errorDescription;

	        if (cause.isNetworkError()) {
	            errorDescription = "networkerror";
	        } else {
	            if (cause.getResponse() == null) {
	                errorDescription = "other kind of error the network";
	            } else {

	                // Error message handling - return a simple error to Retrofit handlers..
	                try {
	                    ErrorResponse errorResponse = (ErrorResponse) cause.getBodyAs(ErrorResponse.class);
	                    errorDescription = errorResponse.error.data.message;
	                } catch (Exception ex) {
	                    try {
	                    	errorDescription = String.format("The following http error occured: %d", cause.getResponse().getStatus());
	                    } catch (Exception ex2) {
	                        Log.e(TAG, "handleError: " + ex2.getLocalizedMessage());
	                        errorDescription = "Error unknown";
	                    }
	                }
	            }
	        }

	        return new Exception(errorDescription);
	    }
}

