package com.access.mongodb.mongolib.conf;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.stream.StreamSource;

@XmlRootElement
public class Context {
	private List<ServerSpec> servers = new ArrayList<>(0);
	private boolean journaled = false;

	public Context() {
	}

	public List<ServerSpec> getServers() {
		return servers;
	}

	public void setServers(List<ServerSpec> servers) {
		this.servers = servers;
	}

	public boolean isJournaled() {
		return journaled;
	}

	public void setJournaled(boolean journaled) {
		this.journaled = journaled;
	}

	public void save(OutputStream os) throws Exception {
		Map<String, Object> properties = new HashMap<String, Object>(0);
		JAXBContext jc = JAXBContext.newInstance(new Class[] {Context.class}, properties);

		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(this, os);
	}
	
	public static Context load(InputStream is) throws Exception {
		Map<String, Object> properties = new HashMap<String, Object>(0);
		JAXBContext jc = JAXBContext.newInstance(new Class[] {Context.class}, properties);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		return (Context)unmarshaller.unmarshal(new StreamSource(is));
	}
}
