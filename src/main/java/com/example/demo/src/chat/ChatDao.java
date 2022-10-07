package com.example.demo.src.chat;


import com.example.demo.src.chat.model.GetChatroomListRes;
import com.example.demo.src.chat.model.GetChatroomRes;
import com.example.demo.src.chat.model.Message;
import com.example.demo.src.chat.model.PostSendMessageReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.util.List;


@Repository
public class ChatDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /** 채팅방 존재 유무 체크 **/
    public int checkChatroom(int userId, int productId) {
        String query = "SELECT c.room_id as room_id\n" +
                "FROM chatroom c\n" +
                "JOIN user_chatroom uc on c.room_id = uc.room_id\n" +
                "WHERE uc.user_id=? AND c.product_id=?";

        Object[] params = new Object[]{userId, productId};

        try {
            return jdbcTemplate.queryForObject(query, Integer.class, params);
        } catch (Exception exception) {
            return -1;
        }
    }

    /** 메세지 보내기 **/
    public int sendMessage(int userId, int chatroomId, PostSendMessageReq postSendMessageReq) {
        String query = "INSERT INTO chat_message(room_id, user_id, message, message_type) VALUES(?,?,?,?)";
        Object[] params = new Object[]{chatroomId, userId, postSendMessageReq.getMessage(), postSendMessageReq.getMessageType()};

        return jdbcTemplate.update(query, params);
    }

    /** 채팅방 생성하기 **/
    public int createChatroom(int productId) {
        String query = "INSERT INTO chatroom(product_id) VALUE (?)";
        jdbcTemplate.update(query, productId);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    /** 채팅방 들어가기 **/
    public void joinChatroom(int userId, int chatroomId) {
        String query = "INSERT INTO user_chatroom(user_id, room_id) VALUES (?,?)";
        Object[] params = new Object[]{userId, chatroomId};

        jdbcTemplate.update(query, params);
    }

    /** 채팅 활성화 하기 **/
    public void activateChatroom(int chatroomId) {
        String query ="UPDATE user_chatroom SET status='Y' WHERE room_id=?";
        jdbcTemplate.update(query, chatroomId);
    }

    /** 채팅방 나가기 **/
    public void leaveChatroom(int userId, int chatroomId) {
        String query ="UPDATE user_chatroom SET status='N' WHERE user_id=? AND room_id=?";
        Object[] params = new Object[]{userId, chatroomId};
        jdbcTemplate.update(query, params);
    }

    /** 채팅방 목록 불러오기 **/
    public List<GetChatroomListRes> getChatroomList(int userId) {
        String query = "SELECT c.room_id     as room_id,\n" +
                "       s.profile_image_url,\n" +
                "       s.store_name  as store_name,\n" +
                "       cm.updated_at as last_updated_at,\n" +
                "       cm.message    as last_sent_message,\n" +
                "       pi.url        as product_image_url\n" +
                "FROM chatroom c\n" +
                "         JOIN user_chatroom uc on c.room_id = uc.room_id\n" +
                "         JOIN store s on uc.user_id = s.user_id\n" +
                "         LEFT JOIN (SELECT product_id, MIN(product_image_id), url\n" +
                "                    FROM product_image pi\n" +
                "                    GROUP BY product_id) pi on c.product_id = pi.product_id\n" +
                "         JOIN (SELECT room_id, message, MAX(updated_at) as updated_at\n" +
                "               FROM chat_message\n" +
                "               GROUP BY room_id) cm on c.room_id = cm.room_id\n" +
                "WHERE uc.user_id != ?\n" +
                "  AND c.room_id in (SELECT room_id\n" +
                "                    FROM user_chatroom\n" +
                "                    WHERE user_id = ?\n" +
                "                      AND status = 'Y')\n";
        Object[] params = new Object[]{userId, userId};

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetChatroomListRes(
                        rs.getInt("room_id"),
                        rs.getString("profile_image_url"),
                        rs.getString("store_name"),
                        rs.getString("last_updated_at"),
                        rs.getString("last_sent_message"),
                        rs.getString("product_image_url")
                ), params);
    }

    /** 채팅방 상세 조회 **/
    public GetChatroomRes getChatroom(int userId, int chatroomId) {
        String query = "SELECT s.store_name, s.user_id, p.product_id, p.name, p.price, s.profile_image_url\n" +
                "FROM store s\n" +
                "         JOIN user_chatroom uc on s.user_id = uc.user_id\n" +
                "         JOIN chatroom c on uc.room_id = c.room_id\n" +
                "         JOIN product p on c.product_id = p.product_id\n" +
                "WHERE s.user_id = ?\n" +
                "  AND uc.room_id = ?";

        Object[] params = new Object[]{userId, chatroomId};

        return jdbcTemplate.queryForObject(query,
                (rs,rowNum)->new GetChatroomRes(
                        rs.getString("store_name"),
                        rs.getInt("user_id"),
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getInt("price"),
                        rs.getString("profile_image_url")
                ),params);
    }

    /** 메세지 목록 조회 **/
    public List<Message> getMessageList(int chatroomId) {
        String query = "SELECT user_id, message, message_type, updated_at as written_at\n" +
                "FROM chat_message\n" +
                "WHERE room_id=?";


        return jdbcTemplate.query(query,
                (rs,rowNum)->new Message(
                        rs.getInt("user_id"),
                        rs.getString("message"),
                        rs.getString("message_type"),
                        rs.getString("written_at")
                ),chatroomId);
    }
}
