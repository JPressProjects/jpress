package io.jpress.wechat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.msg.in.InMsg;
import com.jfinal.weixin.sdk.msg.out.OutMsg;

import io.jpress.core.ClassScaner;
import io.jpress.utils.StringUtils;

public class ProcesserInvoker {
	private static final Log log = Log.getLog(ProcesserInvoker.class);
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
				Reply reply = clazz.getAnnotation(Reply.class);
				if (null != reply && StringUtils.isNotBlank(reply.key())) {
					map.put("[" + reply.key() + "]", clazz);
				}
			}
		}

		isInited = true;
	}

	private static IMessageProcesser getProcesser(String replyContent) {

		String key = replyContent.substring(0, replyContent.indexOf("]") + 1);
		String config = replyContent.substring(replyContent.indexOf("]") + 1);

		Class<? extends IMessageProcesser> clazz = map.get(key);
		try {
			IMessageProcesser processer = clazz.newInstance();
			processer.onConfig(config);
			return processer;
		} catch (Exception e) {
			log.warn("wechat ProcesserInvoker getProcesser error", e);
		}
		return null;
	}

}
