package io.jpress.front.controller;

import com.jfinal.upload.UploadFile;

import io.jpress.core.JBaseController;
import io.jpress.router.RouterMapping;

@RouterMapping(url="/test")
public class TestController extends JBaseController {
	
	public void index(){
		
		if(isMultipartRequest()){
			
			UploadFile file  = getFile();
			
			
			System.out.println("---->>>"+file.getFile().getAbsolutePath());
			
		}
		
		renderText("test jikexueyuan ...");
		
	}

}
