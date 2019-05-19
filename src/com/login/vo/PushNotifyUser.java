package com.login.vo;

public class PushNotifyUser {

	private String email;
	private String _id;//google ID
	private boolean sendUpdates = true;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public boolean isSendUpdates() {
		return sendUpdates;
	}
	public void setSendUpdates(boolean sendUpdates) {
		this.sendUpdates = sendUpdates;
	}
	@Override
	public String toString() {
		return "PushNotifyUser [email=" + email + ", _id=" + _id + ", sendUpdates=" + sendUpdates + "]";
	}
}
