package io.jpress.addon.helloworld;

import io.jpress.core.addon.Addon;
import io.jpress.core.addon.AddonInfo;


public class HelloWorldAddon implements Addon {

    @Override
    public void onInstall(AddonInfo addonInfo) {

        System.out.println("HelloWorldAddon onInstall");

    }

    @Override
    public void onUninstall(AddonInfo addonInfo) {

        System.out.println("HelloWorldAddon onUninstall");
    }

    @Override
    public void onStart() {

        System.out.println("HelloWorldAddon onStart");
    }

    @Override
    public void onStop() {

        System.out.println("HelloWorldAddon onStop");

    }
}
