package com.access.mongodb.mongolib.conf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class ServerSpec {
	public enum FAMILY {IP_V4, IP_V6};
	private String address = null;
	private int port = 0;
	private FAMILY family = FAMILY.IP_V4;
	private Credential credential = new Credential();
	
	public ServerSpec() {
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public FAMILY getFamily() {
		return family;
	}

	public void setFamily(FAMILY family) {
		this.family = family;
	}

	public Credential getCredential() {
		return credential;
	}

	public void setCredential(Credential credential) {
		this.credential = credential;
	}

}
