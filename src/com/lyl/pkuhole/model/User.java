package com.lyl.pkuhole.model;

public class User {

	public long id;

	public String token;

	public String name;

	public String gender;

	public String department;

	@Override
	public String toString() {
		return String.format("User[id=%d, token=%s, name=%s, gender=%s, department=%s]", id, token, name, gender,
				department);
	}

	public String toFormattedString() {
		return String.format("当前用户：\n学号：\t%d\n姓名：\t%s\n院系：\t%s", id, name, department);
	}

}
