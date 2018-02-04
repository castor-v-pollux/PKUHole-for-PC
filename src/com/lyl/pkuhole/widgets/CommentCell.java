package com.lyl.pkuhole.widgets;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import com.lyl.pkuhole.model.Comment;

public class CommentCell extends JTextArea implements VerticalList.ListItemListener {

	private static final Font BorderTitleFont = new Font(Font.SANS_SERIF, Font.BOLD, 18);

	private static final Color SelectedColor = new Color(0xff, 0xff, 0xcc);

	private TitledBorder border;

	private Comment comment;

	public CommentCell(Comment comment) {
		this.comment = comment;
		init();
	}

	private void init() {
		setEditable(false);
		setLineWrap(true);
		setText(comment.toFormattedString());

		border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.CYAN, 5), null,
				TitledBorder.LEFT, TitledBorder.TOP, BorderTitleFont);
		border.setTitle("#" + comment.cid);
		setBorder(border);
	}

	@Override
	public void onSelected(boolean isSelected) {
		setBackground(isSelected ? SelectedColor : Color.WHITE);
	}

	@Override
	public void onClicked() {
		/*
		 * Do comment. Delegated to listener in TopicWindow and do nothing here.
		 */
	}

	public Comment getComment() {
		return comment;
	}

}
