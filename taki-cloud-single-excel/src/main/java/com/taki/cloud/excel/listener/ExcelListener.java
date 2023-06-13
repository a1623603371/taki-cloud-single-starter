package com.taki.cloud.excel.listener;

import cn.afterturn.easypoi.cache.manager.POICacheManager;
import com.taki.cloud.excel.cover.TakiFileLoader;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * @ClassName ExcelListener
 * @Description TODO
 * @Author Long
 * @Date 2023/6/13 14:47
 * @Version 1.0
 */
public class ExcelListener implements ApplicationListener<ApplicationEvent> {
    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        POICacheManager.setFileLoader(new TakiFileLoader());
    }
}
