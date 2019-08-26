package com.zijinph.base.auth.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlAppender {

    private static final String SQL_WHERE = "WHERE";

    private static final String SQL_LEFT_JOIN = "LEFT JOIN ";

    /**
     * 将数据权限的sql拼进原sql中
     *
     * @param originSql    原sql
     * @param privilegeSql 数据权限sql
     * @return String
     */
    public static String handlerSql(String originSql, String privilegeSql) {
        if (originSql.endsWith(";")) {
            originSql = originSql.substring(0, originSql.lastIndexOf(";"));
        }
        originSql = originSql.replace("\t", " ").replace("\n", " ");
        originSql = originSql.replaceAll(" {2,}", " ");
        originSql = originSql.replaceAll(" where ", " " + SQL_WHERE + " ");
        originSql = addWhere(originSql);
        originSql = originSql.replace("left", "LEFT");
        originSql = originSql.replace("join", "JOIN");
        originSql = originSql.replaceAll("LEFT[ ]+JOIN[ ]+", SQL_LEFT_JOIN);

//        List<String> matcherList = matcherTableSql(originSql);
//        for (String matcherSql : matcherList) {
//            if (originSql.contains(SQL_LEFT_JOIN + matcherSql)) {
//                continue;
//            }
//            String newMatcherSql = mergeSql(matcherSql, privilegeSql);
//            originSql = originSql.replace(matcherSql, newMatcherSql);
//        }
        return originSql.replace(SQL_WHERE, SQL_WHERE + " " + privilegeSql);
    }

    /**
     * 添加where关键字
     *
     * @param originSql 原始sql
     * @return String
     */
    private static String addWhere(String originSql) {
        if (originSql.contains("WHERE")) {
            return originSql;
        }
        String[] split = originSql.split(" ");
        for (int i = 0; i < split.length; i++) {
            if (split[i].equalsIgnoreCase("GROUP") && (i + 1) < split.length && split[i + 1].equalsIgnoreCase("BY")) {
                return originSql.replaceAll(split[i], "WHERE 1=1 GROUP");
            }
            if (split[i].equalsIgnoreCase("ORDER") && (i + 1) < split.length && split[i + 1].equalsIgnoreCase("BY")) {
                return originSql.replaceAll(split[i], "WHERE 1=1 ORDER");
            }
            if (split[i].equalsIgnoreCase("LIMIT")) {
                return originSql.replaceAll(split[i], "WHERE 1=1 LIMIT");
            }
        }
        return originSql + " WHERE 1=1";
    }

    /**
     * 合并sql
     *
     * @param originSql    原sql
     * @param privilegeSql 数据权限sql
     * @return String
     */
    private static String mergeSql(String originSql, String privilegeSql) {
        return originSql.replace(SQL_WHERE, SQL_WHERE + " " + privilegeSql);
    }

    /**
     * 配置符合
     *
     * @param originSql 原sql
     * @return List<String>
     */
    private static List<String> matcherTableSql(String originSql) {
        List<String> matcharList = new ArrayList<>();
        String regex = "\\w[ ,](.*?)WHERE";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(originSql);
        while (matcher.find()) {
            matcharList.add(matcher.group());
        }
        return matcharList;
    }

    public static void main(String[] args) {

        long start = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++) {
            String sql = "select * from t_bill t where t.user_id = ? and t.staff_id = ? order by t.create_time desc";
            String privilegeSql = " (orgi_id like '0102%' or orig_id like '0202%') and ";
            SqlAppender.handlerSql(sql, privilegeSql);
        }

        long end = System.currentTimeMillis();

        System.out.println(end -start);
    }

}
