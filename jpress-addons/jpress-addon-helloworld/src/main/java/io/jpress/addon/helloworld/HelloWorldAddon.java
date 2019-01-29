package io.jpress.addon.helloworld;

import io.jpress.core.addon.Addon;


public class HelloWorldAddon implements Addon {

    @Override
    public void onInstall() {

        System.out.println("HelloWorldAddon onInstall");

    }

    @Override
    public void onUninstall() {

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
