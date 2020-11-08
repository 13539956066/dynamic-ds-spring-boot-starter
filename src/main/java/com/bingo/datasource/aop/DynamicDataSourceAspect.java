package com.bingo.datasource.aop;

import com.bingo.datasource.annotation.DS;
import com.bingo.datasource.util.DynamicDataSourceContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Author GouBin
 * @Date 2020-10-26
 * @Version 1.0
 * @Modified_By
 */
@Aspect
@Component
public class DynamicDataSourceAspect {

    @Pointcut("@annotation(com.bingo.datasource.annotation.DS)")
    public void dataSourcePointCut(){

    }
    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String dsKey = getDSAnnotation(joinPoint).value();
        DynamicDataSourceContextHolder.push(dsKey);
        try{
            return joinPoint.proceed();
        }finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    /**
     * 根据类或方法获取数据源注解
     */
    private DS getDSAnnotation(ProceedingJoinPoint joinPoint){
        Class<?> targetClass = joinPoint.getTarget().getClass();
        DS dsAnnotation = targetClass.getAnnotation(DS.class);
        // 先判断类的注解，再判断方法注解
        if(Objects.nonNull(dsAnnotation)){
            return dsAnnotation;
        }else{
            MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
            return methodSignature.getMethod().getAnnotation(DS.class);
        }
    }
}
