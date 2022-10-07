package com.example.demo.src.review;

import com.example.demo.src.review.model.PostReviewReq;
import com.example.demo.src.review.model.PutReviewReq;
import com.example.demo.src.review.model.ReviewInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ReviewDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // Insert SQL
    /** 거래 후기 작성**/
    public int insertReview(int userId, int targetId, PostReviewReq reviewInfo){
        String query = "INSERT INTO review(reviewer_id, target_user_id, trade_id, rating, content) " +
                        "VALUES(?, ?, ?, ?, ?) ";

        Object[] queryParams = new Object[]{userId, targetId,reviewInfo.getTradeId(),
                                            reviewInfo.getRating(), reviewInfo.getContents()};

        return this.jdbcTemplate.update(query, queryParams);
    }

    //Select SQL
    /** 거래 후기 작성 가능한지 확인 **/
    public int selectReviewer(int userId, int tradeId){
        String query = "SELECT EXISTS (SELECT * FROM review " +
                "WHERE reviewer_id = ? AND trade_id = ?)";

        return this.jdbcTemplate.queryForObject(query, int.class, userId, tradeId);
    }

    /** 거래 후기 삭제가 가능한지 확인 **/
    public int selectDeleteReview(int userId, int reviewID){
        String query = "SELECT EXISTS (SELECT * FROM review " +
                "WHERE reviewer_id = ? AND review_id = ? AND status = 'Y')";

        return this.jdbcTemplate.queryForObject(query, int.class, userId, reviewID);
    }

    /** 거래 후기 조회 **/
    public List<ReviewInfo> selectReviews(int storeId, int reviewId, String date, int size){
        String query = "SELECT r.review_id, rating, content, store_name, r.created_at, t.product_id, name " +
                        "FROM review r " +
                        "INNER JOIN store s on s.user_id = r.reviewer_id " +
                        "INNER JOIN trade t on r.trade_id = t.trade_id " +
                        "LEFT JOIN product p on t.product_id = p.product_id " +
                        "WHERE (target_user_id = ? AND r.status = 'Y') " +
                        "AND ((r.created_at = ? AND review_id > ?) " +
                        "OR r.created_at < ?) " +
                        "ORDER BY r.created_at desc , review_id " +
                        "LIMIT ?";

        Object[] reviewsParams = new Object[]{storeId, date, reviewId, date, size + 1};

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new ReviewInfo(
                        rs.getInt("review_id"),
                        rs.getFloat("rating"),
                        rs.getString("content"),
                        rs.getString("store_name"),
                        rs.getString("r.created_at"),
                        rs.getInt("t.product_id"),
                        rs.getString("name")),
                reviewsParams);
    }

    /** 거래 후기 집계 **/
    public int selectReviewCount(int storeId){
        String query = "SELECT COUNT(*) AS reviewCount FROM review " +
                        "WHERE target_user_id = ? AND status = 'Y'";

        return this.jdbcTemplate.queryForObject(query, int.class, storeId);
    }

    // Update SQL
    /**거래 후기 삭제 **/
    public int deleteReview(int userId, int reviewId) {
        String query = "UPDATE review SET status = 'N' " +
                        "WHERE reviewer_id = ? AND review_id = ?";

        return this.jdbcTemplate.update(query, userId, reviewId);
    }

    /**거래 후기 수정 **/
    public int updateReview(int userId, int reviewId, PutReviewReq reviewInfo) {
        String query = "UPDATE review SET rating = ?, content = ? " +
                "WHERE reviewer_id = ? AND review_id = ?";

        Object[] queryParams = new Object[]{reviewInfo.getRating(), reviewInfo.getContents(),
                                            userId, reviewId};

        return this.jdbcTemplate.update(query, queryParams);
    }


}
