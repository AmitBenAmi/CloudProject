package main;

public class User {

	private final String _id;
	private final String userName;
	private final String passwordHash;
	private final String email;
	private final String _rev;
	
	public User(String username, String passwordHash, String email) {
		this._id = null;
		this.userName = username;
		this.passwordHash = passwordHash;
		this.email = email;
		this._rev = null;
	}
	
	public String username() {
		return this.userName;
	}
	
	public boolean passwordValid(String password) {
		return MD5.hashPassword(password).equals(this.passwordHash);
	}
	
}
