package main;

public class User {

	private final String _id;
	private final String passwordHash;
	private final String email;
	private final String _rev;
	
	public User(String username, String passwordHash, String email) {
		this._id = username;
		this.passwordHash = passwordHash;
		this.email = email;
		this._rev = null;
	}
	
	public String username() {
		return this._id;
	}
	
	public boolean passwordValid(String password) {
		return MD5.hashPassword(password).equals(this.passwordHash);
	}
	
}
