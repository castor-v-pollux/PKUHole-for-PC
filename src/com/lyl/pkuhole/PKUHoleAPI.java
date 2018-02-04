package com.lyl.pkuhole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lyl.pkuhole.exception.PKUHoleException;
import com.lyl.pkuhole.model.Comment;
import com.lyl.pkuhole.model.Topic;
import com.lyl.pkuhole.model.TopicType;
import com.lyl.pkuhole.model.User;
import com.lyl.pkuhole.utils.TopicTypeDeserializer;

public class PKUHoleAPI {

	public static final String PKU_HOLE_HOST = "www.pkuhelper.com";
	private static final String PKU_HOLE_LOGIN_PATH = "/services/login/login.php";
	private static final String PKU_HOLE_API_PATH = "/services/pkuhole/api.php";
	public static final String PKU_HOLE_PIC_PATH = "/services/pkuhole/images/";
	private static final String USER_AGENT = "okhttp/3.4.1";

	private static final JsonParser parser = new JsonParser();
	private static final Gson gson = new GsonBuilder().registerTypeAdapter(TopicType.class, new TopicTypeDeserializer())
			.create();

	/**
	 * Perform HTTP GET method with PKUHole server.
	 * 
	 * @param path
	 *            URL path
	 * @param args
	 *            URL arguments
	 * @return JSON Object received
	 * @throws PKUHoleException
	 */
	private static JsonElement api(String path, List<NameValuePair> args) throws PKUHoleException {
		URL url;
		try {
			url = new URIBuilder().setScheme("http").setHost(PKU_HOLE_HOST).setPath(path).addParameters(args).build()
					.toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			throw new PKUHoleException("Bad URL: " + e.getMessage());
		}
		try {

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Host", PKU_HOLE_HOST);
			conn.setRequestProperty("User-Agent", USER_AGENT);

			int status = conn.getResponseCode();
			if (status == 200) {
				InputStreamReader response = new InputStreamReader(conn.getInputStream());
				BufferedReader reader = new BufferedReader(response);
				String line;
				StringBuilder sb = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				return parser.parse(sb.toString());
			} else {
				throw new PKUHoleException(String.format("网络连接失败(%d): %s", status, conn.getResponseMessage()));
			}

		} catch (IOException e) {
			throw new PKUHoleException("网络不畅！");
		}

	}

	/**
	 * Perform HTTP POST method with PKUHole server.
	 * 
	 * @param path
	 *            URL path
	 * @param args
	 *            URL arguments
	 * @param content
	 *            POST content
	 * @return JSON Object received
	 * @throws PKUHoleException
	 */
	private static JsonElement aqi(String path, List<NameValuePair> args, List<NameValuePair> content)
			throws PKUHoleException {
		URL url;
		try {
			url = new URIBuilder().setScheme("http").setHost(PKU_HOLE_HOST).setPath(path).addParameters(args).build()
					.toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			throw new PKUHoleException("Bad URL: " + e.getMessage());
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Host", PKU_HOLE_HOST);
			conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			OutputStream out = conn.getOutputStream();
			out.write(NVP2Bytes(content));
			out.flush();
			out.close();

			int status = conn.getResponseCode();
			if (status == 200) {
				InputStreamReader response = new InputStreamReader(conn.getInputStream());
				BufferedReader reader = new BufferedReader(response);
				String line;
				StringBuilder sb = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				return parser.parse(sb.toString());
			} else {
				throw new PKUHoleException(String.format("网络连接失败(%d): %s", status, conn.getResponseMessage()));
			}
		} catch (IOException e) {
			throw new PKUHoleException("网络不畅！");
		}
	}

	private static NameValuePair pair(String name, String value) {
		return new BasicNameValuePair(name, value);
	}

	private static byte[] NVP2Bytes(List<NameValuePair> content) {
		StringBuilder sb = new StringBuilder();
		for (NameValuePair nvp : content) {
			sb.append(nvp.getName()).append("=").append(nvp.getValue()).append("&");
		}
		return sb.substring(0, sb.length() - 1).getBytes(Charset.forName("utf-8"));
	}

