package com.zhl;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Aspect
public class ServiceLogAspect {
    /**
     * 第一个*: 匹配任意返回类型
     * com.zhl.service.impl: 指定基础包名，要切的class所在的包
     * ..: 匹配当前包及子包下的所有类
     * *: 匹配任意类名
     * .: 无意义
     * *(..): *匹配任意方法名，括号里表示参数列表
     * 切面
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.zhl.service.impl..*.*(..))")
    public Object recordTimesLog(ProceedingJoinPoint joinPoint) throws Throwable{
        long begin = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        String point = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();
        long end = System.currentTimeMillis();
        long takeTime = end - begin;
        log.info("{} 耗时: {} ms", point, takeTime);
        return result;
    }
}
