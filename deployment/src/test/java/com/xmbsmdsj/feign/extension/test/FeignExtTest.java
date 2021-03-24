package com.xmbsmdsj.feign.extension.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.xmbsmdsj.feign.extension.deployment.MockAPI;
import io.quarkus.test.QuarkusUnitTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class FeignExtTest {
    

    public static interface FakeFeign{

    }

    public static class MyInvokationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return null;
        }
    }


    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
    .setArchiveProducer( () ->
        ShrinkWrap.create(JavaArchive.class)
            .addClasses(MockAPI.class)
    );

    @Test
    public void testNothing() {
        Proxy.newProxyInstance(
                FeignExtTest.class.getClassLoader(),
                new Class[]{FakeFeign.class},
                new MyInvokationHandler()
        );
    }
}
