package org.svan.aalogger;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AspectTargetTestClass {

    @Disabled
    @LogThisMethodCall
    @MyDummyAnnotation
    public String makeTestCall_DefaultLogger() {
        return ("makeTestCall_DefaultLogger called");
    }

    @Disabled
    @MyDummyAnnotation
    @LogThisMethodCall(TestLogger.class)
    public String makeTestCall_TestLogger() {
        return ("makeTestCall_TestLoggerInterface called");
    }

    @LogThisMethodCall(AnnotatedLoggingAspectTest.AspectLogger1.class)
    public String makeTestCall_Logger1() {
        return ("makeTestCall_Logger1 called");
    }

    @LogThisMethodCall(AnnotatedLoggingAspectTest.AspectLogger2.class)
    public String makeTestCall_Logger2() {
        return ("makeTestCall_Logger2 called");
    }

    @LogThisMethodCall(AnnotatedLoggingAspectTest.AspectLogger1.class)
    @MyDummyAnnotation
    public String makeTestCall_ThrowException() {
        if (true)
            throw new RuntimeException("makeTestCall_ThrowException thrown exception");
        return "makeTestCall_ThrowException called";
    }
}

@interface MyDummyAnnotation{}
