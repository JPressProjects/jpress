package io.jpress.wechat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.weixin.sdk.msg.in.InMsg;
import com.jfinal.weixin.sdk.msg.out.OutMsg;

import io.jpress.core.ClassScaner;
import io.jpress.utils.StringUtils;

public class ProcesserInvoker {

	private static Map<String, Class<? extends IMessageProcesser>> map = new HashMap<String, Class<? extends IMessageProcesser>>();
	private static boolean isInited = false;

	public static OutMsg invoke(String replyContent, InMsg message) {
		doInit();
		IMessageProcesser processer = getProcesser(replyContent);
		return processer == null ? null : processer.process(message);
	}

	private static void doInit() {
		if (isInited)
			return;

		List<Class<IMessageProcesser>> clist = ClassScaner.scanSubClass(IMessageProcesser.class, true);
		if (clist != null && clist.size() > 0) {
			for (Class<? extends IMessageProcesser> clazz : clist) {
				Replay replay = clazz.getAnnotation(Replay.class);
				if (null != replay && StringUtils.isNotBlank(replay.key())) {
					map.put(replay.key(), clazz);
				}
			}
		}

		isInited = true;
	}

	private static IMessageProcesser getProcesser(String replyContent) {

		String config = replyContent.substring(replyContent.indexOf("]") + 1);
		String key = replyContent.substring(1, replyContent.indexOf("]"));
		Class<? extends IMessageProcesser> clazz = map.get(key);
		try {
			IMessageProcesser processer = clazz.newInstance();
			processer.onConfig(config);
			return processer;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
