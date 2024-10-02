package com.kh.spring.member.controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.kh.spring.member.model.service.MemberService;
import com.kh.spring.member.model.service.MemberServiceImpl2;
import com.kh.spring.member.model.vo.Member;

@SessionAttributes("loginUser") // Model에 loginUser라는 key 값으로 객체가 등록되면 자동으로 세션에 추가되는 어노테이션
@Controller // 클래스 단위로 빈에 자동으로 등록되게  --> 컨트롤러영역에는 @Controller
public class MemberController {
	private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

	
	/*@ExceptionHandler(value = BadSqlGrammarException.class)
	public ModelAndView controllerExceptionHandler(Exception e) {
       e.printStackTrace();
        //return "common/errorPage"; --> String으로 단순히 페이지만 던지겠다. 
		 return new ModelAndView("common/errorPageServer").addObject("msg", e.getMessage());
    }
    */
	
	/* 생기는 컨트롤러마다 다 만들어줘야 함
	@ExceptionHandler(value = Exception.class)
	public ModelAndView controllerExceptionHandler(Exception e) {
       e.printStackTrace();
		 return new ModelAndView("common/errorPage").addObject("msg", e.getMessage());
    }
    */
	
	

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder; //비밀번호암호화
	

	@Autowired // 빈스캐닝을 통해 인터페이스를 구현한 구현체 클래스 중 서비스로 등록된(@Service) 클래스(빈)를 찾아 자동으로 값을 넘겨준다. 
	private MemberService memberService;
	
	@Autowired //target
	private MemberServiceImpl2 memberServiceImpl2;
	
	// 파라미터(요청시 전달값)를 전송받는 방법
	
	/* 1. HttpServletRequest 를 통해 전송을 받는 방법 (기존 jsp/servlet 방식)
	 * 
	@RequestMapping(value="login.me", method=RequestMethod.POST) // view 에서 servlet이 아니라 컨트롤러의 메소드를 호출하여 실행  
																 // @RequestMapping 을 명시하여 HandlerMapping 에 등록, POST 방식 명시  
	public String loginMember(HttpServletRequest request) {
		String userId = request.getParameter("userId");
		String userPwd = request.getParameter("userPwd");
		
		System.out.println("ID : " + userId);
		System.out.println("PWD : " + userPwd);
		
		return "main"; // 메인페이지가 보이도록 --> servlet-context의 InternalResourceViewResolver 를 활용하여 forwarding (값유지)
					   // --> 리턴되는 문자열을 servlet-context.xml 파일에 기술된 viewResolver에 의해 사용자에게 노출될 뷰페이지로 포워딩된다. 
	}
	*/
	
	
	/*2. @RequestParam 어노테이션 방식  - 스프링에서 제공하는 파라미터를 받아오는 방법 
	 
	
	@RequestMapping(value="login.me", method=RequestMethod.POST) 
	public String loginMember(@RequestParam("userId") String userId, @RequestParam("userPwd") String userPwd) {
		
		System.out.println("ID : " + userId);
		System.out.println("PWD : " + userPwd);
		
		return "main";
	}
	*/
	
	
	/*3.@RequestParam 어노테이션 생략  - 매개변수를 파라미터의 name과 동일하게 작성해야 어노테이션을 생략하더라도 값을 받아올 수 있다.
	
	@RequestMapping(value="login.me", method=RequestMethod.POST) 
	public String loginMember(String userId,String userPwd) {
		
		System.out.println("ID : " + userId);
		System.out.println("PWD : " + userPwd);
		
		return "main";
	}
	*/

	
	/*4. @ModelAttribute를 이용한 방법 - 요청 파라미터가 많은 경우 객체 타입으로 넘겨받는다. 기본생성자와 setter를 이용한 주입방식이기 때문에  둘 중 하나라도 없으면 에러 발생
	  							 - 반드시 파라미터의 name 속성 값과 필드명이 동일해야 하며, setter 작성 규칙에 맞게 작성되어야 함. 
	
	@RequestMapping(value="login.me", method=RequestMethod.POST) 
	public String loginMember(@ModelAttribute Member m) {
		
		System.out.println("ID : " + m.getUserId());
		System.out.println("PWD : " + m.getUserPwd());
		
		return "main";
	}*/
	
	
	/*5. @ModelAttribute 어노테이션 생략하고 객체를 바로 전달받는 방식
	@RequestMapping(value="login.me", method=RequestMethod.POST) 
	public String loginMember(Member m, HttpSession session) {
		
		try {
			Member loginUser = memberService.loginMember(m);
			System.out.println("loginUser : " + loginUser);
			session.setAttribute("loginUser", loginUser);
			return "redirect:/"; // redirect:index.jsp --> sendRedirect 방식 사용 (session에 로그인한 유저 객체 값 담아주기)
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "common/errorPage";
		}
				
	}
	*/
	
	
	/*응답 페이지에 응답할 데이터가 있는 경우*/
	
