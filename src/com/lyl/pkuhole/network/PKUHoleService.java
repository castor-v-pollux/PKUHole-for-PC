package com.lyl.pkuhole.network;

import com.lyl.pkuhole.model.Comment;
import com.lyl.pkuhole.model.Topic;
import com.lyl.pkuhole.model.User;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PKUHoleService {
	
	@GET("services/pkuhole/api.php?action=getlist")
	Observable<Request<Topic[]>> getTopics(
			@Query("p") int page
	);
	
	@GET("services/pkuhole/api.php?action=getone")
	Observable<Request<Topic>> getSingleTopic(
			@Query("pid") long pid
	);
	
	@GET("/services/pkuhole/api.php?action=getcomment")
	Observable<Request<Comment[]>> getComments(
			@Query("pid") long pid			
	);
	
	@FormUrlEncoded
	@POST("/services/pkuhole/api.php?action=search")
	Observable<Request<Topic[]>> searchTopics(
			@Field("keywords") String keywords,
			@Field("pagesize") int pageSize
	);
	
	@FormUrlEncoded
	@POST("/services/login/login.php?platform=PC")
	Observable<User.Inner> login(
			@Field("uid") String uid,
			@Field("password") String password
	);
	
	@FormUrlEncoded
	@POST("/services/pkuhole/api.php?action=getattention")
	Observable<Request<Topic[]>> getAttentionTopics(
			@Field("token") String token
	);
	
	@FormUrlEncoded
	@POST("/services/pkuhole/api.php?action=attention")
	Observable<Request<Object>> setAttention(
			@Field("token") String token,
			@Field("pid") long pid,
			@Field("switch") int attention
	);
	
	@FormUrlEncoded
	@POST("/services/pkuhole/api.php?action=docomment")
	Observable<Request<Long>> sendComment(
			@Field("token") String token,
			@Field("pid") long pid,
			@Field("text") String text
	);
	
	@FormUrlEncoded
	@POST("/services/pkuhole/api.php?action=report")
	Observable<Request<Object>> report(
			@Field("token") String token,
			@Field("pid") long pid,
			@Field("reason") String reason
	);
	
	@FormUrlEncoded
	@POST("/services/pkuhole/api.php?action=dopost")
	Observable<Request<Long>> sendPost(
			@Field("token") String token,
			@Field("type") String type,
			@Field("text") String text,
			@Field("data") String data
	);

}
