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

	public static final String errorMsg = "���粻����";

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
	 * ��ȡ�ض�ҳ����
	 * 
	 * @param page
	 *            ҳ��
	 * @return �����б�
	 */
	public static Observable<Topic[]> getTopics(int page) {
		return service.getTopics(page)
				.subscribeOn(Schedulers.single())
				.flatMap(new Interceptor<>());
	}

	/**
	 * �õ���������
	 * 
	 * @param pid
	 *            ������
	 * @return ��������
	 */
	public static Observable<Topic> getSingleTopic(long pid) {
		return service.getSingleTopic(pid)
				.subscribeOn(Schedulers.single())
				.flatMap(new Interceptor<>());
	}

	/**
	 * ��ȡ��������
	 * 
	 * @param pid
	 *            ������
	 * @return �����б�
	 */
	public static Observable<Comment[]> getComments(long pid) {
		return service.getComments(pid)
				.subscribeOn(Schedulers.single())
				.flatMap(new Interceptor<>());
	}

	/**
	 * �����ض��ؼ��ֵ�����
	 * 
	 * @param keywords
	 *            �ؼ���
	 * @param pageSize
	 *            �������
	 * @return �����б�
	 */
	public static Observable<Topic[]> searchTopics(String keywords, int pageSize) {
		return service.searchTopics(keywords, pageSize)
				.subscribeOn(Schedulers.single())
				.flatMap(new Interceptor<>());
	}

	// The following APIs are user-specified.

	/**
	 * ��¼
	 * 
	 * @param uid
	 *            ѧ��
	 * @param password
	 *            ����
	 * @return �û���Ϣ
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
	 * ��ȡ��ע����
	 * 
	 * @param token
	 *            token
	 * @return ��ע�����б�
	 */
	public static Observable<Topic[]> getAttentionTopics(String token) {
		return service.getAttentionTopics(token)
				.subscribeOn(Schedulers.single())
				.flatMap(new Interceptor<>());
	}

	/**
	 * ������������
	 * 
	 * @param token
	 *            token
	 * @param text
	 *            ��������
	 * @return �ɹ������������ţ�ʧ�ܣ��׳��쳣
	 */
	public static Observable<Long> sendTextPost(String token, String text) {
		return service.sendPost(token, "text", text, null)
				.subscribeOn(Schedulers.single())
				.flatMap(new Interceptor<>());
	}

	/**
	 * ����ͼƬ����
	 * 
	 * @param token
	 *            token
	 * @param text
	 *            ��������
	 * @param image
	 *            ͼƬ����
	 * @return
	 */
	public static Observable<Long> sendImagePost(String token, String text, String image) {
		return service.sendPost(token, "image", text, image)
				.subscribeOn(Schedulers.single())
				.flatMap(new Interceptor<>());
	}

	/**
	 * ��������
	 * 
	 * @param token
	 *            token
	 * @param pid
	 *            ������
	 * @param text
	 *            ��������
	 * @return �ɹ������������ţ�ʧ�ܣ��׳��쳣
	 */
	public static Observable<Long> sendComment(String token, long pid, String text) {
		return service.sendComment(token, pid, text)
				.subscribeOn(Schedulers.single())
				.flatMap(new Interceptor<>());
	}

	/**
	 * ����������ע
	 * 
	 * @param token
	 *            token
	 * @param pid
	 *            ������
	 * @param attention
	 *            �Ƿ��ע
	 * @return
	 */
	public static Observable<Object> setAttention(String token, long pid, boolean attention) {
		return service.setAttention(token, pid, attention ? 1 : 0)
				.subscribeOn(Schedulers.single())
				.flatMap(new Interceptor<>());
	}

	/**
	 * �ٱ�����
	 * 
	 * @param token
	 *            token
	 * @param pid
	 *            ������
	 * @param reason
	 *            �ٱ�����
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
