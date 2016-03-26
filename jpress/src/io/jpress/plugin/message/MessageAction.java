package io.jpress.plugin.message;

import java.util.ArrayList;
import java.util.List;

public class MessageAction {
	
	private List<String> actions = new ArrayList<String>();
	
	public void register(String action){
		actions.add(action);
	}
	
	public List<String> getActions(){
		return actions;
	}

}
