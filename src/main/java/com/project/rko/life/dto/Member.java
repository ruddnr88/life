package com.project.rko.life.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Member {
	private int id;
	private String regDate;
	private String updateDate;
	private boolean delStatus;
	private String delDate;
	private String loginId;
	private String loginPw;
	private String name;
	private String nickname;
	private String email;
	public Map<String, Object> getExtra() {
		// TODO Auto-generated method stub
		return null;
	}
}
