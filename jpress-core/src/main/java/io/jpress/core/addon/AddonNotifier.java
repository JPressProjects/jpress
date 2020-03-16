package io.jpress.core.addon;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/1
 */
public interface AddonNotifier {


    public void notifyAddonInstall(String path) ;


    public void notifyAddonStarted(String addonId) ;


    public void notifyAddonStoped(String addonId) ;


    public void notifyAddonUninstall(String addonId);
}
