package ru.nsu.stoliarov.streetmap.service.speedtest;


import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
@AllArgsConstructor
public class SpeedTestAspect {

    private final SpeedTestDataHolder speedTestDataHolder;

    @Around("@annotation(ru.nsu.stoliarov.streetmap.service.speedtest.SpeedTest)")
    Object testSpeed(ProceedingJoinPoint joinPoint) throws Throwable {

        boolean isFlush = Optional.of(joinPoint)
                .map(s -> (MethodSignature) s.getSignature())
                .map(MethodSignature::getMethod)
                .map(method -> method.getAnnotation(SpeedTest.class))
                .map(SpeedTest::isFlush)
                .orElse(false);


        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long finish = System.currentTimeMillis();

        speedTestDataHolder.setMilliseconds(speedTestDataHolder.getMilliseconds() + finish - start);
        if (!isFlush) {
            speedTestDataHolder.setRecordCount(speedTestDataHolder.getRecordCount() + 1);
        }

        return result;
    }
}
