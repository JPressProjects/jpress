package io.jpress;

import io.jpress.core.Jpress;
import io.jpress.ui.widget.CCPostWidget;
import io.jpress.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;


public class JavaTest {

	public static void main(String[] args) throws Exception {
		
//		String src = "/images/xxx/aaa/basfb.jpg";
//		
//		int inserTo = src.lastIndexOf(".");
//		String xxx = "100x50";
//		String newString = src.substring(0, inserTo)  +"."+xxx + src.substring(inserTo, src.length());
//		
//		System.out.println(newString);
		
//		String water = "/Users/michael/Desktop/wmtest/watermark.png";
//		String src = "/Users/michael/Desktop/wmtest/src02.jpg";
//		String dest = "/Users/michael/Desktop/wmtest/dest02.jpg";
//		
////		ImageUtils.pressImage(water, src, "/Users/michael/Desktop/wmtest/dest021.jpg", 1, 1f);
////		ImageUtils.pressImage(water, src, "/Users/michael/Desktop/wmtest/dest022.jpg", 2, 1f);
////		ImageUtils.pressImage(water, src, "/Users/michael/Desktop/wmtest/dest023.jpg", 3, 1f);
////		ImageUtils.pressImage(water, src, "/Users/michael/Desktop/wmtest/dest024.jpg", 4, 1f);
////		ImageUtils.pressImage(water, src, "/Users/michael/Desktop/wmtest/dest025.jpg", 5, 1f);
//		
//		ImageUtils.pressImage(water, "/Users/michael/Desktop/wmtest/src0303.jpg");
//		
//		ImageUtils.scale(src, 1000, 1000);
//		
//		System.out.println("---->>finished!");
		
//		ApiConfig ac = new ApiConfig();
//		ac.setAppId("wx1615b59ad6af7b21");
//		ac.setAppSecret("f25d720f2c33bc95f855682b2f161a5c");
//		ac.setToken("wewen");
//		
//		ApiConfigKit.setThreadLocalApiConfig(ac);
//		
//		ApiResult ar = MediaApi.batchGetMaterial(MediaType.NEWS, 0, 2);
//		
//		JSONObject json = JSONObject.parseObject(ar.toString());
//		
//		JSONArray ja = json.getJSONArray("item");
//		
//		for (int i = 0; i < ja.size(); i++) {
//			System.out.println(ja.getJSONObject(i).getString("media_id"));
//		}
//		
//		System.out.println(ar.toString());
		
		
		// 按指定模式在字符串查找
//	      String line = "This order was placed for QT3000! OK?${wc name=\"xxxx\"}xsdf";
//	      String pattern = "{wc\b+name=\"*\"";
//
//	      // 创建 Pattern 对象
//	      Pattern r = Pattern.compile(pattern);
//
//	      // 现在创建 matcher 对象
//	      Matcher m = r.matcher(line);
//	      if (m.find( )) {
//	         System.out.println("Found value: " + m.group(0) );
//	         System.out.println("Found value: " + m.group(1) );
//	         System.out.println("Found value: " + m.group(2) );
//	      } else {
//	         System.out.println("NO MATCH");
//	      }
		
//		Pattern p = Pattern.compile("(?<=\\$\\{widgets(\\s)?name=\").*?(?=\"(\\s)?\\})");
//		Matcher m = p.matcher(" <a href=http://www.miibeian.gov.cn target=_blank>${widgets name=\"xxx\"}\r\n${widgets name=\"xxx1\"}${wc name=\"xxx2\"}京ICP证030173号</a>");
////		System.out.println(m.find());
//		while(m.find()){
//		    System.out.println(m.group(0));
//		}
//		
//		String text = JSON.toJSONString(new CCPostWidget());
//		System.out.println("--->>>"+text);
//		
//		CCPostWidget w = JSON.parseObject(text, CCPostWidget.class);
//		System.out.println(w.getConfig().get("aa1"));
		
//		System.out.println(Jpress.currentTemplate().getPath());
		
		String file ="/Users/michael/work/javaee/workspace/jpress/WebRoot/templates/default/index.html";
		
		String text = FileUtils.readString(new File(file));
		
		Pattern p = Pattern.compile("(?<=<@jp_widgets(\\s)?name=\").*?(?=\"(\\s)?(/)?>)");
		System.out.println(text);
		Matcher m = p.matcher(text);
		List<String> list = new ArrayList<String>();
		while(m.find()){
//			list.add(m.group(0));
			System.out.println("---->>>>"+m.group(0));
		}
		System.out.println("---->>>>finished");
		
	}

}
