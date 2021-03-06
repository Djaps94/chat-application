package model;

import java.io.Serializable;

public class User implements Serializable{

    private static final long serialVersionUID = 1L;

    private String username;
	private String password;
	private Host host;
	private Boolean registered    = false;
	private Boolean logged        = false;
	private Boolean notregistered = false;
	private Boolean logout        = false;
	
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

    public Boolean getRegistered() {
        return registered;
    }

    public void setRegistered(Boolean registered) {
        this.registered = registered;
    }

    public Boolean getLogged() {
        return logged;
    }

    public void setLogged(Boolean logged) {
        this.logged = logged;
    }

    public Boolean getNotregistered() {
        return notregistered;
    }

    public void setNotregistered(Boolean notregistered) {
        this.notregistered = notregistered;
    }

    public Boolean getLogout() {
        return logout;
    }

    public void setLogout(Boolean logout) {
        this.logout = logout;
    }
}
