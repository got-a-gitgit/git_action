package com.example.demo.src.chat;


import com.example.demo.config.BaseException;
import com.example.demo.src.chat.model.PostSendMessageReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Transactional(rollbackFor = Exception.class)
@Service
public class ChatService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ChatDao chatDao;
    private final ChatProvider chatProvider;
    private final JwtService jwtService;


    @Autowired

    public ChatService(ChatDao chatDao, ChatProvider chatProvider, JwtService jwtService) {
        this.chatDao = chatDao;
        this.chatProvider = chatProvider;
        this.jwtService = jwtService;
    }

    /** 채팅 시작하기 & 메세지 보내기 **/
    public void sendMessage(int userId, PostSendMessageReq postSendMessageReq) throws BaseException {
        try{
            int productId = postSendMessageReq.getProductId();
            //채팅방이 존재하는지 확인
            int chatroomId = chatDao.checkChatroom(userId, productId);

            //존재하지 않는다면 생성
            if (chatroomId == -1) {
                //채팅방 생성
                chatroomId = chatDao.createChatroom(productId);
                //구매자 채팅방 입장
                chatDao.joinChatroom(userId, chatroomId);
                //판매자 채팅방 입장
                chatDao.joinChatroom(postSendMessageReq.getSellerId(), chatroomId);
            }

            //상대방 채팅방 활성화
            chatDao.activateChatroom(chatroomId);

            //메세지 보내기
            int affectedRows = chatDao.sendMessage(userId,chatroomId,postSendMessageReq);
        } catch(Exception exception){
            logger.error("sendMessage 에러", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 채팅방 나가기 **/
    public void leaveChatroom(int userId, int chatroomId) throws BaseException {
        try{
            chatDao.leaveChatroom(userId, chatroomId);
        } catch(Exception exception){
            logger.error("leaveChatroom 에러", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

