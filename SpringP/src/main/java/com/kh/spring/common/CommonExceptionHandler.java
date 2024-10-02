package com.kh.spring.common;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;


@ControllerAdvice("com.kh.spring") // 모든 컨트롤러에서 발생하는 exception 클래스에 대한 처리  
public class CommonExceptionHandler { // servlet-context bean 등록 필요

	@ExceptionHandler({ Exception.class })
	public ModelAndView runtimeExceptionHandler(Exception ex, HttpServletRequest request) { // 스프링의 오류는 모두  runtime 기준---> 오류를 육안으로 확인하기 어려움
		ModelAndView view = new ModelAndView();

		view.setViewName("common/errorPage");
		view.addObject("msg", ex.getMessage());
		
		ex.printStackTrace();

		return view;
	}

}