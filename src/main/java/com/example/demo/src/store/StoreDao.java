package com.example.demo.src.store;

import com.example.demo.src.store.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class StoreDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    // Insert SQL
    /** 상점명 삽입 **/
    public int insertStoreName(int userId, String name) {
        String query = "INSERT INTO store(user_id, store_name) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE user_id = ?, store_name = ?";

        return this.jdbcTemplate.update(query, userId, name, userId, name);
    }

    /** 팔로우 상태 수정**/
    public String insertFollowing(int userId, int followId){
        String query = "INSERT INTO follow(followee, follower, status) VALUES(?, ?, 'Y') " +
                        "ON DUPLICATE KEY UPDATE status = CASE " +
                        "WHEN status = 'Y' THEN 'N' " +
                        "WHEN status = 'N' THEN 'Y' " +
                        "END";
        this.jdbcTemplate.update(query, userId, followId);  // 팔로우 관계 수정

        String resultQuery = "SELECT status FROM follow WHERE followee = ? and follower = ?";

        return this.jdbcTemplate.queryForObject(resultQuery, String.class, userId, followId); // 결과 반환
    }

    /** 계좌 등록 **/
    public int insertAccount(int userId, PostAccountReq accountInfo){
        String query = "INSERT INTO account(user_id, account_holder, bank_id, account_number, default_flag) " +
                        "VALUES (?, ?, ?, ?, ? )";
        Object[] queryParams = new Object[]{userId, accountInfo.getName(), accountInfo.getBankId(),
                                accountInfo.getAccountNumber(), accountInfo.getDefaultFlag()};

        this.jdbcTemplate.update(query, queryParams);

        String resultQuery = "SELECT last_insert_id()";

        return this.jdbcTemplate.queryForObject(resultQuery, int.class); // 결과 반환
    }

    // Select SQL
    /** 유저 식별번호 확인 **/
    public int selectUserId(int userId){
        String query = "SELECT EXISTS (SELECT user_id FROM user WHERE user_id = ? AND status = 'Y')";

        return this.jdbcTemplate.queryForObject(query, int.class, userId);
    }

    /** 상점명 중복 확인**/
    public int selectStoreName(int userId, String storeName){
        String query = "SELECT EXISTS (SELECT store_name FROM store WHERE store_name = ? AND user_id <> ?)";

        return this.jdbcTemplate.queryForObject(query, int.class, storeName, userId);
    }

    /** 상점 프로필 조회 **/
    public PatchStoreProfileRes selectStoreProfile(int userId){
        String query = "SELECT profile_image_url, store_name, description FROM store WHERE user_id = ?";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new PatchStoreProfileRes(
                        rs.getString("profile_image_url"),
                        rs.getString("store_name"),
                        rs.getString("description")),
                userId);
    }

    /** 상점 정보 전체 조회 **/
    public GetStoreInfoRes selectStoreInfo(int userId) {
        String query = "SELECT store_name, profile_image_url, description, s.created_at, authentication_flag, " +
                        "ROUND(AVG(rating), 1) AS rating, trade, follower, followee " +
                        "FROM store s " +
                        "LEFT JOIN review r ON s.user_id = r.target_user_id " +
                        "LEFT JOIN (SELECT seller_id, COUNT(seller_id) AS trade " +
                        "FROM trade WHERE status = 'F' GROUP BY seller_id) trade_tb ON s.user_id = trade_tb.seller_id " +
                        "LEFT JOIN (SELECT follower AS f, COUNT(follower) AS follower " +
                        "FROM follow GROUP BY follower) follower_tb ON s.user_id = follower_tb.f " +
                        "LEFT JOIN (SELECT followee AS f,COUNT(followee) AS followee " +
                        "FROM follow GROUP BY followee) followee_tb ON s.user_id = followee_tb.f " +
                        "WHERE user_id = ?";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new GetStoreInfoRes(
                        rs.getString("store_name"),
                        rs.getString("profile_image_url"),
                        rs.getString("description"),
                        rs.getFloat("rating"),
                        rs.getInt("trade"),
                        rs.getInt("follower"),
                        rs.getInt("followee"),
                        rs.getString("s.created_at"),
                        rs.getString("authentication_flag")),
                userId);
    }

    /** 상점 거래내역(판매) 조회 **/
    public List<TradeInfo> selectSales(int userId, String type){
        String query = "SELECT trade_id, url, name, price, store_name, t.created_at, t.status " +
                "FROM trade t " +
                "INNER JOIN product p ON t.product_id = p.product_id " +
                "INNER JOIN product_image pi ON p.product_id = pi.product_id " +
                "INNER JOIN store s ON t.seller_id = s.user_id " +
                "WHERE (seller_id = ? AND t.status = " + type + ") " +
                "GROUP BY trade_id, t.created_at " +
                "ORDER BY t.created_at DESC";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new TradeInfo(
                        rs.getInt("trade_id"),
                        rs.getString("url"),
                        rs.getString("name"),
                        rs.getInt("price"),
                        rs.getString("store_name"),
                        rs.getString("t.created_at"),
                        rs.getString("t.status")),
                userId);
    }

    /** 상점 거래내역(구매) 조회 **/
    public List<TradeInfo> selectPurchases(int userId, String type){
        String query = "SELECT trade_id, url, name, price, store_name, t.created_at, t.status " +
                "FROM trade t " +
                "INNER JOIN product p ON t.product_id = p.product_id " +
                "INNER JOIN product_image pi ON p.product_id = pi.product_id " +
                "INNER JOIN store s ON t.seller_id = s.user_id " +
                "WHERE (buyer_id = ? AND t.status = " + type + ") " +
                "GROUP BY trade_id, t.created_at " +
                "ORDER BY t.created_at DESC";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new TradeInfo(
                        rs.getInt("trade_id"),
                        rs.getString("url"),
                        rs.getString("name"),
                        rs.getInt("price"),
                        rs.getString("store_name"),
                        rs.getString("t.created_at"),
                        rs.getString("t.status")),
                userId);
    }

    /** 팔로잉 관계 확인 **/
    public int selectIsFollwing(int userId, int followId){
        String query = "SELECT EXISTS (SELECT followee FROM follow " +
                        "WHERE followee = ? AND follower = ? AND status = 'Y')";

        return this.jdbcTemplate.queryForObject(query, int.class, userId, followId);
    }


    /** 팔로워 목록 조회 **/
    public List<followInfo> selectFollowers(int storeId, int lastId, int size){
        String query = "SELECT s.user_id, profile_image_url, store_name, alarm_flag, IFNULL(followers, 0) AS followers, IFNULL(products, 0) AS products " +
                        "FROM store s " +
                        "LEFT JOIN (SELECT follower, alarm_flag, COUNT(IF(follower IS NULL, 'NULL', follower)) AS followers, updated_at  FROM follow GROUP BY follower) followers_tb " +
                        "    ON s.user_id = followers_tb.follower " +
                        "LEFT JOIN (SELECT user_id, COUNT(product_id) AS products FROM product GROUP BY user_id) products_tb " +
                        "    ON s.user_id = products_tb.user_id " +
                        "WHERE s.user_id IN (SELECT followee FROM follow origin WHERE origin.follower = ? AND origin.status ='Y') " +
                        "AND s.user_id > ? " +
                        "ORDER BY user_id " +
                        "LIMIT ?";


        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new followInfo(
                        rs.getInt("s.user_id"),
                        rs.getString("profile_image_url"),
                        rs.getString("store_name"),
                        rs.getString("alarm_flag"),
                        rs.getInt("followers"),
                        rs.getInt("products")),
                storeId, lastId, size + 1);
    }

    /** 팔로잉 목록 조회 **/
    public List<followInfo> selectFollowings(int storeId, int lastId, int size){
        String query = "SELECT s.user_id, profile_image_url, store_name, alarm_flag, IFNULL(followers, 0) AS followers, IFNULL(products, 0) AS products " +
                "FROM store s " +
                "LEFT JOIN (SELECT follower, alarm_flag, COUNT(IF(follower IS NULL, 'NULL', follower)) AS followers, updated_at FROM follow GROUP BY follower) followers_tb " +
                "    ON s.user_id = followers_tb.follower " +
                "LEFT JOIN (SELECT user_id, COUNT(product_id) AS products FROM product GROUP BY user_id) products_tb " +
                "    ON s.user_id = products_tb.user_id " +
                "WHERE s.user_id IN (SELECT follower FROM follow origin WHERE origin.followee = ? AND origin.status ='Y') " +
                "AND s.user_id > ? " +
                "ORDER BY user_id " +
                "LIMIT ?";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new followInfo(
                        rs.getInt("s.user_id"),
                        rs.getString("profile_image_url"),
                        rs.getString("store_name"),
                        rs.getString("alarm_flag"),
                        rs.getInt("followers"),
                        rs.getInt("products")),
                storeId, lastId, size + 1);
    }

    /** 상점 거래내역(판매) 조회 **/
    public List<TradeInfo> selectSales(int userId, int tradeId, String date, int size){
        String query = "SELECT trade_id, url, name, price, store_name, t.created_at, t.status " +
                "FROM trade t " +
                "INNER JOIN product p ON t.product_id = p.product_id " +
                "INNER JOIN product_image pi ON p.product_id = pi.product_id " +
                "INNER JOIN store s ON t.seller_id = s.user_id " +
                "WHERE seller_id = ? " +
                "AND ((t.created_at = ? AND trade_id > ?) " +
                "OR t.created_at < ?) " +
                "GROUP BY trade_id, t.created_at " +
                "ORDER BY t.created_at DESC , trade_id " +
                "LIMIT ?";

        Object[] queryParams = new Object[]{userId, date, tradeId, date, size + 1};

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new TradeInfo(
                        rs.getInt("trade_id"),
                        rs.getString("url"),
                        rs.getString("name"),
                        rs.getInt("price"),
                        rs.getString("store_name"),
                        rs.getString("t.created_at"),
                        rs.getString("t.status")),
                queryParams);
    }

    /** 상점별 판매목록 3개까지 조회 **/
    public List<ProductInfo> selectProductsByStore(int userId){
        String query = "SELECT user_id, p.product_id, pi.url, price " +
                        "FROM product p " +
                        "INNER JOIN product_image pi ON p.product_id = pi.product_id " +
                        "WHERE user_id = ? AND p.status= 'S' LIMIT 3";
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new ProductInfo(
                        rs.getInt("p.product_id"),
                        rs.getString("pi.url"),
                        rs.getInt("price")
                ), userId);
    }

    /** 계좌 중복 확인 **/
    public int selectDuplicatedAccount(int bankId, String accountNumber){
        String query = "SELECT EXISTS( " +
                        "SELECT account_number FROM account " +
                        "WHERE bank_id = ? AND account_number = ?)";
        return this.jdbcTemplate.queryForObject(query, int.class, bankId, accountNumber);
    }

    /** 최대 계좌(2개) 등록 여부 확인 **/
    public int selectMaxAccount(int userId){
        String query = "SELECT COUNT(user_id) FROM account " +
                         "WHERE user_id = ?";
        return this.jdbcTemplate.queryForObject(query, int.class, userId);
    }

    /** 기본 계좌 ID 조회 **/
    public int selectMainAccount(int userId){
        String query = "SELECT account_id FROM account " +
                        "WHERE user_id =? AND default_flag = 'Y'";
        return this.jdbcTemplate.queryForObject(query, int.class, userId);
    }

    /** 일반 계좌 ID 조회 **/
    public int selectNotMainAccount(int userId){
        String query = "SELECT account_id FROM account " +
                "WHERE user_id =? AND default_flag = 'N'";
        return this.jdbcTemplate.queryForObject(query, int.class, userId);
    }

    /** 계좌 존재 확인 **/
    public int selectAccountExist(int userId, int accountId){
        String query = "SELECT EXISTS(SELECT * FROM account WHERE user_id = ? AND account_id = ?)";

        return this.jdbcTemplate.queryForObject(query, int.class, userId, accountId);

    }

    /** 계좌 번호 확인 **/
    public String selectAccountNumber(int accountId){
        String query = "SELECT account_number FROM account " +
                        "WHERE account_id = ?";
        return this.jdbcTemplate.queryForObject(query, String.class, accountId);
    }

    /** 계좌 목록 조회 **/
    public List<AccountInfo> selectAccounts(int userId){
        String query = "SELECT account_id, default_flag, account.bank_id, bank_logo_url, name, account_number, account_holder " +
                        "FROM account " +
                        "INNER JOIN bank_type bt ON account.bank_id = bt.bank_id " +
                        "WHERE user_id = ? ORDER BY account.created_at";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new AccountInfo(
                        rs.getInt("account_id"),
                        rs.getString("default_flag"),
                        rs.getInt("account.bank_id"),
                        rs.getString("bank_logo_url"),
                        rs.getString("name"),
                        rs.getString("account_number"),
                        rs.getString("account_holder")),
                userId);
    }

    /** 본인인증 정보 조회 **/
    public GetAuthenticationRes selectUserAuthentication(int storeId){
        String query = "SELECT profile_image_url, store_name, authentication_flag, CONCAT('*',MID(account_holder,2,1),'*') AS name " +
                        "FROM store " +
                        "LEFT JOIN account a ON store.user_id = a.user_id " +
                        "WHERE store.user_id = ? " +
                        "LIMIT 1";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new GetAuthenticationRes(
                        rs.getString("profile_image_url"),
                        rs.getString("store_name"),
                        rs.getString("authentication_flag"),
                        rs.getString("name")),
                storeId);
    }

    // Update SQL
    /** 상점 소개 수정 **/
    public int updateStoreProfile(int userId, PatchStoreProfileReq storeProfile){
        String query = "UPDATE store " +
                       "SET profile_image_url = ?, store_name = ?, description = ? " +
                       "WHERE user_id = ? AND status = 'Y'";

        Object[] storeProfileParams = new Object[]{storeProfile.getOriginImageUrl(),
            storeProfile.getStoreName(), storeProfile.getDescription(), userId};

        return this.jdbcTemplate.update(query, storeProfileParams);
    }

    /** 팔로잉 알람 설정 수정**/
    public String updateFollowingNotification(int userId, int followId){
        String query = "UPDATE follow SET alarm_flag = CASE " +
                        "WHEN alarm_flag = 'Y' " +
                        "THEN 'N' ELSE 'Y' END " +
                        "WHERE followee = ? AND follower = ? AND status = 'Y'";

        this.jdbcTemplate.update(query, userId, followId);  // 알람 설정 변경

        String resultQuery = "SELECT alarm_flag FROM follow WHERE followee = ? and follower = ?";

        return this.jdbcTemplate.queryForObject(resultQuery, String.class, userId, followId); // 결과 반환
    }

    /** 기본 결제 계좌 변경 **/
    public void updateAccountDefaultValue(int accountId, String isMain){
        String query = "UPDATE account SET default_flag = ? " +
                        "WHERE account_id = ?";

        this.jdbcTemplate.update(query, isMain, accountId);
    }

    /** 계좌 수정 **/
    public int updateAccount(int accountId, PostAccountReq accountInfo){
        String query = "UPDATE account " +
                        "SET account_holder = ?, bank_id = ?, account_number = ?, default_flag = ? " +
                        "WHERE account_id = ?";

        Object[] queryParams = new Object[]{accountInfo.getName(), accountInfo.getBankId(),
                accountInfo.getAccountNumber(), accountInfo.getDefaultFlag(), accountId};

        return this.jdbcTemplate.update(query, queryParams);
    }

    // Delete SQL
    /** 계좌 삭제 **/
    public int deleteAccount(int accountId){
        String query = "DELETE FROM account WHERE account_id = ?";

        return this.jdbcTemplate.update(query, accountId);
    }

}

    // 페이징 처리
