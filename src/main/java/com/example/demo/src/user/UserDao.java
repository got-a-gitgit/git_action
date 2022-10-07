package com.example.demo.src.user;

import com.example.demo.src.user.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // Insert SQL
    /** 회원가입 **/
    public int insertUser(String email) {
        String query = "INSERT INTO user (email) VALUES(?)";

        this.jdbcTemplate.update(query, email);

        String userIdQuery = "SELECT last_insert_id()";

        return this.jdbcTemplate.queryForObject(userIdQuery, int.class);

    }

    /** SNS 연동 **/
    public void updateSNSFlag(int userId, String status){
        String query = "UPDATE user SET sns_flag = ? WHERE user_id = ?";

        this.jdbcTemplate.update(query, status, userId);
    }

    public void insertSNSInfo(int userId, int snsTypeId ){
        String query = "INSERT INTO sns(user_id, sns_type_id) VALUES(?, ?) " +
                "ON DUPLICATE KEY UPDATE user_id = ?, sns_type_id = ?";

        this.jdbcTemplate.update(query, userId, snsTypeId, userId, snsTypeId);

    }

    /** 유저 상태 수정 **/
    public void updateUserStatus(String email, String status){
        String query = "UPDATE user SET status = ? WHERE email = ?";

        this.jdbcTemplate.update(query, status, email);
    }


    // Check SQL
    /** 이메일 확인 **/
    public UserInfo checkUserEmail(String email) {
        String query = "SELECT u.user_id, sns_flag, store_name, u.status " +
                        "FROM user u " +
                        "LEFT JOIN store s ON u.user_id = s.user_id " +
                        "WHERE email = ?";

        try {
            return this.jdbcTemplate.queryForObject(query,
                    (rs, rowNum) -> new UserInfo(
                            rs.getInt("u.user_id"),
                            rs.getString("sns_flag"),
                            rs.getString("store_name"),
                            rs.getString("u.status"))
                    , email);
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

}
