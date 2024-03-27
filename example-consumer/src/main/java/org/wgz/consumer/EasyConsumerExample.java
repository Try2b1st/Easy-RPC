package org.wgz.consumer;

import org.wgz.common.model.User;
import org.wgz.common.service.UserService;
import org.wgz.proxy.ServiceProxyFactory;

public class EasyConsumerExample {
    public static void main(String[] args) {

        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("hello world");
        User newUser = userService.getUser(user);
        if(newUser == null){
            System.out.println("newUser is null");
        }else{
            System.out.println(newUser.getName());
        }
    }
}
