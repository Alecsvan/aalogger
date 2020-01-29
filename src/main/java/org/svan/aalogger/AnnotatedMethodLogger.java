package org.svan.aalogger;

import org.aspectj.lang.ProceedingJoinPoint;

public interface AnnotatedMethodLogger {
    Object logBeforeAndAfterCall(ProceedingJoinPoint pjp) throws Throwable;
}
