package model;

import java.util.ArrayList;
import java.util.List;

public class Host {

	private String adress;
	private String alias;
	private List<Host> hosts;
	private List<User> activeUsers;
	private List<User> registeredUsers;
	
	public Host() {}
	
	public Host(String adress, String alias){
		this.adress 		 = adress;
		this.alias  		 = alias;
		this.hosts  		 = new ArrayList<>();
		this.activeUsers 	 = new ArrayList<>();
		this.registeredUsers = new ArrayList<>();
	}
	
	
	public String getAdress() {
		return adress;
	}
	public void setAdress(String adress) {
		this.adress = adress;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}

	public List<Host> getHosts() {
		return hosts;
	}

	public void setHosts(List<Host> hosts) {
		this.hosts = hosts;
	}

	public List<User> getActiveUsers() {
		return activeUsers;
	}

	public void setActiveUsers(List<User> activeUsers) {
		this.activeUsers = activeUsers;
	}

	public List<User> getRegisteredUsers() {
		return registeredUsers;
	}

	public void setRegisteredUsers(List<User> registeredUsers) {
		this.registeredUsers = registeredUsers;
	}
	
	
}
