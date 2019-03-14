/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.codegen.generator;

import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.generator.BaseModelGenerator;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;
import io.jboot.app.JbootApplication;
import io.jboot.codegen.CodeGenHelpler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class UIGenerator{

    private String moduleName;//product
    private String modulePackage;//io.jpress.module.product
    
    private String modelPackage;//io.jpress.module.product.model
    
   
    private String basePath;
    private String webPath;
    private String templatesDir="io/jpress/codegen/templates/";
    
    private Kv data;
    private String[] templates= {"ui_listener_template.jf","ui_controller_template.jf","ui_edit_template.jf","ui_list_template.jf"};
    public static final int UI_MODULELISTENER=0;
    public static final int UI_CONTROLLER=1;
    public static final int UI_EDIT=2;
    public static final int UI_LIST=3;
    
    
    
    private String moduleListenerOutputDir;
    private String controllerOutputDir;
    private String htmlOutputDir;
    
    private String targetTemplate;
    private String targetOutputDirFile;
    
    List<TableMeta> tableMetaList;
    
    private Engine engine = Engine.create("forUI");
    public static void main(String[] args)
    {
    	
    }
    public UIGenerator(String moduleName,String modelPackage,List<TableMeta> tableMetaList) {
    	
    	this.tableMetaList=tableMetaList;
    	this.moduleName=moduleName;
    	this.modelPackage=modelPackage;
    	modulePackage=modulePackage=modelPackage.substring(0,modelPackage.lastIndexOf("."));
    	
		basePath=PathKit.getWebRootPath() + "/../module-" + moduleName;
		webPath = basePath + "/module-" + moduleName + "-web";
		
		String upcasedModuleName=toUpperCaseFirstOne(moduleName);
		
		String moduleListenerPakcage=modelPackage.substring(0, modelPackage.lastIndexOf("."));
        String controllerPackage=modelPackage.substring(0, modelPackage.lastIndexOf("."))+".controller";
        
        
        moduleListenerOutputDir = webPath + "/src/main/java/" + moduleListenerPakcage.replace(".", "/");
        controllerOutputDir = webPath + "/src/main/java/" + controllerPackage.replace(".", "/");
   	    htmlOutputDir=webPath+"/src/main/webapp/WEB-INF/views/admin/"+moduleName;
   	 
		data = Kv.by("moduleName", moduleName);//product
		data.set("upcasedModuleName", upcasedModuleName);//Product
	    data.set("modulePackage", modulePackage);//io.jpress.module.product
	    data.set("modelPackage", modelPackage);//io.jpress.module.product.model
	    data.set("moduleListenerPakcage", moduleListenerPakcage);//io.jpress.module.product
        data.set("controllerPackage", controllerPackage);//io.jpress.module.product.controller
        
        engine.setSourceFactory(new ClassPathSourceFactory());
        engine.addSharedMethod(new StrKit());
		
	}
    
    public UIGenerator genListener()
    {
    	   generate(UIGenerator.UI_MODULELISTENER);
    	   return this;
    }
    
    public UIGenerator genControllers()
    {
    	 generate(UIGenerator.UI_CONTROLLER);
    	 return this;
    }
    public UIGenerator genEdit()
    {
    	 generate(UIGenerator.UI_EDIT);
    	 return this;
    }
    
    public UIGenerator genList()
    {
    	 generate(UIGenerator.UI_LIST);
    	 return this;
    }

    public void generate(int genType) {
       
        String targetOutputDir="";
        if(UIGenerator.UI_MODULELISTENER==genType)
        {
    	  targetTemplate=templatesDir+templates[0];
    	  targetOutputDir=moduleListenerOutputDir;
    	  targetOutputDirFile=targetOutputDir + File.separator + toUpperCaseFirstOne(moduleName)+ "ModuleListener" + ".java";	
        }
      
        for (TableMeta tableMeta :tableMetaList) {
            data.set("tableMeta", tableMeta);
            String lowerCaseModelName=toLowerCaseFirstOne(tableMeta.modelName);
            data.set("lowerCaseModelName", lowerCaseModelName);
            
            if(UIGenerator.UI_CONTROLLER==genType)
            {
        	  targetTemplate=templatesDir+templates[1];
        	  targetOutputDir=controllerOutputDir;
        	  targetOutputDirFile= targetOutputDir + File.separator +"_"+tableMeta.modelName + "Controller" + ".java";
            }
            
            if(UIGenerator.UI_EDIT==genType)
            {
        	  targetTemplate=templatesDir+templates[2];
        	  targetOutputDir=htmlOutputDir;
        	  targetOutputDirFile= targetOutputDir + File.separator +tableMeta.name+"_edit.html";
            }
            if(UIGenerator.UI_LIST==genType)
            {
        	  targetTemplate=templatesDir+templates[3];
        	  targetOutputDir=htmlOutputDir;
        	  targetOutputDirFile= targetOutputDir + File.separator +tableMeta.name+"_list.html";
            }
           // tableMeta.columnMetas.get(0).remarks
            String content=engine.getTemplate(targetTemplate).renderToString(data);
           
            //
                   File dir = new File(targetOutputDir);
		            if (!dir.exists()) {
		                dir.mkdirs();
		            }
		            File targetFile = new File(targetOutputDirFile);
		            if (targetFile.exists()) {
		                return;
		            }
		            try {
		   			FileWriter fw = new FileWriter(targetOutputDirFile);
		   			 try {
		   			     fw.write(content);
		   			 } finally {
		   			     fw.close();
		   			 }
		   		} catch (IOException e) {
		   			e.printStackTrace();
		   		}
            //
        }
    }
    
    
    
    
	public  String toLowerCaseFirstOne(String s){
    	  if(Character.isLowerCase(s.charAt(0)))
    	    return s;
    	  else
    	    return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    	}

    	public  String toUpperCaseFirstOne(String s){
    	  if(Character.isUpperCase(s.charAt(0)))
    	    return s;
    	  else
    	    return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    	}


}
