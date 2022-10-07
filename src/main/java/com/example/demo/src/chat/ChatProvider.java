package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.src.chat.model.GetChatroomListRes;
import com.example.demo.src.chat.model.GetChatroomRes;
import com.example.demo.src.chat.model.Message;
import com.example.demo.utils.JwtService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
@AllArgsConstructor
public class ChatProvider {
    private final ChatDao chatDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());



    /** 채팅방 목록 조회 **/
    public List<GetChatroomListRes> getChatroomList(int userId) throws BaseException {
        try{
            return chatDao.getChatroomList(userId);
        }
        catch (Exception exception) {
            logger.error("getChatroomList 에러", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 채팅방 상세 조회 **/
    public GetChatroomRes getChatroom(int userId, int chatroomId) throws BaseException {
        try {
            GetChatroomRes getChatroomRes = chatDao.getChatroom(userId, chatroomId);
            List<Message> messageList = chatDao.getMessageList(chatroomId);
            getChatroomRes.setMessageList(messageList);

            return getChatroomRes;
        } catch (Exception exception) {
            logger.error("getChatroom 에러", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
