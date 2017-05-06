package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Host implements Serializable{

    private static final long serialVersionUID = 1L;

    private String adress;
	private String alias;
	private List<User> activeUsers;
	private List<User> registeredUsers;
	
	public Host() {}
	
	public Host(String adress, String alias){
		this.adress 		 = adress;
		this.alias  		 = alias;
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

    @Override
    public String toString() {
        return "Host [adress=" + adress + ", alias=" + alias +"]";
    }
	
	
}
