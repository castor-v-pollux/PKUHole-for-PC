package com.lyl.pkuhole.network;

import com.google.gson.GsonBuilder;
import com.lyl.pkuhole.exception.PKUHoleException;
import com.lyl.pkuhole.model.Comment;
import com.lyl.pkuhole.model.Topic;
import com.lyl.pkuhole.model.TopicType;
import com.lyl.pkuhole.model.User;
import com.lyl.pkuhole.utils.TopicTypeDeserializer;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {

	public static final String errorMsg = "网络不畅！";

	private static final String baseUrl = "http://www.pkuhelper.com";

	private static PKUHoleService service;

	public static void init() {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(baseUrl)
				.addConverterFactory(GsonConverterFactory
						.create(new GsonBuilder()
								.registerTypeAdapter(TopicType.class, new TopicTypeDeserializer())
								.create()))
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build();
		service = retrofit.create(PKUHoleService.class);
	}

	/**
	 * 获取特定页树洞
	 * 
	 * @param page
	 *            页号
	 * @return 树洞列表
	 */
	public static Observable<Topic[]> getTopics(int page) {
		return service.getTopics(page)
				.subscribeOn(Schedulers.single())
				.flatMap(new Interceptor<>());
	}

	/**
	 * 得到单个树洞
	 * 
	 * @param pid
	 *            树洞号
	 * @return 树洞内容
	 */
	public static Observable<Topic> getSingleTopic(long pid) {
		return service.getSingleTopic(pid)
				.subscribeOn(Schedulers.single())
				.flatMap(new Interceptor<>());
	}

	/**
	 * 获取树洞评论
	 * 
	 * @param pid
	 *            树洞号
	 * @return 评论列表
	 */
	public static Observable<Comment[]> getComments(long pid) {
		return service.getComments(pid)
				.subscribeOn(Schedulers.single())
				.flatMap(new Interceptor<>());
	}

	/**
	 * 搜索特定关键字的树洞
	 * 
	 * @param keywords
	 *            关键字
	 * @param pageSize
	 *            最大数量
	 * @return 树洞列表
	 */
	public static Observable<Topic[]> searchTopics(String keywords, int pageSize) {
		return service.searchTopics(keywords, pageSize)
				.subscribeOn(Schedulers.single())
				.flatMap(new Interceptor<>());
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
	 */
	public static Observable<User> login(String uid, String password) {
		return service.login(uid, password)
				.subscribeOn(Schedulers.single())
				.flatMap(inner -> {
					if (inner.success())
						return Observable.just(inner);
					else
						return Observable.error(new PKUHoleException(inner.getErrorMsg()));
				});
	}

	/**
	 * 获取关注话题
	 * 
	 * @param token
	 *            token
	 * @return 关注树洞列表
	 */
	public static Observable<Topic[]> getAttentionTopics(String token) {
		return service.getAttentionTopics(token)
				.subscribeOn(Schedulers.single())
				.flatMap(new Interceptor<>());
	}

	/**
	 * 发表文字树洞
	 * 
	 * @param token
	 *            token
	 * @param text
	 *            树洞内容
	 * @return 成功，返回树洞号；失败，抛出异常
	 */
	public static Observable<Long> sendTextPost(String token, String text) {
		return service.sendPost(token, "text", text, null)
				.subscribeOn(Schedulers.single())
				.flatMap(new Interceptor<>());
	}

	/**
	 * 发表图片树洞
	 * 
	 * @param token
	 *            token
	 * @param text
	 *            树洞内容
	 * @param image
	 *            图片内容
	 * @return
	 */
	public static Observable<Long> sendImagePost(String token, String text, String image) {
		return service.sendPost(token, "image", text, image)
				.subscribeOn(Schedulers.single())
				.flatMap(new Interceptor<>());
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
	 */
	public static Observable<Long> sendComment(String token, long pid, String text) {
		return service.sendComment(token, pid, text)
				.subscribeOn(Schedulers.single())
				.flatMap(new Interceptor<>());
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
	 * @return
	 */
	public static Observable<Object> setAttention(String token, long pid, boolean attention) {
		return service.setAttention(token, pid, attention ? 1 : 0)
				.subscribeOn(Schedulers.single())
				.flatMap(new Interceptor<>());
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
	 * @return
	 */
	public static Observable<Object> report(String token, long pid, String reason) {
		return service.report(token, pid, reason)
				.subscribeOn(Schedulers.single())
				.flatMap(new Interceptor<>());
	}

	private static class Interceptor<T> implements Function<Request<T>, Observable<T>> {
		@Override
		public Observable<T> apply(Request<T> request) throws Exception {
			if (request.success())
				return Observable.just(request.getData());
			else
				return Observable.error(new PKUHoleException(request.getErrorMsg()));
		}
	}

}
