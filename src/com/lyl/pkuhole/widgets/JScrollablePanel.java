package com.lyl.pkuhole.widgets;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.Scrollable;

public class JScrollablePanel extends JPanel implements Scrollable {

	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 20;
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 100;
	}

	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	public boolean getScrollableTracksViewportHeight() {
		return true;
	}

}
