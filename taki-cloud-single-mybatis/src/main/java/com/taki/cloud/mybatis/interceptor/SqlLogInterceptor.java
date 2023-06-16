package com.taki.cloud.mybatis.interceptor;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.SystemClock;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.util.*;

/**
 * @ClassName SqlLogInterceptor
 * @Description TODO
 * @Author Long
 * @Date 2023/6/16 15:09
 * @Version 1.0
 */
@Slf4j
public class SqlLogInterceptor implements Interceptor {

    private static final String DRUID_POOLED_PREPARED_STATEMENT = "com.alibaba.druid.pool.DruidPooledPreparedStatement";

    private Method druidGetSqlMethod;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Statement statement;
        Object firstArg = invocation.getArgs()[0];

        if (Proxy.isProxyClass(firstArg.getClass())) {
            statement = (Statement) SystemMetaObject.forObject(firstArg).getValue("h.statement");

        } else {
            statement = (Statement) firstArg;
        }

        MetaObject metaObject = SystemMetaObject.forObject(statement);

        try {
            statement = (Statement) metaObject.getValue("stmt.statement");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (metaObject.hasGetter("delegate")) {

            try {
                statement = (Statement) metaObject.getValue("delegate");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String originalSql = null;
        String stmetClassName = statement.getClass().getName();
        if (DRUID_POOLED_PREPARED_STATEMENT.equals(stmetClassName)) {

            try {
                if (druidGetSqlMethod == null) {
                    Class<?> clazz = Class.forName(DRUID_POOLED_PREPARED_STATEMENT);
                    druidGetSqlMethod = clazz.getMethod("getSql");
                }
                Object stmtSql = druidGetSqlMethod.invoke(statement);

                if (stmtSql instanceof String) {
                    originalSql = (String) stmtSql;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (originalSql == null) {
            originalSql = statement.toString();
        }
        originalSql = originalSql.replaceAll("[\\s]+", StringPool.SPACE);

        int index = indexOfSqlStart(originalSql);

        if (index > 0) {
            originalSql = originalSql.substring(index);
        }

        //计算执行sql耗时
        long start = SystemClock.now();
        Object result = invocation.proceed();
        long timing = SystemClock.now() - start;


        // sql 打印结果
        Object target = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject1 = SystemMetaObject.forObject(target);
        MappedStatement ms = (MappedStatement) metaObject1.getValue("delegate.mappedStatement");

        // 打印 sql
        String sqlLogger = "\n\n==============  Sql Start  ==============" +
                "\nExecute ID  ：{}" +
                "\nExecute SQL ：{}" +
                "\nExecute Time：{} ms" +
                "\n==============  Sql  End   ==============\n";
        log.info(sqlLogger, ms.getId(), originalSql, timing);

        return result;
    }

    /***
     * @description: 获取 sql 语句开头
     * @param sql
     * @return int
     * @author Long
     * @date: 2023/6/16 16:54
     */
    private int indexOfSqlStart(String sql) {

        String updateCaseSql = sql.toUpperCase();
        Set<Integer> set = new HashSet<>();
        set.add(updateCaseSql.indexOf("SELECT "));
        set.add(updateCaseSql.indexOf("UPDATE "));
        set.add(updateCaseSql.indexOf("INSERT "));
        set.add(updateCaseSql.indexOf("DELETE "));

        set.remove(-1);
        if (CollectionUtils.isEmpty(set)) {
            return -1;
        }
        List<Integer> list = new ArrayList<>(set);
        list.sort(Comparator.naturalOrder());

        return list.get(0);
    }
}
