package org.wgz.provider;

import org.wgz.common.model.User;
import org.wgz.common.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println(user.getName());
        return user;
    }
}
