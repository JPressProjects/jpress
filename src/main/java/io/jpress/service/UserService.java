package io.jpress.service;

import com.jfinal.kit.Ret;

/**
 * Created by michael on 2017/8/10.
 */
public interface UserService {

    public Ret login(String username, String password);
}
