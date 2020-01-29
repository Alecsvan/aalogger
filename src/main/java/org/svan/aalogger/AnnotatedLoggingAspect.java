package org.svan.aalogger;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.List;

@Slf4j
@Aspect
@Component
public class AnnotatedLoggingAspect implements InitializingBean {
    final static String DEFAULT_LOGGER = PlainLog.class.getSimpleName();

    @Getter
    private List<AnnotatedMethodLogger> loggersList;

    @Autowired
    public void setLoggersList(List<AnnotatedMethodLogger> loggersList) {
        this.loggersList = loggersList;
    }

    @Override
    public void afterPropertiesSet() {
        log.error("Loggers::");
        loggersList.forEach(logger -> log.error(logger.getClass().getSimpleName()));
    }

    @Around("@annotation(LogThisMethodCall)")
    public Object logJobHistory(ProceedingJoinPoint pjp) throws Throwable {
        if (loggersList == null) {
            throw new RuntimeException(
                "No loggers provided for logging aspect");
        }
        Class<? extends AnnotatedMethodLogger> loggerInterface =
            getRequestedLoggerClassFromAnnotationParameter(pjp);
        AnnotatedMethodLogger logger = getLogger(loggerInterface);

        if (logger == null)
            throw new RuntimeException(
                "Cannot find requested AnnotatedMethodLogger implementation: "
                    + loggerInterface.getSimpleName());

        return logger.logBeforeAndAfterCall(pjp);
    }

    private AnnotatedMethodLogger getLogger(Class<?
        extends AnnotatedMethodLogger> loggerInterface) {
        for (AnnotatedMethodLogger obj : loggersList)
            if (loggerInterface.isInstance(obj))
                return obj;
        return null;
    }

    private Class<? extends AnnotatedMethodLogger>
                getRequestedLoggerClassFromAnnotationParameter(ProceedingJoinPoint pjp) {
        //expecting only one annotation of this type
        Annotation[] annotation = ((MethodSignature) pjp.getSignature())
            .getMethod()
            .getAnnotationsByType(LogThisMethodCall.class);
        if (annotation.length == 0)
            throw new RuntimeException(
                String.format("Something wrong. Aspect advice for annotation " +
                        "pointcut" +
                        " was triggered but no annotation of %s was found",
                    LogThisMethodCall.class.getName()));
        return ((LogThisMethodCall) annotation[0]).value();
    }

}
