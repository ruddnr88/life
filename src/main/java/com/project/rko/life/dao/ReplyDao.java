package com.project.rko.life.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.project.rko.life.dto.Reply;

@Mapper
public interface ReplyDao {

	List<Reply> getForPrintReplies(Map<String, Object> param);

	void writeReply(Map<String, Object> param);

	Reply getForPrintReplyById(int id);

	void deleteReply(int id);

	void modifyReply(Map<String, Object> param);

}