	/*1. Model이라는 객체를 사용하는 방법 - 응답뷰로 전달하고자 하는 데이터를 Map(key-value)형식으로 담을 수 있다. 기본 scope:request
	
	@RequestMapping(value="login.me", method=RequestMethod.POST) 
	public String loginMember(Member m, HttpSession session, Model model) {
		
		try {
			Member loginUser = memberService.loginMember(m);
			System.out.println("loginUser : " + loginUser);
			session.setAttribute("loginUser", loginUser);
			return "redirect:/"; // redirect:index.jsp --> sendRedirect 방식 사용 (session에 로그인한 유저 객체 값 담아주기)
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("msg", "로그인 실패");
			return "common/errorPage";
		}
		
	}*/
	
	
	/*2. ModelAndView라는 객체를 사용하는 방법 - 값과 뷰를 모두 지정해줄 수 있다.
	
	@RequestMapping(value="login.me", method=RequestMethod.POST) 
	public ModelAndView loginMember(Member m, HttpSession session, ModelAndView mv) {
		
		try {
			Member loginUser = memberService.loginMember(m);
			System.out.println("loginUser : " + loginUser);
			session.setAttribute("loginUser", loginUser);
			mv.setViewName("redirect:/"); // redirect:index.jsp --> sendRedirect 방식 사용 (session에 로그인한 유저 객체 값 담아주기)
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mv.addObject("msg", "로그인 실패"); //key-value 형태로 값을 담고 화면에서 ${키값}으로 뽑음
			mv.setViewName("common/errorPage"); // 일반 경로를 적으면 forward
		}
		return mv;
	}
	*/
	
	/*
	@RequestMapping("logout.me")
	public String logoutMember(HttpSession session) {
		
		session.invalidate(); // 세션 비워서 로그아웃
		
		return "redirect:/";
	}
	*/
	
	/*3. @SessionAttributes 어노테이션 사용하기 - Model 이라는 객체에 Attribute를 추가할 때 설정된 key값을 세션끼지 자동으로  등록시키는 기능 
	
	@RequestMapping("login.me")
	public String loginMember(Member m, Model model) {
		
		try {
			Member loginUser = memberService.loginMember(m);
			System.out.println("loginUser : " + loginUser);
			model.addAttribute("loginUser", loginUser); //--> session 대신 model에 key 값을 등록하면  session에 값을 등록해준다.
			return "redirect:/"; // redirect:index.jsp --> sendRedirect 방식 사용 (session에 로그인한 유저 객체 값 담아주기)
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("msg", "로그인 실패");
			return "common/errorPage";
		}
	}
	*/
	
	//로그아웃 방법 변경 --> @SessionAttributes 사용시
	@RequestMapping("logout.me")
	public String logoutMember(SessionStatus status) {
		
		status.setComplete(); // 세션을 완료해준다 --> 만료시킨다 (현재 컨트롤러의 @SessionAttributes 에 의해 저장된 오브젝트를 제거 
		
		return "redirect:/";
	}
	
