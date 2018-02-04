package com.lyl.pkuhole.model;

import java.util.ArrayList;
import java.util.List;

import com.lyl.pkuhole.PKUHole;
import com.lyl.pkuhole.PKUHoleAPI;
import com.lyl.pkuhole.exception.PKUHoleException;
import com.lyl.pkuhole.utils.UIUtils;

/**
 * 管理当前用户所有关注的树洞列表。
 * 
 * 树洞关于关注只有两个API（已知的只有两个）:
 * 1.获取用户所有关注的树洞：/services/pkuhole/api.php?action=getattention token=*
 * 2.将某个树洞进行关注/取关：/services/pkuhole/api.php?action=attention
 * token=*&pid=*&switch=1/0
 * (还有第三个是在getcomment的时候传token，获得的json里有attention字段，但代价太高，为了获取关注情况获取整个评论列表，不采用)
 * 
 * 为了得知一个特定的树洞是否被关注，需要在本地维护一个关注的树洞列表，用户登录时初始化，后续关注或取关时进行动态维护。
 */
public class AttentionManager {

	/**
	 * 按pid从大到小有序
	 */
	public static List<Topic> topicList;

	public static void getAttentionList() {
		User user = PKUHole.getInstance().user;
		if (user == null) {
			// This could never happen.
			return;
		}
		String token = user.token;
		Topic[] topics;
		try {
			topics = PKUHoleAPI.getAttentionTopics(token);
		} catch (PKUHoleException e) {
			UIUtils.messageBox("获取关注列表失败！原因：" + e.getMessage() + "\n请重新打开本客户端。");
			System.exit(0);
			return;
		}
		topicList = new ArrayList<Topic>();
		for (Topic topic : topics)
			topicList.add(topic);
	}

	public static void clearAttentionList() {
		topicList = null;
	}

	public static void addAttentionTopic(Topic topic) {
		if (topicList == null)
			return;
		int l = 0, r = topicList.size() - 1;
		while (r > l) {
			int m = (l + r) / 2;
			if (topic.pid < topicList.get(m).pid)
				l = m + 1;
			else
				r = m;
		}
		// 重复元素
		if (l < topicList.size() && topicList.get(l).pid == topic.pid)
			return;
		topicList.add(l, topic);
	}

	public static void removeAttentionTopic(int pid) {
		if (topicList == null)
			return;
		int l = topicList.size();
		for (int i = 0; i < l; i++)
			if (topicList.get(i).pid == pid) {
				topicList.remove(i);
				return;
			}
	}

	public static boolean isAttention(int pid) {
		if (topicList == null)
			return false;
		int l = topicList.size();
		for (int i = 0; i < l; i++)
			if (topicList.get(i).pid == pid)
				return true;
		return false;
	}

}
