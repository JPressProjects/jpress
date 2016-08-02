package io.jpress.template;

public class TplMetadata {

	private String name;
	private String text;
	private String placeholder;
	private String dataType = DATA_TYPE_INPUT;

	public static String DATA_TYPE_INPUT = "input";
	public static String DATA_TYPE_SELECT = "select";
	public static String DATA_TYPE_CHECKBOX = "checkbox";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

}
