package org.evergreen.evergreenuaa.exception;


import org.evergreen.evergreenuaa.constant.Constants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class UserNotEnabledException extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create(Constants.PROBLEM_BASE_URI + "/user-not-enabled");

    public UserNotEnabledException() {
        super(
            TYPE,
            "未授权访问",
            Status.UNAUTHORIZED,
            "用户未激活，请联系管理员");
    }
}
