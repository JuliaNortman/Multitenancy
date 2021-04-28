package com.knu.ynortman.multitenancy.util.aop;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;


@Aspect
@Configuration
@Slf4j
@EnableAspectJAutoProxy
//@ConditionalOnExpression("${aspect.enabled:true}")
public class ExecutionTimeAdvice {

	@Around("@annotation(TrackExecutionTime)")
	public Object executionTime(ProceedingJoinPoint point) throws Throwable {
		long startTime = System.currentTimeMillis();
		Object object = point.proceed();
		long endtime = System.currentTimeMillis();
		long executionTime = endtime - startTime;
		
		MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        TrackExecutionTime trackExecutionTime = method.getAnnotation(TrackExecutionTime.class);
        String message = trackExecutionTime.message();
        if (message != null && StringUtils.isBlank(message)) {
            log.info("Method {} execution: {} ms", point.getSignature().toShortString(), executionTime);
        }
        else {
            log.info("{}: {} ms", message, executionTime);
        }
		
		/*log.info("Class Name: " + point.getSignature().getDeclaringTypeName() + ". Method Name: "
				+ point.getSignature().getName() + ". Time taken for Execution is : " + (endtime - startTime) + "ms");*/
		return object;
	}
}