	/**
	 * 获取特定页树洞
	 * 
	 * @param page
	 *            页号
	 * @return 树洞列表
	 * @throws PKUHoleException
	 */
	public static Topic[] getTopics(int page) throws PKUHoleException {
		List<NameValuePair> nvp = Arrays.asList(pair("action", "getlist"), pair("p", page + ""));
		JsonObject json = api(PKU_HOLE_API_PATH, nvp).getAsJsonObject();
		if (json.get("code").getAsInt() != 0) {
			throw new PKUHoleException(json.get("msg").getAsString());
		}
		JsonElement data = json.get("data");
		if (data == null)
			return null;
		else
			return gson.fromJson(data, Topic[].class);
	}

	/**
	 * 得到单个树洞
	 * 
	 * @param pid
	 *            树洞号
	 * @return 树洞内容
	 * @throws PKUHoleException
	 */
	public static Topic getSingleTopic(int pid) throws PKUHoleException {
		List<NameValuePair> nvp = Arrays.asList(pair("action", "getone"), pair("pid", pid + ""));
		JsonObject json = api(PKU_HOLE_API_PATH, nvp).getAsJsonObject();
		if (json.get("code").getAsInt() != 0) {
			throw new PKUHoleException(json.get("msg").getAsString());
		}
		JsonElement data = json.get("data");
		if (data == null)
			return null;
		else
			return gson.fromJson(data, Topic.class);
	}

	/**
	 * 获取树洞评论
	 * 
	 * @param pid
	 *            树洞号
	 * @return 评论列表
	 * @throws PKUHoleException
	 */
	public static Comment[] getComments(int pid) throws PKUHoleException {
		List<NameValuePair> nvp = Arrays.asList(pair("action", "getcomment"), pair("pid", pid + ""));
		JsonObject json = api(PKU_HOLE_API_PATH, nvp).getAsJsonObject();
		if (json.get("code").getAsInt() != 0) {
			throw new PKUHoleException(json.get("msg").getAsString());
		}
		JsonElement data = json.get("data");
		if (data == null)
			return null;
		else
			return gson.fromJson(data, Comment[].class);
	}

	/**
	 * 搜索特定关键字的树洞
	 * 
	 * @param keywords
	 *            关键字
	 * @param pageSize
	 *            最大数量
	 * @return 树洞列表
	 * @throws PKUHoleException
	 */
	public static Topic[] searchTopics(String keywords, int pageSize) throws PKUHoleException {
		List<NameValuePair> nvp = Arrays.asList(pair("action", "search"));
		List<NameValuePair> content = Arrays.asList(pair("keywords", keywords), pair("pagesize", pageSize + ""));
		JsonObject json = aqi(PKU_HOLE_API_PATH, nvp, content).getAsJsonObject();
		if (json.get("code").getAsInt() != 0) {
			throw new PKUHoleException(json.get("msg").getAsString());
		}
		JsonElement data = json.get("data");
		if (data == null)
			return null;
		else
			return gson.fromJson(data, Topic[].class);
	}

	// The following APIs are user-specified.

	/**
	 * 登录
	 * 
	 * @param uid
	 *            学号
	 * @param password
	 *            密码
	 * @return 用户信息
	 * @throws PKUHoleException
	 */
	public static User login(String uid, String password) throws PKUHoleException {
		List<NameValuePair> nvp = Arrays.asList(pair("platform", "PC"));
		List<NameValuePair> content = Arrays.asList(pair("uid", uid), pair("password", password));
		JsonObject json = aqi(PKU_HOLE_LOGIN_PATH, nvp, content).getAsJsonObject();
		if (json.get("code").getAsInt() != 0)
			throw new PKUHoleException(json.get("msg").getAsString());
		else
			return gson.fromJson(json, User.class);
	}

	/**
	 * 获取关注话题
	 * 
	 * @param token
	 *            token
	 * @return 关注树洞列表
	 * @throws PKUHoleException
	 */
	public static Topic[] getAttentionTopics(String token) throws PKUHoleException {
		List<NameValuePair> nvp = Arrays.asList(pair("action", "getattention"));
		List<NameValuePair> content = Arrays.asList(pair("token", token));
		JsonObject json = aqi(PKU_HOLE_API_PATH, nvp, content).getAsJsonObject();
		if (json.get("code").getAsInt() != 0) {
			throw new PKUHoleException(json.get("msg").getAsString());
		}
		JsonElement data = json.get("data");
		if (data == null)
			return null;
		else
			return gson.fromJson(data, Topic[].class);
	}

