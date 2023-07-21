package com.liao.niso.manager;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author liaoguixin
 * @data 2023/7/10
 */
// 分表存储
@Component
public class DynamicTableCreator {
    @Resource
    private JdbcTemplate jdbcTemplate;

    public void createTable(String tableName, String[] columns) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
        for (String column : columns) {
            sql.append(column).append(" VARCHAR(255), ");
        }
        sql.delete(sql.length() - 2, sql.length() - 1);
        sql.append(")").append("DEFAULT CHARACTER SET utf8");

        jdbcTemplate.execute(sql.toString());
    }

    public void insertData(String tableName, String[] columns, Object[] values) {

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(tableName).append("(");
        for (String column: columns) {
            sql.append(column).append(",");
        }
        sql.delete(sql.length() - 1, sql.length());
        sql.append(")").append(" VALUES (");
        for (Object value : values) {
            sql.append("'").append(value).append("', ");
        }
        sql.delete(sql.length() - 2, sql.length() - 1);
        sql.append(")");

        jdbcTemplate.execute(sql.toString());
    }

    public void deleteTable(String tableName) {
        jdbcTemplate.execute("drop table " + tableName);
    }

    public void updateTable(String tableName, String[] tableDatas) {
        jdbcTemplate.execute("drop table " + tableName);

        saveChildChart(tableName, tableDatas);
    }

    public void saveChildChart(String tableName, String[] tableDatas) {
        String[] columns = tableDatas[0].split(",");
        createTable(tableName, columns);

        for (int i = 1; i < tableDatas.length; i++) {
            String[] values = tableDatas[i].split(",");
            insertData(tableName, columns, values);
        }
    }
}