	//회원가입 페이지 
	@RequestMapping("enrollForm.me")
	public String enrollForm() {
		
		return "member/memberEnrollForm";
	}

	//회원가입 contoller
	@RequestMapping("insert.me")
	public String insertMember(@ModelAttribute Member m, @RequestParam("post") String post, 
														 @RequestParam("address1") String address1,
														 @RequestParam("address2") String address2, HttpSession session) {
		
		m.setAddress(post + "/" + address1 + "/" + address2);
		System.out.println("m : " + m );
		
		System.out.println("암호화 전 : "+ m.getUserPwd());
		
		// 비밀번호 암호화 작업		
		String encPwd = bCryptPasswordEncoder.encode(m.getUserPwd());
		System.out.println("암호화 후 : "+ encPwd);
		
		m.setUserPwd(encPwd);
		
		memberService.insertMember(m);
		
		session.setAttribute("msg", "회원가입 성공"); // <-- menubar msg
		
		return "redirect:/";
	}
	
	//암호화처리 후 로그인
	@RequestMapping("login.me")
	public String loginMember(Member m, Model model) throws Exception{
		/*
		 * String encPwd = bCryptPasswordEncoder.encode(m.getUserPwd());
		 * System.out.println("암호화 후 : "+encPwd);
		 */
			Member loginUser = memberService.loginMember(bCryptPasswordEncoder, m);
			System.out.println("loginUser : " + loginUser);
			model.addAttribute("loginUser", loginUser);
			return "redirect:/"; // redirect:index.jsp
	
	}
	
	// 마이페이지
	@RequestMapping("myPage.me")
	public String myPage() {
		return "member/myPage";
	}
	
	
	// 마이페이지 - 수정하기
	@RequestMapping("update.me")
	public String updateMember(@ModelAttribute Member m, @RequestParam("post") String post, 
														 @RequestParam("address1") String address1,
														 @RequestParam("address2") String address2, Model model) throws Exception{
		
		m.setAddress(post + "/" + address1 + "/" + address2);
		Member userInfo;
		
		userInfo = memberServiceImpl2.updateMember(m); //target-proxy
		//userInfo = memberService.updateMember(m);
		model.addAttribute("loginUser", userInfo);

		return "member/myPage";
		
	}
	// 회원탈퇴
	@RequestMapping("delete.me")
	public String deleteMember(String userId) {
		
		memberService.deleteMember(userId);
		
		return "redirect:logout.me";
	}
	
	// 비밀번호 수정 
	@RequestMapping("updatePwd.me")
	public String updatePwd(@RequestParam("oldPwd") String oldPwd,
							@RequestParam("newPwd") String newPwd,
							@RequestParam("rePwd") String rePwd, HttpSession session) {
		
		Member loginUser = (Member)session.getAttribute("loginUser");
		String userId = loginUser.getUserId();
		String userPwd = loginUser.getUserPwd();
		String encPwd = bCryptPasswordEncoder.encode(newPwd);
		
		System.out.println("oldPwd : " + oldPwd);
		System.out.println("userId = " + userId + " userPwd = " + userPwd);
		
		Member m = new Member();
		
		if(!bCryptPasswordEncoder.matches(oldPwd, userPwd)) {
			session.setAttribute("msg", "현재 비밀번호가 아닙니다.");	
			return "member/myPage";
		} else if(!newPwd.equals(rePwd)){
			session.setAttribute("msg", "비밀번호를 다시 확인해주세요.");	
			return "member/myPage";
		} else {	
			m.setUserId(userId);
			m.setUserPwd(encPwd);			
			memberService.updatePwd(m);		
			session.setAttribute("msg", "비밀번호가 수정되었습니다. 다시 로그인해주세요.");	
			return "redirect:logout.me";
		}
		
		
	}

	
	
}
