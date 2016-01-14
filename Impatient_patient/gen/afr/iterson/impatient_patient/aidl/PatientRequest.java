/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/ap/Documents/workspace_2/Impatient_patient/src/afr/iterson/impatient_patient/aidl/PatientRequest.aidl
 */
package afr.iterson.impatient_patient.aidl;
/**
 * Interface defining the method implemented within
 * PollingService that provides asynchronous access to the
 * Spring web service.
 */
public interface PatientRequest extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements afr.iterson.impatient_patient.aidl.PatientRequest
{
private static final java.lang.String DESCRIPTOR = "afr.iterson.impatient_patient.aidl.PatientRequest";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an afr.iterson.impatient_patient.aidl.PatientRequest interface,
 * generating a proxy if needed.
 */
public static afr.iterson.impatient_patient.aidl.PatientRequest asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof afr.iterson.impatient_patient.aidl.PatientRequest))) {
return ((afr.iterson.impatient_patient.aidl.PatientRequest)iin);
}
return new afr.iterson.impatient_patient.aidl.PatientRequest.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getPatient:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
afr.iterson.impatient_patient.aidl.PatientResults _arg1;
_arg1 = afr.iterson.impatient_patient.aidl.PatientResults.Stub.asInterface(data.readStrongBinder());
this.getPatient(_arg0, _arg1);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements afr.iterson.impatient_patient.aidl.PatientRequest
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
    * A one-way (non-blocking) call to the PollingService that
    * retrieves information about the currentpatient list from the Spring web service.  
    * The PollingService subsequently uses the
    * PatientListResults parameter to return a List of Patient
    * containing the results from theSpring web service back
    * to the SessionViewActivity via the one-way sendResults() method.
    */
@Override public void getPatient(java.lang.String medicalrecordid, afr.iterson.impatient_patient.aidl.PatientResults results) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(medicalrecordid);
_data.writeStrongBinder((((results!=null))?(results.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_getPatient, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_getPatient = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
/**
    * A one-way (non-blocking) call to the PollingService that
    * retrieves information about the currentpatient list from the Spring web service.  
    * The PollingService subsequently uses the
    * PatientListResults parameter to return a List of Patient
    * containing the results from theSpring web service back
    * to the SessionViewActivity via the one-way sendResults() method.
    */
public void getPatient(java.lang.String medicalrecordid, afr.iterson.impatient_patient.aidl.PatientResults results) throws android.os.RemoteException;
}
