package com.example.demo.src.product;


import com.example.demo.config.BaseException;
import com.example.demo.src.product.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Repository
public class ProductDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**상품 등록**/
    public int createProduct(PostProductReq postProductReq){
        String createProductQuery = "insert into product("+
                "name, " +
                "user_id, " +
                "price, " +
                "category_id, " +
                "shipping_fee_included_flag, " +
                "location, " +
                "amount, " +
                "used_flag, " +
                "safe_payment_flag, " +
                "exchange_flag, " +
                "contents) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        Object[] createProductParams = new Object[]{
                postProductReq.getName(),
                postProductReq.getUserId(),
                postProductReq.getPrice(),
                postProductReq.getCategoryId(),
                postProductReq.getShippingFeeIncluded(),
                postProductReq.getLocation(),
                postProductReq.getAmount(),
                postProductReq.getUsed(),
                postProductReq.getSafePayment(),
                postProductReq.getExchange(),
                postProductReq.getContents()
        };

        this.jdbcTemplate.update(createProductQuery, createProductParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    /**태그 등록**/
    public List<Integer> createTags(List<String> tags) {
        String createTagsQuery = "INSERT IGNORE INTO tag(name) value (?)";
        List<Integer> tagIds = new ArrayList<>();

        for (int i = 0; i < tags.size(); i++) {
            String tag=tags.get(i);
            int rowsAffected = this.jdbcTemplate.update(createTagsQuery, tag);

            //tagId 반환
            if (rowsAffected == 0) {
                String getTagIdQuery = "SELECT tag_id FROM tag WHERE name = ?";
                tagIds.add(this.jdbcTemplate.queryForObject(getTagIdQuery, int.class, tag));
            } else {
                String lastInserIdQuery = "select last_insert_id()";
                tagIds.add(this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class));
            }
        }

        return tagIds;
    }

    /**태그와 상품 연결**/
    public int createProductTags(int productId, List<Integer> tagIds) {
        String query = "INSERT IGNORE INTO product_tag (product_id, tag_id) VALUES (?,?) ";
        return this.jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, productId);
                    ps.setInt(2, tagIds.get(i));
                }

                @Override
                public int getBatchSize() {
                    return tagIds.size();
                }
            }).length;

    }

    /**상품 이미지 등록**/
    public int createProductImages(int productId, List<String> productImages) {
        String query = "INSERT INTO product_image (product_id, url) VALUES (?,?) ";

        return this.jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, productId);
                ps.setString(2, productImages.get(i));
            }
            @Override
            public int getBatchSize() {
                return productImages.size();
            }
        }).length;

    }

    /**상품 삭제**/
    public int deleteProduct(int productId) {
        String query = "UPDATE product SET status = 'D' WHERE product_id=? AND status!='D'";
        return this.jdbcTemplate.update(query, productId);
    }

    /**상품 상세 조회**/
    public GetProductRes getProduct(int productId) throws BaseException {
        String query = "SELECT " +
                "p.product_id as product_id, " +
                "p.name as name, " +
                "p.user_id as user_id, " +
                "price, " +
                "p.category_id as category_id, " +
                "c.name as category_name, " +
                "shipping_fee_included_flag, " +
                "location, " +
                "amount, " +
                "used_flag, " +
                "safe_payment_flag, "+
                "exchange_flag, "+
                "contents, "+
                "view, "+
                "COUNT(wish_id) as wishes, "+
                "p.status as status, " +
                "c.created_at as created_at\n" +
                "FROM product p\n" +
                "JOIN category c on p.category_id = c.category_id\n" +
                "LEFT JOIN wish w on p.product_id = w.product_id\n" +
                "WHERE p.product_id = ? AND p.status!='D'\n " +
                "GROUP BY p.product_id";

        try {
            return this.jdbcTemplate.queryForObject(query,
                    (rs, rowNum) -> new GetProductRes(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getInt("user_id"),
                            rs.getInt("price"),
                            rs.getInt("category_id"),
                            rs.getString("category_name"),
                            rs.getString("shipping_fee_included_flag"),
                            rs.getString("location"),
                            rs.getInt("amount"),
                            rs.getString("used_flag"),
                            rs.getString("safe_payment_flag"),
                            rs.getString("exchange_flag"),
                            rs.getString("contents"),
                            rs.getInt("view"),
                            rs.getInt("wishes"),
                            rs.getString("status"),
                            rs.getString("created_at")
                    ),
                    productId);
        } catch (Exception exception) {
            throw new BaseException(NON_EXISTENT_PRODUCT);
        }
    }

    /**상품 이미지 조회**/
    public List<ProductImage> getProductImages(int productId) {
        String query = "SELECT product_image_id, url\n" +
                "FROM product p\n" +
                "JOIN product_image pi on p.product_id = pi.product_id\n" +
                "WHERE p.product_id = ? AND pi.status='Y'";

        return this.jdbcTemplate.query(query,
                (rs,rowNum)-> new ProductImage(
                        rs.getInt("product_image_id"),
                        rs.getString("url")
                ),productId);
    }

    /**상품 태그 조회**/
    public List<String> getProductTags(int productId) {
        String query = "SELECT t.name as tag\n" +
                "FROM product p\n" +
                "JOIN product_tag pt on p.product_id = pt.product_id\n" +
                "JOIN tag t on t.tag_id = pt.tag_id\n" +
                "WHERE p.product_id = ? AND pt.status='Y'";

        return this.jdbcTemplate.query(query,
                (rs,rowNum)-> new String(
                        rs.getString("tag")
                ),productId);
    }

    /**상품 조회수 증가**/
    public void increaseProductView(int productId) {
        String query = "UPDATE product SET view = view+1 WHERE product_id=?";
        this.jdbcTemplate.update(query, productId);
    }

    /**상점 상품목록 조회 with 무한스크롤**/
    public List<StoreProductRes> getProductListByStoreId(int userId, int storeId, String lastUpdatedAt, Integer lastProductId, Integer size) {
        String query = "SELECT p.product_id as product_id,\n" +
                "       p.user_id    as user_id,\n" +
                "       price,\n" +
                "       name,\n" +
                "       safe_payment_flag,\n" +
                "        CASE\n" +
                "            WHEN w.status ='Y' THEN 'Y'\n" +
                "            ELSE 'N'\n" +
                "        END as wish,\n" +
                "        pi.url as image,\n" +
                "        p.updated_at as updated_at,\n" +
                "        p.status as status\n" +
                "FROM product p\n" +
                "         LEFT JOIN (SELECT product_id, status\n" +
                "                    FROM wish\n" +
                "                    WHERE user_id = ?) w on p.product_id = w.product_id\n" +
                "         LEFT JOIN (SELECT url, MIN(product_image_id), product_id\n" +
                "               FROM product_image\n" +
                "               GROUP BY product_id) pi on p.product_id = pi.product_id\n" +
                "WHERE p.user_id = ? AND p.status!='D' AND (p.updated_at<? OR (p.updated_at=? AND p.product_id<?))\n"+
                "order by p.updated_at DESC, p.product_id DESC\n" +
                "LIMIT ?";

        Object[] params = new Object[]{userId, storeId, lastUpdatedAt, lastUpdatedAt,lastProductId, size};
        return this.jdbcTemplate.query(query,
                (rs,rowNum)->new StoreProductRes(
                    rs.getInt("product_id"),
                    rs.getInt("user_id"),
                    rs.getInt("price"),
                        rs.getString("image"),
                        rs.getString("name"),
                        rs.getString("safe_payment_flag"),
                        rs.getString("wish"),
                        rs.getString("updated_at"),
                        rs.getString("status")
                ),
                params);
    }

    /**상점 상품목록 첫번째 페이지 조회 with 무한스크롤**/
    public List<StoreProductRes> getFirstProductListByStoreId(int userId, int storeId, Integer size) {
        String query = "SELECT p.product_id as product_id,\n" +
                "       p.user_id    as user_id,\n" +
                "       price,\n" +
                "       name,\n" +
                "       safe_payment_flag,\n" +
                "        CASE\n" +
                "            WHEN w.status ='Y' THEN 'Y'\n" +
                "            ELSE 'N'\n" +
                "        END as wish,\n" +
                "        pi.url as image,\n" +
                "        p.updated_at as updated_at,\n" +
                "        p.status as status\n" +
                "FROM product p\n" +
                "         LEFT JOIN (SELECT product_id, status\n" +
                "                    FROM wish\n" +
                "                    WHERE user_id = ?) w on p.product_id = w.product_id\n" +
                "         LEFT JOIN (SELECT url, MIN(product_image_id), product_id\n" +
                "               FROM product_image\n" +
                "               GROUP BY product_id) pi on p.product_id = pi.product_id\n" +
                "WHERE p.user_id = ? AND p.status!='D'"+
                "order by p.created_at DESC, p.product_id DESC\n" +
                "LIMIT ?";

        Object[] params = new Object[]{userId, storeId, size};
        return this.jdbcTemplate.query(query,
                (rs,rowNum)->new StoreProductRes(
                        rs.getInt("product_id"),
                        rs.getInt("user_id"),
                        rs.getInt("price"),
                        rs.getString("image"),
                        rs.getString("name"),
                        rs.getString("safe_payment_flag"),
                        rs.getString("wish"),
                        rs.getString("updated_at"),
                        rs.getString("status")
                ),
                params);
    }

    /**상점 상품목록 조회 without 무한스크롤**/
    public List<StoreProductRes> getWholeProductListByStoreId(int userId, int storeId) {
        String query = "SELECT p.product_id as product_id,\n" +
                "       p.user_id    as user_id,\n" +
                "       price,\n" +
                "       name,\n" +
                "       safe_payment_flag,\n" +
                "        CASE\n" +
                "            WHEN w.status ='Y' THEN 'Y'\n" +
                "            ELSE 'N'\n" +
                "        END as wish,\n" +
                "        pi.url as image,\n" +
                "        p.updated_at as updated_at,\n" +
                "        p.status as status\n" +
                "FROM product p\n" +
                "         LEFT JOIN (SELECT product_id, status\n" +
                "                    FROM wish\n" +
                "                    WHERE user_id = ?) w on p.product_id = w.product_id\n" +
                "         LEFT JOIN (SELECT url, MIN(product_image_id), product_id\n" +
                "               FROM product_image\n" +
                "               GROUP BY product_id) pi on p.product_id = pi.product_id\n" +
                "WHERE p.user_id = ? AND p.status!='D'" +
                "order by p.created_at DESC, p.product_id DESC\n";

        Object[] params = new Object[]{userId, storeId};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new StoreProductRes(
                        rs.getInt("product_id"),
                        rs.getInt("user_id"),
                        rs.getInt("price"),
                        rs.getString("image"),
                        rs.getString("name"),
                        rs.getString("safe_payment_flag"),
                        rs.getString("wish"),
                        rs.getString("updated_at"),
                        rs.getString("status")
                ),
                params);
    }

    /**홈 화면 추천상품목록 첫 페이지 조회 with 무한스크롤**/
    public List<RecommendedProduct> getFirstProductList(int userId) {
        String query = "SELECT p.product_id as product_id, " +
                "user_id, " +
                "pi.url as image,\n" +
                "       CASE\n" +
                "           WHEN w.status ='Y' THEN 'Y'\n" +
                "           ELSE 'N'\n" +
                "        END as wish,\n" +
                "price, " +
                "name, " +
                "location, " +
                "p.updated_at as updated_at, " +
                "safe_payment_flag,\n" +
                "    CASE\n" +
                "        WHEN ISNULL(wc.wishes) THEN 0\n" +
                "        ELSE wc.wishes\n" +
                "    END as wishes,\n" +
                "p.status as status\n"+
                "FROM product p\n" +
                "         LEFT JOIN(SELECT product_id, status\n" +
                "                   FROM wish\n" +
                "                   WHERE user_id = ?) w on p.product_id = w.product_id\n" +
                "         LEFT JOIN(SELECT url, MIN(product_image_id), product_id\n" +
                "                   FROM product_image\n" +
                "                   GROUP BY product_id) pi on p.product_id = pi.product_id\n" +
                "         LEFT JOIN(SELECT product_id, COUNT(product_id) as wishes\n" +
                "                   FROM wish\n" +
                "                   GROUP BY product_id) as wc on p.product_id = wc.product_id\n" +
                "WHERE p.status!='D'"+
                "ORDER BY p.updated_at DESC, p.product_id DESC\n" +
                "LIMIT 21";

        return this.jdbcTemplate.query(query,
                (rs,rowNum)->new RecommendedProduct(
                        rs.getInt("product_id"),
                        rs.getInt("user_id"),
                        rs.getString("image"),
                        rs.getString("wish"),
                        rs.getInt("price"),
                        rs.getString("name"),
                        rs.getString("location"),
                        rs.getString("updated_at"),
                        rs.getString("safe_payment_flag"),
                        rs.getInt("wishes"),
                        rs.getString("status")
                ),
                userId);
    }

    /**홈 화면 추천상품목록 조회 with 무한스크롤**/
    public List<RecommendedProduct> getProductList(int userId, String lastUpdatedAt, Integer lastProductId) {
        String query = "SELECT p.product_id as product_id, " +
                "user_id, " +
                "pi.url as image,\n" +
                "       CASE\n" +
                "           WHEN w.status ='Y' THEN 'Y'\n" +
                "           ELSE 'N'\n" +
                "        END as wish,\n" +
                "price, " +
                "name, " +
                "location, " +
                "p.created_at as created_at, " +
                "safe_payment_flag,\n" +
                "    CASE\n" +
                "        WHEN ISNULL(wc.wishes) THEN 0\n" +
                "        ELSE wc.wishes\n" +
                "    END as wishes,\n" +
                "p.status as status\n"+
                "FROM product p\n" +
                "         LEFT JOIN(SELECT product_id, status\n" +
                "                   FROM wish\n" +
                "                   WHERE user_id = ?) w on p.product_id = w.product_id\n" +
                "         LEFT JOIN(SELECT url, MIN(product_image_id), product_id\n" +
                "                   FROM product_image\n" +
                "                   GROUP BY product_id) pi on p.product_id = pi.product_id\n" +
                "         LEFT JOIN(SELECT product_id, COUNT(product_id) as wishes\n" +
                "                   FROM wish\n" +
                "                   GROUP BY product_id) as wc on p.product_id = wc.product_id\n" +
                "WHERE p.status!='D' AND (p.updated_at<? OR (p.updated_at=? AND p.product_id>?))\n"+
                "ORDER BY p.updated_at DESC, p.product_id DESC\n" +
                "LIMIT 21";

        Object[] params = new Object[]{userId, lastUpdatedAt, lastUpdatedAt, lastProductId};

        return this.jdbcTemplate.query(query,
                (rs,rowNum)->new RecommendedProduct(
                        rs.getInt("product_id"),
                        rs.getInt("user_id"),
                        rs.getString("image"),
                        rs.getString("wish"),
                        rs.getInt("price"),
                        rs.getString("name"),
                        rs.getString("location"),
                        rs.getString("created_at"),
                        rs.getString("safe_payment_flag"),
                        rs.getInt("wishes"),
                        rs.getString("status")
                ),
                params);
    }

    /** 카테고리 리스트 조회 **/
    public List<Category> getCategoryList(int categoryId) {
        String query = "SELECT category_id, " +
                "name, " +
                "end_flag\n" +
                "FROM category\n" +
                "WHERE parent_id = ?";

            return this.jdbcTemplate.query(query,
                    (rs, rowNum) -> new Category(
                            rs.getInt("category_id"),
                            rs.getString("name"),
                            rs.getString("end_flag")
                    ),
                    categoryId);

    }

    public List<Category> getMainCategoryList() {
        String query = "SELECT category_id, " +
                "name, " +
                "end_flag\n" +
                "FROM category\n" +
                "WHERE parent_id IS NULL";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new Category(
                        rs.getInt("category_id"),
                        rs.getString("name"),
                        rs.getString("end_flag")
                ));

    }

    /** 상품 판매 상태 변경 **/
    public void updateStatus(int productId, String status) {
        String query = "UPDATE product SET status=? WHERE product_id=?";
        Object[] params = new Object[]{status, productId};

        this.jdbcTemplate.update(query, params);

    }

    /** 상품 찜한 사람 목록 조회 **/
    public List<GetWisherListRes> getWisherList(int productId) {
        String query = "SELECT w.user_id as user_id," +
                " s.profile_image_url as profile_image_url," +
                " store_name," +
                " w.updated_at as updated_at\n" +
                "FROM wish w\n" +
                "JOIN store s ON w.user_id = s.user_id\n" +
                "WHERE product_id = ? AND w.status='Y'";

        return this.jdbcTemplate.query(query,
                (rs,rowNum)->new GetWisherListRes(
                        rs.getInt("user_id"),
                        rs.getString("profile_image_url"),
                        rs.getString("store_name"),
                        rs.getString("updated_at")
                )
                ,productId);
    }

    /** 상품 수정 **/
    public void updateProduct(int productId, PutProductReq putProductReq) {
        String query = "UPDATE product\n" +
                "SET name=?,\n" +
                "    price=?,\n" +
                "    category_id=?,\n" +
                "    shipping_fee_included_flag=?,\n" +
                "    location=?,\n" +
                "    amount=?,\n" +
                "    used_flag=?,\n" +
                "    safe_payment_flag=?,\n" +
                "    exchange_flag=?,\n" +
                "    contents =?\n" +
                "WHERE product_id = ?";

        Object[] params = new Object[]{
                putProductReq.getName(),
                putProductReq.getPrice(),
                putProductReq.getCategoryId(),
                putProductReq.getShippingFeeIncluded(),
                putProductReq.getLocation(),
                putProductReq.getAmount(),
                putProductReq.getUsed(),
                putProductReq.getSafePayment(),
                putProductReq.getExchange(),
                putProductReq.getContents(),
                productId
        };

        this.jdbcTemplate.update(query,params);
    }

    /** 상품 이미지 삭제 **/
    public void deleteProductImage(List<String> deletedImageList) {
        String query ="UPDATE product_image SET status = 'N'\n";

        String imageIds = "";
        for (int i = 0; i < deletedImageList.size(); i++) {
            if (i != deletedImageList.size() - 1) {
                imageIds += deletedImageList.get(i) + ", ";
            } else {
                imageIds += deletedImageList.get(i);
            }
        }

        query+="WHERE product_image_id in ("+imageIds+")";

        this.jdbcTemplate.update(query);
    }

    /** 제거된 상품-태그 삭제 **/
    public int deleteProductTags(int productId, List<Integer> tagIds) {
        String query = "UPDATE product_tag SET status='N'\n" +
                "WHERE product_id =?"+
                "\nAND tag_id NOT IN (";


        for (int i = 0; i < tagIds.size(); i++) {
            if (i != tagIds.size() - 1) {
                query += tagIds.get(i) + ", ";
            } else {
                query += tagIds.get(i) + ")";
            }

        }

        return this.jdbcTemplate.update(query, productId);
    }

    /** 상품의 모든 태그 삭제 **/
    public int deleteAllProductTags(int productId) {
        String query = "UPDATE product_tag SET status='N'\n" +
                "WHERE product_id =?";

        return this.jdbcTemplate.update(query, productId);
    }
}
