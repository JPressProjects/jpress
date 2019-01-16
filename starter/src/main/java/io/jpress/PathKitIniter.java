package io.jpress;

import com.jfinal.kit.PathKit;
import io.jboot.core.listener.JbootAppListenerBase;

import java.net.URISyntaxException;


public class PathKitIniter extends JbootAppListenerBase {

    @Override
    public void onInit() {
        try {
            PathKit.setWebRootPath(PathKitIniter.class.getResource("/").toURI().getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
