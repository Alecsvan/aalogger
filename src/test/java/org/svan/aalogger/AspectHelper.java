package org.svan.aalogger;

import org.mockito.Mockito;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class AspectHelper {

    private AnnotatedLoggingAspect aspect = new AnnotatedLoggingAspect();

    <T> TestLogger prepareLoggerSpy(Class<? extends TestLogger> clazz,
                                    PlainLoggingToErrorLog plainLoggerSpy,
                                    Object objectOfClassThatHasNestedLoggerClass)
                                                                    throws Exception {
        Constructor<? extends TestLogger> ctor =
            clazz.getDeclaredConstructor(objectOfClassThatHasNestedLoggerClass.getClass());
        TestLogger logger = ctor.newInstance(objectOfClassThatHasNestedLoggerClass);
        logger.setPlainLogger(plainLoggerSpy);
        return Mockito.spy(logger);
    }

    AspectTargetTestClass getProxyWithAspect() {
        AspectJProxyFactory factory = new AspectJProxyFactory(new AspectTargetTestClass());
        factory.addAspect(aspect);
        return factory.getProxy();
    }

    void setLoggers(AnnotatedMethodLogger ... args) {
        aspect.setLoggersList(Arrays.asList(args));
    }
}
