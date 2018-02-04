package com.lyl.pkuhole.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.lyl.pkuhole.exception.PKUHoleException;

import sun.misc.BASE64Encoder;

public class BASE64Utils {

	public static String imageToString(BufferedImage image) throws PKUHoleException {
		BASE64Encoder encoder = new BASE64Encoder();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "png", output);
		} catch (IOException e) {
			throw new PKUHoleException("Í¼Æ¬×ªÂëÊ§°Ü£¡");
		}
		try {
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] result = output.toByteArray();
		return encoder.encode(result);
	}

}
