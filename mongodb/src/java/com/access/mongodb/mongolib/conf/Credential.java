package com.access.mongodb.mongolib.conf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Credential {
	public enum MECHANISM_TYPE {NONE, PLAINSASL, MONGODBCR, GSSAPI, X509, SHA1};
	private String userName = "";
	private String database = "";
	private String password = "";
	private MECHANISM_TYPE mechanism = MECHANISM_TYPE.NONE;
	public Credential() {
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public MECHANISM_TYPE getMechanism() {
		return mechanism;
	}
	public void setMechanism(MECHANISM_TYPE mechanism) {
		this.mechanism = mechanism;
	}

}
