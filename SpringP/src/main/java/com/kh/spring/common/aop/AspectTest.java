package com.kh.spring.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

//@Aspect // 붙이지 않으면 aop 설정이 적용되지 않는다. (api 참고)
@Component
public class AspectTest {
	
	@Pointcut("execution(* com.kh.spring..*ServiceImpl.*(..))") // Pointcut 적용 범위 : 해당 메소드의 모든 ServiceImpl 안의 모든 메소드 
																//com.kh.spring.. -> .. 의미 : spring 패키지의 모든 하위 폴더
	public void beforePointCut() {
		
	}
	
	@Before("beforePointCut()") // 상단의 pointcut을 해당 어드바이스에 적용한다. / 해당 JointPoint 의 실행 전 
	public void before(JoinPoint join) throws Exception {
	//Joinpoint : 타겟이 되는 대상의 정보를 가지고 있다. 가장 앞에 적어주어야 함. 
	
		Signature sig = join.getSignature();
		Object[] params = join.getArgs();
		for(Object obj : params) {
			System.out.println("obj :"+ obj); // --> 상단에 로거 명시 후 로거로 출력해도 됨 : 비밀번호 암호화와 관련된 security 정보 
		}
		
		System.out.println("메소드 호출 전 확인  " + sig.getDeclaringTypeName() + " : " + sig.getName());
	}
	
	
	@Pointcut("execution(* com.kh.spring..*ServiceImpl.*(..))")
	public void afterPointCut() {
	
	}
	
	@After("afterPointCut()") 
	public void after(JoinPoint join) throws Exception {
	
	
		Signature sig = join.getSignature();
		
		System.out.println("메소드 호출 후 확인  " + sig.getDeclaringTypeName() + " : " + sig.getName());
	}
	
	
	@Pointcut("execution(* com.kh.spring..*ServiceImpl.*(..))") 
	public void afterReturning() {
	
	}
	
	@AfterReturning(pointcut="afterReturning()", returning="returnObj") // 순차적으로 정상적으로 실행된 로그를 기록하기 위해 사용
	public void returningPoint(JoinPoint join, Object returnObj) throws Exception {
	
		Signature sig = join.getSignature();
		
		System.out.println("@AfterReturning 확인  " + sig.getDeclaringTypeName() + " : " + sig.getName() + " returnObj : " + returnObj);
	}
	
	
	@Pointcut("execution(* com.kh.spring..*ServiceImpl.*(..))") 
	public void afterThrowingPoint() {
	
	}
	
	@AfterThrowing(pointcut="afterThrowingPoint()", throwing="exceptionObj") // 예외 발생에 대한 로그 기록 
	public void throwingPoint(JoinPoint join, Exception exceptionObj) throws Exception {
	
		Signature sig = join.getSignature();
		
		System.out.println("@AfterThrowing 확인  " + sig.getDeclaringTypeName() + " : " + sig.getName());
		
		if(exceptionObj instanceof IllegalArgumentException ) {
			System.out.println("부적합한 값이 입력되었습니다.");
		} else {
			System.out.println("예외 발생 메세지  : " + exceptionObj.getMessage());
			System.out.println("예외 발생 메세지  : " + exceptionObj.getClass().getName());
		}
		
	}
	
	
	@Pointcut("execution(* com.kh.spring..*ServiceImpl.*(..))") 
	public void arouncPointCut() {
	
	}
	
	@Around("arouncPointCut()") // 예외 발생에 대한 로그 기록 
	public Object aroundLog(ProceedingJoinPoint join) throws Throwable {
	
		String methodName = join.getSignature().getName();
		
		//실행 시간 
		StopWatch stopWatch = new StopWatch(); 
		stopWatch.start();
		
		Object obj = join.proceed(); // 메소드 실행 전과 후로 나누는 기준 
		
		stopWatch.stop();
		System.out.println(methodName + " 소요시간(ms) : " + stopWatch.getTotalTimeMillis() + "초" );
		
		return obj;
		
	}
}
