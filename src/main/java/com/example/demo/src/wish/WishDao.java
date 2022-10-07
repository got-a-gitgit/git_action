package com.example.demo.src.wish;


import com.example.demo.src.wish.model.WishInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;


@Repository
public class WishDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /** 관심상품 등록 **/
    public void createWish(int userId, int productId) {
        String query = "INSERT INTO wish(user_id,product_id) VALUES(?, ?) ON DUPLICATE KEY UPDATE status= 'Y'";
        Object[] params = new Object[]{userId, productId};
        this.jdbcTemplate.update(query, params);
    }

    /** 관심상품 삭제 **/
    public void deleteWish(int userId, int productId) {
        String query = "UPDATE wish SET status = 'N' WHERE user_id=? AND product_id=? AND status='Y'";
        Object[] params = new Object[]{userId, productId};
        this.jdbcTemplate.update(query, params);
    }

    /** 관심상품 목록 조회 **/
    public List<WishInfo> selectWishes(int userId, int productId, String date, int size) {
        String query = "SELECT p.product_id, url, name, price, safe_payment_flag, wish.updated_at, profile_image_url, store_name, p.updated_at " +
                "FROM wish " +
                "INNER JOIN product p on wish.product_id = p.product_id " +
                "INNER JOIN product_image pi on wish.product_id = pi.product_id " +
                "INNER JOIN store s on p.user_id = s.user_id " +
                "WHERE (wish.user_id = ? AND wish.status = 'Y') " +
                "AND ((wish.updated_at = ? AND p.product_id > ?) " +
                "OR wish.updated_at < ?) " +
                "ORDER BY wish.updated_at desc, product_id " +
                "LIMIT ?";

        Object[] wishesParams = new Object[]{userId, date, productId, date, size + 1};

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new WishInfo(
                        rs.getInt("p.product_id"),
                        rs.getString("url"),
                        rs.getString("name"),
                        rs.getInt("price"),
                        rs.getString("safe_payment_flag"),
                        rs.getString("wish.updated_at"),
                        rs.getString("profile_image_url"),
                        rs.getString("store_name"),
                        rs.getString("p.updated_at")),
                wishesParams);

    }
}
