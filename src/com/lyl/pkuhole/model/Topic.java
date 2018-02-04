package com.lyl.pkuhole.model;

import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.lyl.pkuhole.PKUHoleAPI;
import com.lyl.pkuhole.utils.TimeUtils;
import com.lyl.pkuhole.utils.UIUtils;
import com.lyl.pkuhole.widgets.TopicCell;
import com.lyl.pkuhole.widgets.TopicCellWithImage;

public class Topic extends Observable {

	public int pid;

	public TopicType type;

	public String text;

	public long timestamp;

	public int reply;

	public int likenum;

	public ImageIcon imageIcon;
	public ImageIcon scaledImageIcon;

	/*
	 * When a topic is image type, url is the image url and extra is the image size;
	 * When a topic is
	 */
	public String url;

	public long extra;

	private boolean isLoaded = false;

	@Override
	public String toString() {
		return String.format("Topic[pid=%d, type=%s, text=%s, timestamp=%d, reply=%d, likenum=%d, url=%s, extra=%d]",
				pid, type, text, timestamp, reply, likenum, url, extra);
	}

	public String toFormattedString() {
		return String.format("%s\n\n%s                ÆÀÂÛ:%d\t¹Ø×¢:%d", text, TimeUtils.timeFormatter(timestamp), reply,
				likenum);
	}

	public JComponent getCell(boolean isNewWindowEnabled) {
		if (type == TopicType.TEXT || type == TopicType.AUDIO) {
			return new TopicCell(this, isNewWindowEnabled);
		} else {
			return new TopicCellWithImage(this, isNewWindowEnabled);
		}
	}

	public synchronized void loadImage() {
		if (type != TopicType.IMAGE)
			return;
		if (isLoaded)
			return;
		new Thread() {
			@Override
			public void run() {
				URL url;
				try {
					url = new URL("http://" + PKUHoleAPI.PKU_HOLE_HOST + PKUHoleAPI.PKU_HOLE_PIC_PATH + Topic.this.url);
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return;
				}
				try {
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(5000);
					Image image = ImageIO.read(conn.getInputStream());
					imageIcon = new ImageIcon(image);
					int width = image.getWidth(null);
					int height = image.getHeight(null);
					if (height > UIUtils.MAX_IMAGE_HEIGHT) {
						width = UIUtils.MAX_IMAGE_HEIGHT * width / height;
						height = UIUtils.MAX_IMAGE_HEIGHT;
						scaledImageIcon = new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_DEFAULT));
					} else {
						scaledImageIcon = imageIcon;
					}
					isLoaded = true;
					setChanged();
					notifyObservers(null);
				} catch (FileNotFoundException e) {
					/**
					 * This exception is thrown when a picture on the server has been deleted and
					 * isn't accessible, as is an earlier picture. In this case, we still consider
					 * the image loaded, notify the observers and let them to notify the user to
					 * avoid re-accessing later.
					 */
					isLoaded = true;
					setChanged();
					notifyObservers(null);
				} catch (IIOException e) {
					/**
					 * This exception is thrown when a picture on the server is broken and cannot be
					 * parsed by ImageIO. In this case, we consider the image loaded, too, notify
					 * the observers and let them to notify the user to avoid re-accessing later.
					 */
					isLoaded = true;
					setChanged();
					notifyObservers(null);
				} catch (IOException e) {
					setChanged();
					notifyObservers(null);
				}
			}
		}.start();
	}

	public boolean isLoaded() {
		return isLoaded;
	}

}
