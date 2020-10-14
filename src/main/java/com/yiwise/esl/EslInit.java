package com.yiwise.esl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

@Component
public class EslInit implements ApplicationContextAware {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private EslOutBound eslOutBound;

    @PostConstruct
    public void init() {
        logger.info("====初始化esl服务:begin");
        try {
            eslOutBound.createOutboundServer();
        }catch (Exception e ){
            logger.error("开启批量外呼esl", e);
        }
        logger.info("====初始化esl服务:end");
    }

    @PreDestroy
    public void preDestroy() {
        logger.info("====销毁esl服务，begin");
        // 当前环境是否是本地环境
        try {
            eslOutBound.destoryOutboundServer();
        } catch (Exception e) {
            logger.error("关闭批量外呼esl", e);
        }
        logger.info("====销毁esl服务，end");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {}
}
