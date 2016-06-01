package io.jpress.wechat.processer;

import com.jfinal.weixin.sdk.msg.in.InMsg;
import com.jfinal.weixin.sdk.msg.out.OutMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;

import io.jpress.wechat.IMessageProcesser;
import io.jpress.wechat.MessageProcesser;

@MessageProcesser(key = "hello")
public class HelloProcesser implements IMessageProcesser {

	@Override
	public void onInit(String configInfo) {
		
		
	}
	
	@Override
	public OutMsg process(InMsg message) {
		OutTextMsg out = new OutTextMsg(message);
		out.setContent("hello...");
		return out;
	}

	

}
