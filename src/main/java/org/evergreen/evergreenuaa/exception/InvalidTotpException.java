package org.evergreen.evergreenuaa.exception;


import org.evergreen.evergreenuaa.constant.Constants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class InvalidTotpException extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create(Constants.PROBLEM_BASE_URI + "/invalid-token");

    public InvalidTotpException() {
        super(
            TYPE,
            "验证码错误",
            Status.UNAUTHORIZED,
            "验证码不正确或已过期，请重新输入");
    }
}
