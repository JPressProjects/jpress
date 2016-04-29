package io.jpress.wechat.processer;

import com.jfinal.weixin.sdk.msg.in.InMsg;
import com.jfinal.weixin.sdk.msg.out.OutMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;

import io.jpress.wechat.IMessageProcesser;
import io.jpress.wechat.Replay;

@Replay(key = "simple")
public class SimpleProcesser implements IMessageProcesser {

	@Override
	public OutMsg process(InMsg message) {
		OutTextMsg out = new OutTextMsg(message);
		out.setContent("SimpleProcesser...");
		return out;
	}

	@Override
	public void onConfig(String configInfo) {
		
		
	}

}
