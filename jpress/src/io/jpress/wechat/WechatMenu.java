package io.jpress.wechat;

import com.alibaba.fastjson.JSONObject;

public class WechatMenu {
	
	
	
	
	

	
	
	public static JSONObject toJSONObject(WechatMenuItem item){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name", item.getName());
		jsonObject.put("type", item.getType());
		
		if("view".equals(item.getType())){
			jsonObject.put("url", item.getKey());
		}else{
			jsonObject.put("key", item.getKey());
		}
		return jsonObject;
	}

}
