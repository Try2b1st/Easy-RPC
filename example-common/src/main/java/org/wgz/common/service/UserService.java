package org.wgz.common.service;

import org.wgz.common.model.User;

public interface UserService {
    User getUser(User user);

    /**
     * 测试Mock是否生效 （返回值维int类型，生效返回默认值为1）
     *
     * @return
     */
    default int getNumber() {
        return 0;
    }
}
