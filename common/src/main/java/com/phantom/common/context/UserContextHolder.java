package com.phantom.common.context;

import com.phantom.common.bean.dto.UserBaseInfoDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserContextHolder {
    private static final InheritableThreadLocal<UserBaseInfoDTO> userContext = new InheritableThreadLocal<>();

    public static void setUser(UserBaseInfoDTO user) {
        userContext.set(user);
        log.debug("User context set for user: {}", user.getUsername());
    }

    public static UserBaseInfoDTO getUser() {
        UserBaseInfoDTO user = userContext.get();
        if (user == null) {
            log.warn("No user context found in current thread");
        }
        return user;
    }

    public static void clear() {
        userContext.remove();
        log.debug("User context cleared");
    }

    public static Integer getUserId() {
        UserBaseInfoDTO user = getUser();
        return user != null ? user.getId() : null;
    }

    public static String getUsername() {
        UserBaseInfoDTO user = getUser();
        return user != null ? user.getUsername() : null;
    }
}
