package com.example.demo.src.event;

import com.example.demo.src.event.model.GetEventListRes;
import com.example.demo.src.event.model.GetNoticeRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class EventDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /** 홈 화면 이벤트 목록 조회 **/
    public List<GetEventListRes> getEventList() {
        String query = "SELECT event_id, event_image, url\n" +
                "FROM event\n" +
                "WHERE status = 'Y' AND type = '배너'";
        return jdbcTemplate.query(query,
                (rs,rowNum)->new GetEventListRes(
                        rs.getInt("event_id"),
                        rs.getString("event_image"),
                        rs.getString("url")
                ));
    }

    /** 공지 목록 조회 **/
    public List<GetNoticeRes> selectNotices() {
        String query = "SELECT event_id, type, contents, url " +
                "FROM event " +
                "WHERE status = 'Y' AND type <> '배너'" ;
        return jdbcTemplate.query(query,
                (rs,rowNum)->new GetNoticeRes(
                        rs.getInt("event_id"),
                        rs.getString("type"),
                        rs.getString("contents"),
                        rs.getString("url")
                ));
    }
}
