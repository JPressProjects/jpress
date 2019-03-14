package io.jpress.addon.helloworld;

import io.jpress.core.addon.AddonInfo;
import io.jpress.core.addon.AddonUpgrader;


public class HelloWorldUpgrader implements AddonUpgrader {
    @Override
    public boolean onUpgrade(AddonInfo oldAddon, AddonInfo thisAddon) {
        System.out.println("HelloWorldUpgrader.onUpgrade()");
        return true;
    }

    @Override
    public void onRollback(AddonInfo oldAddon, AddonInfo thisAddon) {
        System.out.println("HelloWorldUpgrader.onRollback()");
    }
}
