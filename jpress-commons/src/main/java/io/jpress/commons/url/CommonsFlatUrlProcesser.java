package io.jpress.commons.url;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class CommonsFlatUrlProcesser implements FlatUrlProcesser {

    private final Map<String, String> prefixAndTargets;

    public CommonsFlatUrlProcesser(Map<String, String> prefixAndTargets) {
        this.prefixAndTargets = prefixAndTargets;
    }

    @Override
    public String flat(String target, HttpServletRequest request) {
        for (Map.Entry<String, String> entry : prefixAndTargets.entrySet()) {
            if (target.startsWith(entry.getKey())) {
                return entry.getValue() + target.substring(entry.getKey().length());
            }
        }
        return target;
    }
}
