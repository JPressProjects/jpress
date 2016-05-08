package io.jpress.router;

import java.math.BigInteger;
import java.util.HashMap;

public class RouterParams extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	public String slug() {
		Object slug = get("slug");
		return slug == null ? null : slug.toString();
	}

	public RouterParams slug(String slug) {
		put("slug", slug);
		return this;
	}

	public BigInteger id() {
		Object id = get("id");
		return id == null ? null : new BigInteger(id.toString());
	}

	public RouterParams id(BigInteger id) {
		put("id", id);
		return this;
	}

	public int pageNumber() {
		Object pageNumber = get("pageNumber");
		return pageNumber == null ? 0 : Integer.parseInt(pageNumber.toString());
	}

	public int pageNumberWithDefault(int defaultValue) {
		Object pageNumber = get("pageNumber");
		return pageNumber == null ? defaultValue : Integer.parseInt(pageNumber.toString());
	}

	public RouterParams pageNumber(int pageNumber) {
		put("pageNumber", pageNumber);
		return this;
	}

	public RouterParams pageNumber(String pageNumber) {
		put("pageNumber", pageNumber);
		return this;
	}

	public int pageSize() {
		Object pageSize = get("pageSize");
		return pageSize == null ? 0 : Integer.parseInt(pageSize.toString());
	}

	public int pageSizeWithDefault(int defaultValue) {
		Object pageSize = get("pageSize");
		return pageSize == null ? defaultValue : Integer.parseInt(pageSize.toString());
	}

	public RouterParams pageSize(int pageSize) {
		put("pageSize", pageSize);
		return this;
	}

	public RouterParams pageSize(String pageSize) {
		put("pageSize", pageSize);
		return this;
	}

}
