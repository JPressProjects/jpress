package io.jpress.core.ui;

import io.jpress.core.ui.JWidget.WidgetStorage;
import io.jpress.model.Option;

import java.util.LinkedList;
import java.util.List;

import com.alibaba.fastjson.JSON;

public class JWidgetContainer extends JTag{

	LinkedList<JWidget> widgetList;
	
	@Override
	public void onRender() {

		initWidgetList();
		
		StringBuilder htmlBuilder = new StringBuilder();
		if(widgetList!=null){
			for(JWidget widget : widgetList){
				htmlBuilder.append(widget.getFrontEndHtml());
			}
		}
		
		renderText(htmlBuilder.toString());
	}
	
	private void initWidgetList(){
		String name = getParam("name");
		String value  = Option.cacheValue("widget_"+name);
		if(value == null){
			List<WidgetStorage> wsList = JSON.parseArray(value, WidgetStorage.class);
			if(wsList != null && wsList.size() > 0){
				widgetList = new LinkedList<JWidget>();
				for(WidgetStorage ws : wsList){
					widgetList.add(ws.getWidget());
				}
			}
		}
	}
	
}
