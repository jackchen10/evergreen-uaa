package org.evergreen.evergreenuaa.exception;


import org.evergreen.evergreenuaa.constant.Constants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class UserAccountLockedException extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create(Constants.PROBLEM_BASE_URI + "/user-locked");

    public UserAccountLockedException() {
        super(
            TYPE,
            "未授权访问",
            Status.UNAUTHORIZED,
            "用户账户已锁定，请联系管理员");
    }
}
