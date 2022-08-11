package de.borisskert.springaopexample;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;

import static org.slf4j.LoggerFactory.getLogger;

@Aspect
@Component
public class ProfiledAspect {
    private static final Logger LOG = getLogger(ProfiledAspect.class);

    @Around("@annotation(Profiled)")
    public Object profiledExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String profiledValue = getProfiledValue(joinPoint);
        StopWatch stopWatch = new StopWatch();

        try {
            stopWatch.start();
            Object result = joinPoint.proceed();
            stopWatch.stop();

            logResult(profiledValue, stopWatch, result, joinPoint.getArgs());

            return result;
        } catch (Throwable e) {
            stopWatch.stop();
            logException(profiledValue, stopWatch, e, joinPoint.getArgs());
            throw e;
        }
    }

    private void logResult(String profiledValue, StopWatch stopWatch, Object result, Object[] args) {
        LOG.info("{}, millis: {}, args: {}, result: {}", profiledValue, stopWatch.getTotalTimeMillis(), args, result);
    }

    private void logException(String profiledValue, StopWatch stopWatch, Throwable e, Object[] args) {
        LOG.info("{}, millis: {}, args: {}, exception: {} - {}", profiledValue, stopWatch.getTotalTimeMillis(), args, e.getClass().getCanonicalName(), e.getMessage());
    }

    private String getProfiledValue(ProceedingJoinPoint joinPoint) throws IllegalAccessException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Profiled profiledAnnotation = method.getAnnotation(Profiled.class);

        if (profiledAnnotation == null) {
            throw new IllegalAccessException("No '@Profiled' annotation");
        }

        return profiledAnnotation.value();
    }
}
