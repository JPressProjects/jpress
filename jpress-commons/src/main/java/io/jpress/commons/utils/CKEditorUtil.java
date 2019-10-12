package io.jpress.commons.utils;


import io.jboot.utils.StrUtil;
import org.apache.commons.lang3.StringUtils;

public class CKEditorUtil {

    private static final String[] htmlChars = {"&lt;", "&gt;"};
    private static final String[] escapeChars = {"&amp;lt;", "&amp;gt;"};

    public static String CKEditorContent(String originalContent) {

        //ckeditor 编辑器有个bug，自动把 &lt; 转化为 < 和 把 &gt; 转化为 >
        //因此，此处需要 把 "&lt;" 替换为 "&amp;lt;" 和 把 "&gt;" 替换为 "&amp;gt;"
        //方案：http://komlenic.com/246/encoding-entities-to-work-with-ckeditor-3/

        return StrUtil.isBlank(originalContent)
                ? originalContent
                : StringUtils.replaceEach(originalContent, htmlChars, escapeChars);
    }

}
