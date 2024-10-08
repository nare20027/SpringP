package com.kh.spring.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class TestIntercepter extends HandlerInterceptorAdapter{
	private static final Logger logger = LoggerFactory.getLogger(TestIntercepter.class);

	// 뷰에서 요청 -->filter--> DispatcherServlet -- 인터셉터 --> Controller --> Service --> Dao --> DB
	//								         <-- 인터셉터 --  Controller <-- Service <-- Dao <-- DB

	//servlet-context에 등록 필요
	
	// DispatcherServlet이 controller를 호출하기전 수행 
	@Override   
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub
		
		logger.debug("========== START preHandle ==========");
		logger.debug(request.getRequestURI());
		return super.preHandle(request, response, handler);
	}

	// Controller에서 DispatcherServlet으로 리턴되는 순간에 수행(비즈니스 로직 후처리)
	@Override //비즈니스로직의 후처리를 담당
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
		logger.debug("========== postHandle ==========");
		super.postHandle(request, response, handler, modelAndView);
	}

	// 최종 결과를 생성하는 일을 포함한 모든 작업이 완료된 후 리소스를 반환하기 전에 수행
	@Override 
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
		logger.debug("========== afterCompletion END ==========");
		super.afterCompletion(request, response, handler, ex);
	}

}
