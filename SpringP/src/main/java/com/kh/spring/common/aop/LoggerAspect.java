package com.kh.spring.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kh.spring.member.controller.MemberController;

public class LoggerAspect {

	private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
	
	public Object loggerAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
		
		Signature sig = joinPoint.getSignature();
		
		String type = sig.getDeclaringTypeName();
		String methodName = sig.getName();
		
		String cName = "";
		if(type.indexOf("Controller") > -1) { // type이 Controller 에 포함되면 -1보다 큰 수 
			
			cName = "Controller : ";
			
		}else if(type.indexOf("Service") > -1){ // type이 Service 에 포함되면 -1보다 큰 수 
			
			cName = "Service : ";
			
		}else if(type.indexOf("Dao") > -1) { // type이 Dao 에 포함되면 -1보다 큰 수 
					
			cName = "Dao : ";
			
		}
		
		logger.info("[Before] " + cName + type + "."+methodName+"()");
		Object obj = joinPoint.proceed();
		logger.info("[After] " + cName + type + "."+methodName+"()");
		return obj;
	}
	
}
