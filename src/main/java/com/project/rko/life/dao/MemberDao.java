package com.project.rko.life.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.project.rko.life.dto.Member;

@Mapper
public interface MemberDao {

	Member getMemberById(@Param("id") int id);

	void join(Map<String, Object> param);

	int getLoginIdDupCount(@Param("loginId") String loginId);

	Member getMemberByLoginId(@Param("loginId") String loginId);

	Member getMemberByNameAndEmail(String name, String email);

	void passWordmodify(int actorId, String loginPw);

	void infoModify(Map<String, Object> param);

	void delete(@Param("id") int id);

	boolean isJoinableLoginId(@Param("loginId") String loginId);



}
