package io.jpress;

import io.jboot.config.annotation.PropertyConfig;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress
 */
@PropertyConfig(prefix = "io.jpress")
public class JPressConfig {

    private boolean useRPC = false;


    public boolean isUseRPC() {
        return useRPC;
    }

    public void setUseRPC(boolean useRPC) {
        this.useRPC = useRPC;
    }
}
