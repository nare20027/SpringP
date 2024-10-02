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

@Service
public class MemberServiceImpl2 {
	
	@Autowired
	private SqlSessionTemplate sqlSession; //root-context 에 명시한 sqlsession name과 같게
	
	@Autowired
	private MemberDao memberDao;
	
	// 마이페이지-수정하기

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


}
