package com.lyl.pkuhole.model;

import java.util.ArrayList;
import java.util.List;

import com.lyl.pkuhole.PKUHole;
import com.lyl.pkuhole.network.Network;

import io.reactivex.Observable;

/**
 * ����ǰ�û����й�ע�������б�
 * 
 * �������ڹ�עֻ������API����֪��ֻ��������:
 * 1.��ȡ�û����й�ע��������/services/pkuhole/api.php?action=getattention token=*
 * 2.��ĳ���������й�ע/ȡ�أ�/services/pkuhole/api.php?action=attention
 * token=*&pid=*&switch=1/0
 * (���е���������getcomment��ʱ��token����õ�json����attention�ֶΣ�������̫�ߣ�Ϊ�˻�ȡ��ע�����ȡ���������б�������)
 * 
 * Ϊ�˵�֪һ���ض��������Ƿ񱻹�ע����Ҫ�ڱ���ά��һ����ע�������б��û���¼ʱ��ʼ����������ע��ȡ��ʱ���ж�̬ά����
 */
public class AttentionManager {

	/**
	 * ��pid�Ӵ�С����
	 */
	public static List<Topic> topicList;

	public static Observable<Topic[]> getAttentionList() {
		User user = PKUHole.getInstance().user;
		if (user == null) {
			// This could never happen.
			return null;
		}
		String token = user.token;
		return Network.getAttentionTopics(token)
				.doOnNext(topics -> {
					topicList = new ArrayList<Topic>();
					for (Topic topic : topics)
						topicList.add(topic);
				});
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
		// �ظ�Ԫ��
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
