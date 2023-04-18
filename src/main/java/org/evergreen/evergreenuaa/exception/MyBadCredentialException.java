package org.evergreen.evergreenuaa.exception;


import org.evergreen.evergreenuaa.constant.Constants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class MyBadCredentialException extends AbstractThrowableProblem {

    private static final URI TYPE = URI.create(Constants.PROBLEM_BASE_URI + "/bad-credentials");

    public MyBadCredentialException() {
        super(
            TYPE,
            "认证失败",
            Status.UNAUTHORIZED,
            "用户名或密码错误");
    }
}
