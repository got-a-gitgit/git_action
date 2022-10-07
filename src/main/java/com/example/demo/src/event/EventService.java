package com.example.demo.src.event;


import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
@Service
public class EventService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EventDao eventDao;
    private final EventProvider eventProvider;
    private final JwtService jwtService;


    @Autowired
    public EventService(EventDao eventDao, EventProvider eventProvider, JwtService jwtService) {
        this.eventDao = eventDao;
        this.eventProvider = eventProvider;
        this.jwtService = jwtService;
    }
}
