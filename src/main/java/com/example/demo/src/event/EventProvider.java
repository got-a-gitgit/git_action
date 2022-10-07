package com.example.demo.src.event;

import com.example.demo.config.BaseException;
import com.example.demo.src.event.model.GetEventListRes;
import com.example.demo.src.event.model.GetNoticeRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class EventProvider {
    private final EventDao eventDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public EventProvider(EventDao eventDao, JwtService jwtService) {
        this.eventDao = eventDao;
        this.jwtService = jwtService;
    }

    /** Event list 조회 **/
    public List<GetEventListRes> getEventList() throws BaseException {
        try {
            //홈 화면 이벤트 리스트 조회
            return eventDao.getEventList();
        } catch (Exception exception) {
            logger.error("GetEventList Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 공지 목록 조회 **/
    public List<GetNoticeRes> getNotices() throws BaseException {
        try {
            // 공지 목록 조회
            return eventDao.selectNotices();
        } catch (Exception exception) {
            logger.error("GetNotices Error", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
