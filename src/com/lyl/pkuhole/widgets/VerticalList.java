package com.lyl.pkuhole.widgets;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/**
 * 自己实现的垂直列表组件VerticalList。
 * 
 * Swing库的JList有一些bug：
 * 1.它通过renderer绘制组件，而不是真正由每个组件构成，因此当组件发生改变时很难对模型进行通知（如图片框加载等）；
 * 2.同时，当JList被置于JScrollPane中时，水平将组件增大，其内容会随之增大，但再水平缩小时，内容将保持原来的大小。
 * 
 * VerticaList保持其中每个组件的引用，将组件实际添加到列表中，解决了问题1；
 * Scrollable实现中让getScrollableTracksViewportWidth返回true，解决了问题2。
 */
public class VerticalList extends JPanel implements Scrollable {

	private static final int PREFERRED_HEIGHT = 800;

	private List<JComponent> components;
	private List<MouseAdapter> listeners;

	private int itemCount;
	private int selectedId;

	private ActionListener actionListener;

	private GridBagLayout gb;
	private GridBagConstraints gbc;

	public VerticalList() {
		itemCount = 0;
		selectedId = -1;
		components = new ArrayList<JComponent>();
		listeners = new ArrayList<MouseAdapter>();

		gb = new GridBagLayout();
		gbc = new GridBagConstraints();

		setLayout(gb);

		setFocusable(true);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					if (selectedId > 0)
						onItemSelected(selectedId - 1);
					break;
				case KeyEvent.VK_DOWN:
					if (selectedId != -1 && selectedId < itemCount - 1)
						onItemSelected(selectedId + 1);
					break;
				case KeyEvent.VK_ENTER:
					if (selectedId != -1)
						onItemClicked(selectedId);
					break;
				}
			}
		});

	}

	/**
	 * 添加组件
	 * 
	 * @param component
	 *            Item to be added. Must be an instance of ListItemListener.
	 * @return If component to be added is not instance of ListItemListener, return
	 *         false; else return true.
	 */
	public boolean addItem(JComponent component) {
		if (component instanceof ListItemListener) {
			components.add(component);
			MouseAdapter listener = new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					switch (e.getClickCount()) {
					case 1:
						onItemSelected(listeners.indexOf(this));
						break;
					case 2:
						onItemSelected(listeners.indexOf(this));
						onItemClicked(listeners.indexOf(this));
						break;
					}
				}
			};
			listeners.add(listener);
			component.addMouseListener(listener);
			component.setFocusable(false);
			itemCount++;
			return true;
		} else
			return false;
	}

	/**
	 * 进行真正的布局。必须在组件添加完全后调用。
	 */
	public void commit() {
		if (components.isEmpty())
			return;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		for (int i = 0; i < itemCount - 1; i++) {
			gb.setConstraints(components.get(i), gbc);
			add(components.get(i));
		}
		gbc.weighty = 1;
		gb.setConstraints(components.get(itemCount - 1), gbc);
		add(components.get(itemCount - 1));
		validate();
		Container c = getParent();
		if (c != null && c instanceof JScrollPane) {
			JScrollPane scrollPane = (JScrollPane) c;
			JScrollBar bar = scrollPane.getVerticalScrollBar();
			bar.setValue(bar.getMinimum());
		}
	}

	/**
	 * 移除所有组件
	 */
	@Override
	public void removeAll() {
		super.removeAll();
		components.clear();
		listeners.clear();
		itemCount = 0;
		selectedId = -1;
	}

	public int getItemCount() {
		return itemCount;
	}

	/**
	 * 组件被选择时，将事件传递给组件
	 * 
	 * @param id
	 *            被选中的序号
	 */
	private void onItemSelected(int id) {
		requestFocus();
		if (id == selectedId)
			return;
		if (selectedId != -1)
			((ListItemListener) components.get(selectedId)).onSelected(false);
		((ListItemListener) components.get(id)).onSelected(true);
		selectedId = id;
	}

	/**
	 * 组件被点击时，将事件传递给组件和监听器
	 * 
	 * @param id
	 *            被点击的序号
	 */
	private void onItemClicked(int id) {
		((ListItemListener) components.get(id)).onClicked();
		if (actionListener != null)
			actionListener.actionPerformed(null);
	}

	/**
	 * 设置公共的组件监听器
	 * 
	 * @param listener
	 *            监听器实例
	 */
	public void setActionListener(ActionListener listener) {
		actionListener = listener;
	}

	/**
	 * 被选中的组件
	 */
	public JComponent getSelectedJComponent() {
		if (selectedId == -1)
			return null;
		else
			return components.get(selectedId);
	}

	/**
	 * Interface that its containing components must implement.
	 */
	public static interface ListItemListener {

		void onSelected(boolean isSelected);

		void onClicked();

	}

	/**
	 * --- The Scrollable Implementation --- Mostly copied from JList.
	 */

	private int locationToIndex(Point p) {
		for (int i = 0; i < itemCount; i++)
			if (components.get(i).getBounds().contains(p))
				return i;
		return -1;
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		if (components.isEmpty()) {
			return getPreferredSize();
		}
		int width = 0;
		for (Component c : components) {
			width = Math.max(width, c.getPreferredSize().width);
		}
		return new Dimension(width, PREFERRED_HEIGHT);
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		if (orientation == SwingConstants.HORIZONTAL)
			return 1;
		int row = locationToIndex(visibleRect.getLocation());
		if (row == -1)
			return 0;
		else {
			/* Scroll Down */
			if (direction > 0) {
				Rectangle r = components.get(row).getBounds();
				return (r == null) ? 0 : r.height - (visibleRect.y - r.y);
			}
			/* Scroll Up */
			else {
				Rectangle r = components.get(row).getBounds();
				/*
				 * The first row is completely visible and it's row 0. We're done.
				 */
				if ((r.y == visibleRect.y) && (row == 0)) {
					return 0;
				}
				/*
				 * The first row is completely visible, return the height of the previous row or
				 * 0 if the first row is the top row of the list.
				 */
				else if (r.y == visibleRect.y) {
					if (row == 0)
						return 0;
					else
						return components.get(row).getHeight();
				}
				/*
				 * The first row is partially visible, return the height of hidden part.
				 */
				else {
					return visibleRect.y - r.y;
				}
			}
		}
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		if (orientation == SwingConstants.HORIZONTAL)
			return visibleRect.width;
		int inc = visibleRect.height;
		/* Scroll Down */
		if (direction > 0) {
			// last cell is the lowest left cell
			int last = locationToIndex(new Point(visibleRect.x, visibleRect.y + visibleRect.height - 1));
			if (last != -1) {
				Rectangle lastRect = components.get(last).getBounds();
				if (lastRect != null) {
					inc = lastRect.y - visibleRect.y;
					if ((inc == 0) && (last < itemCount - 1)) {
						inc = lastRect.height;
					}
				}
			}
		}
		/* Scroll Up */
		else {
			int newFirst = locationToIndex(new Point(visibleRect.x, visibleRect.y - visibleRect.height));
			int first = locationToIndex(visibleRect.getLocation());
			if (newFirst != -1) {
				if (first == -1) {
					first = locationToIndex(visibleRect.getLocation());
				}
				Rectangle newFirstRect = components.get(newFirst).getBounds();
				Rectangle firstRect = components.get(first).getBounds();
				if ((newFirstRect != null) && (firstRect != null)) {
					while ((newFirstRect.y + visibleRect.height < firstRect.y + firstRect.height)
							&& (newFirstRect.y < firstRect.y)) {
						newFirst++;
						newFirstRect = components.get(newFirst).getBounds();
					}
					inc = visibleRect.y - newFirstRect.y;
					if ((inc <= 0) && (newFirstRect.y > 0)) {
						newFirst--;
						newFirstRect = components.get(newFirst).getBounds();
						if (newFirstRect != null) {
							inc = visibleRect.y - newFirstRect.y;
						}
					}
				}
			}
		}
		return inc;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

}