	/**
	 * 发表文字树洞
	 * 
	 * @param token
	 *            token
	 * @param text
	 *            树洞内容
	 * @return 成功，返回树洞号；失败，抛出异常
	 * @throws PKUHoleException
	 */
	public static int sendTextPost(String token, String text) throws PKUHoleException {
		List<NameValuePair> nvp = Arrays.asList(pair("action", "dopost"));
		List<NameValuePair> content = Arrays.asList(pair("token", token), pair("type", "text"), pair("text", text));
		JsonObject json = aqi(PKU_HOLE_API_PATH, nvp, content).getAsJsonObject();
		if (json.get("code").getAsInt() != 0)
			throw new PKUHoleException(json.get("msg").getAsString());
		else
			return json.get("data").getAsInt();
	}

	/**
	 * 发表图片树洞: 考虑到数据的特殊性，没有用aqi接口，网络部分代码重写
	 * 
	 * @param token
	 *            token
	 * @param text
	 *            树洞内容
	 * @param image
	 * @throws PKUHoleException
	 */
	public static void sendImagePost(String token, String text, String image) throws PKUHoleException {
		URL url;
		try {
			url = new URL("http://" + PKU_HOLE_HOST + PKU_HOLE_API_PATH + "?action=dopost");
		} catch (MalformedURLException e) {
			throw new PKUHoleException("Bad URL: " + e.getMessage());
		}
		List<NameValuePair> content = Arrays.asList(pair("token", token), pair("type", "image"), pair("text", text));
		JsonObject json;
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Host", PKU_HOLE_HOST);
			conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			OutputStream out = conn.getOutputStream();
			out.write(NVP2Bytes(content));
			out.write("&data=".getBytes());
			out.write(image.getBytes());

			out.flush();
			out.close();

			int status = conn.getResponseCode();
			if (status == 200) {
				InputStreamReader response = new InputStreamReader(conn.getInputStream());
				BufferedReader reader = new BufferedReader(response);
				String line;
				StringBuilder sb = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				json = (JsonObject) parser.parse(sb.toString());
			} else {
				throw new PKUHoleException(String.format("网络连接失败(%d): %s", status, conn.getResponseMessage()));
			}
		} catch (IOException e) {
			throw new PKUHoleException("网络不畅！");
		}
		if (json.get("code").getAsInt() != 0)
			throw new PKUHoleException(json.get("msg").getAsString());
	}

	/**
	 * 发表评论
	 * 
	 * @param token
	 *            token
	 * @param pid
	 *            树洞号
	 * @param text
	 *            评论内容
	 * @return 成功，返回树洞号；失败，抛出异常
	 * @throws PKUHoleException
	 */
	public static long sendComment(String token, long pid, String text) throws PKUHoleException {
		List<NameValuePair> nvp = Arrays.asList(pair("action", "docomment"));
		List<NameValuePair> content = Arrays.asList(pair("token", token), pair("pid", pid + ""), pair("text", text));
		JsonObject json = aqi(PKU_HOLE_API_PATH, nvp, content).getAsJsonObject();
		if (json.get("code").getAsInt() != 0)
			throw new PKUHoleException(json.get("msg").getAsString());
		else
			return json.get("data").getAsLong();
	}

	/**
	 * 设置树洞关注
	 * 
	 * @param token
	 *            token
	 * @param pid
	 *            树洞号
	 * @param attention
	 *            是否关注
	 * @throws PKUHoleException
	 */
	public static void setAttention(String token, long pid, boolean attention) throws PKUHoleException {
		List<NameValuePair> nvp = Arrays.asList(pair("action", "attention"));
		List<NameValuePair> content = Arrays.asList(pair("token", token), pair("pid", pid + ""),
				pair("switch", attention ? "1" : "0"));
		JsonObject json = aqi(PKU_HOLE_API_PATH, nvp, content).getAsJsonObject();
		if (json.get("code").getAsInt() != 0)
			throw new PKUHoleException(json.get("msg").getAsString());
	}

	/**
	 * 举报树洞
	 * 
	 * @param token
	 *            token
	 * @param pid
	 *            树洞号
	 * @param reason
	 *            举报理由
	 * @return 操作是否成功
	 * @throws PKUHoleException
	 */
	public static boolean report(String token, long pid, String reason) throws PKUHoleException {
		List<NameValuePair> nvp = Arrays.asList(pair("action", "report"));
		List<NameValuePair> content = Arrays.asList(pair("token", token), pair("pid", pid + ""),
				pair("reason", reason));
		JsonObject json = aqi(PKU_HOLE_API_PATH, nvp, content).getAsJsonObject();
		return json.get("code").getAsInt() == 0;
	}
}
