package io.jpress.addon.helloworld;

import io.jpress.core.addon.Addon;


public class HelloWorldAddon implements Addon {

    @Override
    public void onInstall() {

        System.out.println("onInstall");

    }

    @Override
    public void onUninstall() {

        System.out.println("onUninstall");
    }

    @Override
    public void onStart() {

        System.out.println("onStart");
    }

    @Override
    public void onStarted() {

        System.out.println("onStarted");

    }

    @Override
    public void onStop() {

        System.out.println("onStop");

    }
}
