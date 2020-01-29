package org.svan.aalogger;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PlainLoggingToErrorLog implements PlainLog {

    @Override
    public Object logBeforeAndAfterCall(ProceedingJoinPoint pjp) throws Throwable {
        logBefore(pjp);
        Object retVal = null;
        Throwable thrownException = null;
        try {
            retVal = pjp.proceed();
        } catch (Throwable ex) {
            thrownException = ex;
            throw ex;
        } finally {
            logAfter(pjp, thrownException == null ? retVal : thrownException);
        }
        return retVal;
    }

    void logBefore(ProceedingJoinPoint pjp) {
        System.out.println(String.format("Method %s entry", ((MethodSignature) pjp.getSignature()).getMethod().getName()));
        log.error("Method {} - {} entry", ((MethodSignature) pjp.getSignature()).getDeclaringTypeName(),  ((MethodSignature) pjp.getSignature()).getMethod().getName());
    }

    void logAfter(ProceedingJoinPoint pjp, Object result) {
        log.error("Method {} exit", ((MethodSignature) pjp.getSignature()).getMethod().getName());
        log.error("Method returned: " + result);
    }
}

