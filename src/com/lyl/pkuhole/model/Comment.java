package com.lyl.pkuhole.model;

import com.lyl.pkuhole.utils.TimeUtils;
import com.lyl.pkuhole.widgets.CommentCell;

public class Comment {

	public int pid;

	public long cid;

	public long timestamp;

	public String name;

	public String text;

	private String formattedString;

	@Override
	public String toString() {
		return String.format("Comment[pid=%d, cid=%d, timestamp=%d, name=%s, text=%s]", pid, cid, timestamp, name,
				text);
	}

	public String toFormattedString() {
		if (formattedString == null)
			formattedString = String.format("%s\n\n%s", text, TimeUtils.timeFormatter(timestamp));
		return formattedString;
	}

	public CommentCell getCell() {
		return new CommentCell(this);
	}

}
