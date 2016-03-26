package io.jpress.template;

import io.jpress.template.Module.TaxonomyType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.jfinal.kit.PathKit;

public class ConfigParser extends DefaultHandler {

	final Template template;
	
	private String cName;
	private Module cModule;
	private List<Module> modules;
	private List<TaxonomyType> cTaxonomys;
	private List<Thumbnail> thumbnails;
	
	public ConfigParser() {
		template = new Template();
		modules = new ArrayList<Module>();
		thumbnails = new ArrayList<Thumbnail>();
	}
	
	
	public Template parser(String tName){
		String path = PathKit.getWebRootPath()+"/templates/"+tName;
		File configFile = getConfigFile(new File(path));
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(configFile,this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		template.setPath(configFile.getParent());
		return template;
	}
	
	private File getConfigFile(File file) {
		if (file.isDirectory()) {
			File configFile = new File(file, "config.xml");
			
			if (configFile.exists() && configFile.isFile()) {
				return configFile;
			} else {
				File[] files = file.listFiles();
				if (null != files && files.length > 0) {
					for (File f : files) {
						if (f.isDirectory()){
							File cf = getConfigFile(f);
							if(cf != null){
								return cf;
							}
						}
							
					}
				}
			}
		}
		return null;
	}
	
	
	@Override
	public void endDocument() throws SAXException {
		template.setModules(modules);
		template.setThumbnails(thumbnails);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
		this.cName = qName;
		
		
		if("module".equalsIgnoreCase(qName)){
			cModule = new Module();
			cModule.setAddTitle(attrs.getValue("add"));
			cModule.setName(attrs.getValue("name"));
			cModule.setListTitle(attrs.getValue("list"));
			cModule.setTitle(attrs.getValue("title"));
			cModule.setCommentTitle(attrs.getValue("comment"));
			
			cTaxonomys = new ArrayList<Module.TaxonomyType>();
		}
		
		
		if("taxonomy".equalsIgnoreCase(qName)){
			TaxonomyType  tt = new TaxonomyType(cModule);
			
			tt.setName(attrs.getValue("name"));
			tt.setTitle(attrs.getValue("title"));
			tt.setFormType(attrs.getValue("formType"));
			
			cTaxonomys.add(tt);
		}
		
		
		if("thumbnail".equalsIgnoreCase(qName)){
			Thumbnail tb = new Thumbnail();
			tb.setName(attrs.getValue("name"));
			tb.setSize(attrs.getValue("size"));
			thumbnails.add(tb);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		if("module".equalsIgnoreCase(qName)){
			cModule.setTaxonomyTypes(cTaxonomys);
			modules.add(cModule);
		}
		
	}

	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		
		String value = new String(ch, start, length);
		
		if ("title".equalsIgnoreCase(cName)) {
			template.setTitle(value);
		} else if ("description".equalsIgnoreCase(cName)) {
			template.setDescription(value);
		} else if ("author".equalsIgnoreCase(cName)) {
			template.setAuthor(value);
		} else if ("authorWebsite".equalsIgnoreCase(cName)) {
			template.setAuthorWebsite(value);
		} else if ("version".equalsIgnoreCase(cName)) {
			template.setVersion(value);
		} else if ("versionCode".equalsIgnoreCase(cName)) {
			int versionCode = 0;
			try {
				versionCode = Integer.parseInt(value.trim());
			} catch (Exception e) {}
			template.setVersionCode(versionCode);
		} else if ("updateUrl".equalsIgnoreCase(cName)) {
			template.setUpdateUrl(value);
		}
		
	}
	
	
	
	
}
