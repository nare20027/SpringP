package com.kh.spring.member.model.service;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.spring.common.exception.CommException;
import com.kh.spring.member.model.dao.MemberDao;
import com.kh.spring.member.model.vo.Member;
//@EnableAspectJAutoProxy(proxy-target-class="true")
//@Transactional(rollbackFor= {Exception.class, RuntimeException.class}) // 트랜잭션 사용을 위한 어노테이션 - 클래스단위 / 메소드단위 둘 다 명시 가능 , 기술된 클래스는 무조건 rollback 
//@Transactional(noRollbackFor= {NullPointerException.class}) 널포인트 예외는 롤백처리하지 않겠다. 
@Service
public class MemberServiceImpl implements MemberService {
	
	@Autowired
	private SqlSessionTemplate sqlSession; //root-context 에 명시한 sqlsession name과 같게
	
	@Autowired
	private MemberDao memberDao;
	
	//암호화 전 로그인
	@Override
	public Member loginMember(Member m) throws Exception {
		
		Member loginUser = memberDao.loginMember(sqlSession, m);
		
		if(loginUser == null) {
			throw new Exception("loginUser 확인");
		}
		return loginUser;
	}

	@Override
	public void insertMember(Member m) {
		
		int result = memberDao.insertMember(sqlSession, m);
		
		if(result < 0) {
			throw new CommException("회원가입에 실패하였습니다.");
		}
		
	}
	//암호화 후 로그인
	@Override
	public Member loginMember(BCryptPasswordEncoder bCryptPasswordEncoder, Member m) {
		
		Member loginUser = memberDao.loginMember(sqlSession, m);
		
		if(loginUser == null) {
			throw new CommException("loginUser 확인");
		}
		
		System.out.println(bCryptPasswordEncoder.matches(m.getUserPwd(), loginUser.getUserPwd()));
		
		if(!bCryptPasswordEncoder.matches(m.getUserPwd(), loginUser.getUserPwd())) {
			throw new CommException("비밀번호 불일치");
		}
		
		return loginUser;
	}

	// 마이페이지-수정하기
	@Override
	public Member updateMember(Member m) throws Exception {
		int result = memberDao.updateMember(sqlSession, m);
		//memberDao.updateMember(sqlSession, m);
		if(result > 0) {
			throw new Exception("회원수정에 실패하였습니다."); // rollback-> runtime Exception
			/* exception 확인 
			 * https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/dao/DataIntegrityViolationException.html
			 */

		} else {
			Member loginUser = memberDao.loginMember(sqlSession, m);
			return loginUser;
		}
		
	}

	// 회원 탈퇴
	@Override
	public void deleteMember(String userId) {
		
		int result = memberDao.deleteMember(sqlSession, userId);
		
		if(result < 0) {
			throw new CommException("회원탈퇴에 실패하였습니다.");
		}
		
	}

	//비밀번호 수정
	@Override
	public void updatePwd(Member m) {
		
		int result = memberDao.updatePwd(sqlSession, m);
		System.out.println("serviceimple" + m);
		
		if(result < 0) {
			throw new CommException("비밀번호 수정에 실패하였습니다.");
		} 
	}

}
