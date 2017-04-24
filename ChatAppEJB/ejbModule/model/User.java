package model;

import java.io.Serializable;

public class User implements Serializable{

	private String username;
	private String password;
	private Host host;
	
	public User() {}
	
	public User(String username, String password){
		this.username = username;
		this.password = password;
	}
	
	public User(String username, String password, Host host){
		this.username = username;
		this.password = password;
		this.host     = host;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Host getHost() {
		return host;
	}
	public void setHost(Host host) {
		this.host = host;
	}
}
