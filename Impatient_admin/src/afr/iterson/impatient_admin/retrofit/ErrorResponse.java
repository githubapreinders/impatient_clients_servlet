package afr.iterson.impatient_admin.retrofit;

public class ErrorResponse {
	  Error error;
	  
	  public static class Error {
	    Data data;
	  
	    public static class Data {
	       String message;
	    }  
	  }
	}