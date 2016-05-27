package io.jpress.utils;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupUtils {

	public static String getFirstImageSrc(String html) {
		if (html == null)
			return null;

		Elements es = Jsoup.parseBodyFragment(html).select("img");
		if (es != null && es.size() > 0)
			return es.first().absUrl("src");

		return null;
	}

	public static List<String> getImageSrcs(String html) {
		if (!StringUtils.isNotBlank(html)) {
			return null;
		}

		List<String> list = new ArrayList<String>();

		Document doc = Jsoup.parseBodyFragment(html);
		Elements es = doc.select("img");
		if (es != null && es.size() > 0) {
			for (Element e : es) {
				list.add(e.attr("src"));
			}
		}
		return list;
	}

}
