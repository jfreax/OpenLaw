package de.jdsoft.law.helper;

public interface CallbackInterface {
	public void onFinish(CallerInterface caller);
    public void onFinish(CallerInterface caller, String result);
}
