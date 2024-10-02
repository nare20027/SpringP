package com.kh.spring.member.model.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.kh.spring.member.model.vo.Member;

public interface MemberService {

	Member loginMember(Member m)throws Exception ; // 암호화 전 로그인 

	void insertMember(Member m);

	Member loginMember(BCryptPasswordEncoder bCryptPasswordEncoder, Member m); // 암호화 후 로그인

	Member updateMember(Member m) throws Exception;

	void deleteMember(String userId);

	void updatePwd(Member m);


}
