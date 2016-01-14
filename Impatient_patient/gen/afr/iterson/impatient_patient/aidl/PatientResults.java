/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/ap/Documents/workspace_2/Impatient_patient/src/afr/iterson/impatient_patient/aidl/PatientResults.aidl
 */
package afr.iterson.impatient_patient.aidl;
/**
 * Interface defining the method that receives callbacks from the
 * PollingService.  This method should be implemented by the
 * PatientActivity.
 */
public interface PatientResults extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements afr.iterson.impatient_patient.aidl.PatientResults
{
private static final java.lang.String DESCRIPTOR = "afr.iterson.impatient_patient.aidl.PatientResults";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an afr.iterson.impatient_patient.aidl.PatientResults interface,
 * generating a proxy if needed.
 */
public static afr.iterson.impatient_patient.aidl.PatientResults asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof afr.iterson.impatient_patient.aidl.PatientResults))) {
return ((afr.iterson.impatient_patient.aidl.PatientResults)iin);
}
return new afr.iterson.impatient_patient.aidl.PatientResults.Stub.Proxy(obj);
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
case TRANSACTION_sendResults:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<afr.iterson.impatient_patient.aidl.Patient> _arg0;
_arg0 = data.createTypedArrayList(afr.iterson.impatient_patient.aidl.Patient.CREATOR);
this.sendResults(_arg0);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements afr.iterson.impatient_patient.aidl.PatientResults
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
     * This one-way (non-blocking) method allows PollingService
     * to return the  Patient associated with a
     * one-way PatientRequest.getPatient() call.
     */
@Override public void sendResults(java.util.List<afr.iterson.impatient_patient.aidl.Patient> results) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeTypedList(results);
mRemote.transact(Stub.TRANSACTION_sendResults, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_sendResults = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
/**
     * This one-way (non-blocking) method allows PollingService
     * to return the  Patient associated with a
     * one-way PatientRequest.getPatient() call.
     */
public void sendResults(java.util.List<afr.iterson.impatient_patient.aidl.Patient> results) throws android.os.RemoteException;
}
