package org.svan.aalogger;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Slf4j
class AnnotatedLoggingAspectTest {

    private TestLogger loggerSpy1, loggerSpy2;
    private PlainLoggingToErrorLog plainLoggerSpy;
    private AspectHelper aspectHelper;
    private AspectTargetTestClass proxy;

    @BeforeEach
    public void setup() throws Exception {
        aspectHelper = new AspectHelper();
        plainLoggerSpy = spy(new PlainLoggingToErrorLog());
        loggerSpy1 = aspectHelper.prepareLoggerSpy(AspectLogger1.class,
            plainLoggerSpy, this);
        loggerSpy2 = aspectHelper.prepareLoggerSpy(AspectLogger2.class,
            plainLoggerSpy, this);
        proxy = aspectHelper.getProxyWithAspect();
    }

    @Test
    public void testAspectCallFlow() {
        aspectHelper.setLoggers(loggerSpy1, loggerSpy2);
        checkExceptionThrowsWhileDefaultLoggerExpectedButNotAdded();
        assertAll(
            () -> verifyNoInteractions(loggerSpy1),
            () -> verifyNoInteractions(loggerSpy2));

        assertAll(
            () -> assertEquals("makeTestCall_Logger1 called",
                proxy.makeTestCall_Logger1()),
            () -> verify(loggerSpy1).logBeforeAndAfterCall(any(ProceedingJoinPoint.class)),
            () -> verify(plainLoggerSpy).logBefore(any(ProceedingJoinPoint.class)),
            () -> verify(plainLoggerSpy).logAfter(any(ProceedingJoinPoint.class),
                eq("makeTestCall_Logger1 called")),
            () -> verifyNoInteractions(loggerSpy2));

        assertAll(
            () -> assertEquals("makeTestCall_Logger2 called",
                proxy.makeTestCall_Logger2()),
            () -> verify(loggerSpy2).logBeforeAndAfterCall(any(ProceedingJoinPoint.class)),
            () -> verify(plainLoggerSpy).logAfter(any(ProceedingJoinPoint.class),
                eq("makeTestCall_Logger1 called")));
    }

    private void checkExceptionThrowsWhileDefaultLoggerExpectedButNotAdded() {
        Exception ex = assertThrows(RuntimeException.class,
            proxy::makeTestCall_DefaultLogger,
            "Exception should be thrown as default logger not provided");
        assertEquals(
            "Cannot find requested AnnotatedMethodLogger implementation: " +
                PlainLog.class.getSimpleName(),
            ex.getMessage());
    }

    @Test
    public void checkThatDefaultLoggerIsCalledForAnnotationWithoutParameters() {
        Exception ex = assertThrows(RuntimeException.class,
            proxy::makeTestCall_DefaultLogger,
            "Exception should be thrown as no loggers added to aspect at this point");
        assertEquals(
            "No loggers provided for logging aspect",
            ex.getMessage());

        aspectHelper.setLoggers(plainLoggerSpy);
        assertAll(
            () -> assertEquals("makeTestCall_DefaultLogger called",
                proxy.makeTestCall_DefaultLogger()),
            () -> verify(plainLoggerSpy).logBeforeAndAfterCall(any(ProceedingJoinPoint.class)));
    }

    @Test
    public void checkThatExceptionIsPassingThroughAspectAndLogger_LoggerAfterTriggers() {
        aspectHelper.setLoggers(loggerSpy1, loggerSpy2);
        Exception ex = assertThrows(RuntimeException.class,
            () -> proxy.makeTestCall_ThrowException());
        assertAll(
            () -> assertEquals("makeTestCall_ThrowException thrown exception",
                ex.getMessage()),
            () -> verify(loggerSpy1).logBeforeAndAfterCall(any(ProceedingJoinPoint.class)),
            () -> verify(plainLoggerSpy).logBefore(any(ProceedingJoinPoint.class)),
            () -> verify(plainLoggerSpy).logAfter(any(ProceedingJoinPoint.class),
                any(RuntimeException.class)),
            () -> verify(plainLoggerSpy).logAfter(any(ProceedingJoinPoint.class),
                argThat(exception -> ((RuntimeException) exception)
                    .getMessage()
                    .equals("makeTestCall_ThrowException thrown exception"))));
    }

    class AspectLogger1 implements TestLogger {
        PlainLoggingToErrorLog plainLogger;

        @Override
        public void setPlainLogger(PlainLoggingToErrorLog pl) {
            plainLogger = pl;
        }

        @Override
        public Object logBeforeAndAfterCall(ProceedingJoinPoint pjp) throws Throwable {
            return plainLogger.logBeforeAndAfterCall(pjp);
        }
    }

    class AspectLogger2 implements TestLogger {
        PlainLoggingToErrorLog plainLogger;

        @Override
        public void setPlainLogger(PlainLoggingToErrorLog pl) {
            plainLogger = pl;
        }

        @Override
        public Object logBeforeAndAfterCall(ProceedingJoinPoint pjp) throws Throwable {
            return plainLogger.logBeforeAndAfterCall(pjp);
        }
    }
}
