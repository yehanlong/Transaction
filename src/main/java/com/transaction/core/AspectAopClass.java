package com.transaction.core;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @ClassName ${Name}
 * @Description TODO
 * @Author < a href="jcsong50@best-inc.com">sqc</ a>
 * @Date 2019/4/2 19:19
 * @Version 1.0
 */
@Aspect
@Order(-1)
@Component
public class AspectAopClass {

    private static Logger logger  = LoggerFactory.getLogger(AspectAopClass.class);

    @Around("execution(* com.sqc95111.demologin.service.*.*(..))")
    public Object myPointCut(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        logger.info("{}.{} begin ",className,methodName);
        long begin = System.currentTimeMillis();
        logger.info("{}.{} end {executed in {} msec}",className,methodName,System.currentTimeMillis()-begin);
        Object result = joinPoint.proceed();
        return result;
    }

    @Pointcut("execution(* com.sqc95111.demologin.service.*.*(..))")
    public void aspectMethod(){
    }

//    @Before(value = "aspectMethod()")
//    public void beforeMethod(){
//        String id = UUID.randomUUID().toString();
//        logger.info("{} start--------------------->",id);
//    }


//    @Before(value = "myPointCut(ProceedingJoinPoint)")
//    public String writeLine(){
//        String id = UUID.randomUUID().toString();
//        logger.info("{}----------------->",id);
//        return "success";
//    }
}