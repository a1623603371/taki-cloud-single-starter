package com.taki.cloud.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * @ClassName TakiMetaObjectHandler
 * @Description TODO
 * @Author Long
 * @Date 2023/6/16 15:05
 * @Version 1.0
 */
public class TakiMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime nowTime = LocalDateTime.now();

        this.strictInsertFill(metaObject,"createTime",LocalDateTime.class,nowTime);
        this.strictInsertFill(metaObject,"updateTime",LocalDateTime.class,nowTime);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime nowTime = LocalDateTime.now();
        this.strictUpdateFill(metaObject,"updateTime",LocalDateTime.class,nowTime);

    }
}
