package models;

public class User {
	private long id;
	private int age;
	private String gender;
	
	public User(long id, int age, String gender) {
		this.id = id;
		this.age = age;
		this.gender = gender;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	
}
