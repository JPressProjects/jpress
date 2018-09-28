package io.jpress.module.article.directive;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.article.directive
 */
public class Kits {

    public static String doReplacePageNumber(String url, int pageNumber) {

        int dotIndexOf = url.lastIndexOf(".");
        int splitIndexOf = url.lastIndexOf("-");

        if (dotIndexOf < 0 & splitIndexOf < 0) {
            return url + "-" + pageNumber;
        }

        if (dotIndexOf < 0 && splitIndexOf > 0) {
            return url.substring(0, splitIndexOf) + "-" + pageNumber;
        }

        if (dotIndexOf > 0 && splitIndexOf < 0) {
            return url.substring(0, dotIndexOf) + "-" + pageNumber + url.substring(dotIndexOf);
        }

        //if (dotIndexOf > 0 && spitIndexOf >0){
        return url.substring(0, splitIndexOf) + "-" + pageNumber + url.substring(dotIndexOf);
    }

    public static void main(String[] args) {

        System.out.println(doReplacePageNumber("/aa/bb/cc", 123));
        System.out.println(doReplacePageNumber("/aa/bb/cc.html", 123));
        System.out.println(doReplacePageNumber("/aa/bb/cc-33-44.html", 123));
        System.out.println(doReplacePageNumber("/aa/bb/cc-333.html", 123));
        System.out.println(doReplacePageNumber("/aa/bb/cc-1.html", 123));
        System.out.println(doReplacePageNumber("/aa/bb/cc-31", 123));
        System.out.println(doReplacePageNumber("/aa/bb/cc-", 123));

    }



}
