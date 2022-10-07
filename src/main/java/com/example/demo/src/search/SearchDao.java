package com.example.demo.src.search;

import com.example.demo.src.search.model.SearchProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;


@Repository
public class SearchDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /** 카테고리별 상품 목록 조회 **/
    public List<SearchProduct> getSearchByCategory(int userId, int categoryId, String safePayment, Integer priceFrom, Integer priceTo, String soldOutIncluded,
                                                   String shippingFeeIncluded, String usded, String exchange, String sort, String keyword) {

        String query = "SELECT p.product_id as product_id, user_id, url as image,\n" +
                "       CASE\n" +
                "           WHEN w.status ='Y' THEN 'Y'\n" +
                "           ELSE 'N'\n" +
                "        END as wish,\n" +
                "    price, name, p.updated_at as updated_at, safe_payment_flag, p.status as status\n" +
                "FROM product p\n" +
                "         LEFT JOIN(SELECT product_id, status\n" +
                "                   FROM wish\n" +
                "                   WHERE user_id = ?) w on p.product_id = w.product_id\n" +
                "         LEFT JOIN(SELECT url, MIN(product_image_id), product_id\n" +
                "                   FROM product_image\n" +
                "                   GROUP BY product_id) pi on p.product_id = pi.product_id\n" +
                "WHERE category_id = ?\n" +
                "   OR category_id in (SELECT category_id FROM category WHERE parent_id = ?)";
        Object[] params = new Object[]{userId, categoryId, categoryId};

        if (safePayment!=null) {
            query+="\nAND safe_payment_flag='Y'";
        }

        if (priceFrom != null) {
            query+="\nAND price>="+priceFrom;
        }
        if (priceTo != null) {
            query+="\nAND price<="+priceTo;
        }

        if (soldOutIncluded.equals("N")) {
            query+="\nAND p.status in ('S','R')";
        }else{
            query+="\nAND p.status in ('S','R','F')";
        }

        if (shippingFeeIncluded != null) {
            query+="\nAND shipping_fee_included_flag="+shippingFeeIncluded;
        }

        if (usded != null) {
            query+="\nAND used_flag="+usded;
        }

        if (exchange != null) {
            query+="\nAND exchange_flag="+exchange;
        }

        if (keyword != null) {
            query+="\nAND name like '%"+keyword+"%'";
        }

        if (sort.equals("latest")) {
            //최신순
            query+="\nORDER BY p.updated_at DESC";
        } else if (sort.equals("price-desc")) {
            //가격 높은순
            query+="\nORDER BY price DESC";
        } else {
            //가격 낮은순
            query+="\nORDER BY price ASC";
        }

        return this.jdbcTemplate.query(query,
                (rs,rowNum)-> new SearchProduct(
                        rs.getInt("product_id"),
                        rs.getInt("user_id"),
                        rs.getString("image"),
                        rs.getString("wish"),
                        rs.getInt("price"),
                        rs.getString("name"),
                        rs.getString("updated_at"),
                        rs.getString("safe_payment_flag"),
                        rs.getString("status")
                )
                ,params);
    }

}
