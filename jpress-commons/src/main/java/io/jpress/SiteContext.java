package io.jpress;

public class SiteContext {

    private static ThreadLocal<Long> TL = new ThreadLocal<>();

    public static void setSiteId(Long siteId){
        TL.set(siteId);
    }

    public static Long getSiteId(){
        Long siteId = TL.get();
        return siteId == null ? 0L : siteId;
    }

    public static void removeSiteId(){
        TL.remove();
    }
}
