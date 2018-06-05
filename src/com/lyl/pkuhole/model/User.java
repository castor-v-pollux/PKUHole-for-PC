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
		return String.format("��ǰ�û���\nѧ�ţ�\t%d\n������\t%s\nԺϵ��\t%s", id, name, department);
	}

	public static class Inner extends User {
		private int code;
		private String msg;

		public boolean success() {
			return code == 0;
		}

		public String getErrorMsg() {
			return msg;
		}
	}

}
