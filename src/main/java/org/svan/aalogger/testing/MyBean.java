package org.svan.aalogger.testing;

import org.springframework.stereotype.Component;
import org.svan.aalogger.LogThisMethodCall;

@Component
public class MyBean {

    @LogThisMethodCall
    void doSomething() {
        System.out.println("MyBean.doSomething() called");
    }
}
