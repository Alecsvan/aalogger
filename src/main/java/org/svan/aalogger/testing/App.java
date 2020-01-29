package org.svan.aalogger.testing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.svan.aalogger.LogThisMethodCall;

@ComponentScan("org.svan.aalogger")
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class App implements CommandLineRunner {

    @Autowired
    ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
        doSomething("Test");
        context.getBean(MyBean.class).doSomething();

    }

    @LogThisMethodCall
    public void doSomething(String arg1) {
        System.out.println("App.doSomething called");
    }
}
