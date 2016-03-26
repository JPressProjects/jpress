package io.jpress.template;


public class Thumbnail {
	
	private String name;
	private String size;
	private int width;
	private int height;
	
	public Thumbnail() {
	
	}
	
	public Thumbnail(String name,String size) {
		this.name = name;
		this.size = size;
		
		if(size != null && size.contains("x")){
			String[] ss = size.split("x");
			if(2 == ss.length){
				this.width = Integer.parseInt(ss[0].trim());
				this.height = Integer.parseInt(ss[1].trim());
			}
		}
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
		
		if(size != null && size.contains("x")){
			String[] ss = size.split("x");
			if(2 == ss.length){
				this.width = Integer.parseInt(ss[0].trim());
				this.height = Integer.parseInt(ss[1].trim());
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public String getSizeAsString(){
		return width+"x"+height;
	}

}
