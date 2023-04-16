package org.evergreen.evergreenuaa.exception;

import org.evergreen.evergreenuaa.constant.Constants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class DuplicateProblemException extends AbstractThrowableProblem {

    private static final URI TYPE = URI.create(Constants.PROBLEM_BASE_URI + "/duplicate");

    public DuplicateProblemException(String message) {
        super(TYPE, "发现重复数据", Status.CONFLICT, message);
    }

}
