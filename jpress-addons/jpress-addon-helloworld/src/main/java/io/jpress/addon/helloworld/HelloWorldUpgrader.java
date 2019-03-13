package io.jpress.addon.helloworld;

import io.jpress.core.addon.AddonInfo;
import io.jpress.core.addon.AddonUpgrader;


public class HelloWorldUpgrader implements AddonUpgrader {
    @Override
    public void onUpgrade(AddonInfo oldAddon, AddonInfo thisAddon) {
        System.out.println("HelloWorldUpgrader.onUpgrade()");
    }
}
