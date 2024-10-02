package com.kh.spring.member.model.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.member.model.vo.Member;

@Repository //db 와 접근하는 클래스에 부여하는 어노테이션
public class MemberDao {

	public Member loginMember(SqlSessionTemplate sqlSession, Member m) {
			
		return sqlSession.selectOne("memberMapper.loginMember", m);

	}

	public int insertMember(SqlSessionTemplate sqlSession, Member m) {
		
		return sqlSession.insert("memberMapper.insertMember", m);
	}

	public int updateMember(SqlSessionTemplate sqlSession, Member m) {
		// TODO Auto-generated method stub
		return sqlSession.update("memberMapper.updateMember", m);
	}

	public int deleteMember(SqlSessionTemplate sqlSession, String userId) {
		// TODO Auto-generated method stub
		return sqlSession.update("memberMapper.deleteMember", userId);
	}

	public int updatePwd(SqlSessionTemplate sqlSession, Member m) {
		System.out.println("dao"+ m);
		
		return sqlSession.update("memberMapper.updatePwd", m);
	}

}
