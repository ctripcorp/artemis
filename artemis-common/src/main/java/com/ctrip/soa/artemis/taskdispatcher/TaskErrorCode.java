package com.ctrip.soa.artemis.taskdispatcher;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

public enum TaskErrorCode {

    RateLimited, RerunnableFail, PermanentFail;

    public static final Set<TaskErrorCode> RERUNNABLE_ERROR_CODES = Collections
            .unmodifiableSet(Sets.newHashSet(TaskErrorCode.RateLimited, TaskErrorCode.RerunnableFail));

}