//    /** 상점 거래내역(판매) 조회 **/
//    public List<TradeInfo> selectSales(int userId, int tradeId, String date, int size){
//        String query = "SELECT trade_id, url, name, price, store_name, t.created_at, t.status " +
//                "FROM trade t " +
//                "INNER JOIN product p ON t.product_id = p.product_id " +
//                "INNER JOIN product_image pi ON p.product_id = pi.product_id " +
//                "INNER JOIN store s ON t.seller_id = s.user_id " +
//                "WHERE seller_id = ? " +
//                "AND ((t.created_at = ? AND trade_id > ?) " +
//                "OR t.created_at < ?) " +
//                "GROUP BY trade_id, t.created_at " +
//                "ORDER BY t.created_at DESC , trade_id " +
//                "LIMIT ?";
//
//        Object[] queryParams = new Object[]{userId, date, tradeId, date, size + 1};
//
//        return this.jdbcTemplate.query(query,
//                (rs, rowNum) -> new TradeInfo(
//                        rs.getInt("trade_id"),
//                        rs.getString("url"),
//                        rs.getString("name"),
//                        rs.getInt("price"),
//                        rs.getString("store_name"),
//                        rs.getString("t.created_at"),
//                        rs.getString("t.status")),
//                queryParams);
//    }

