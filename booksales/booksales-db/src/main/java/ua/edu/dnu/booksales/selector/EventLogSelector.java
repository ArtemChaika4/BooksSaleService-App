package ua.edu.dnu.booksales.selector;

import ua.edu.dnu.booksales.model.DBTable;

import java.sql.Date;

public class EventLogSelector implements Selector{
    private static final String LOG_SELECT = "SELECT * FROM event_log " +
            "LEFT JOIN employees ON event_log.employee_id = employees.employee_id " +
            "LEFT JOIN positions ON employees.position_id = positions.position_id";
    private String likeParameter;
    private Date afterDate;
    private Date beforeDate;
    private DBTable table;

    public EventLogSelector setTable(DBTable table){
        this.table = table;
        return this;
    }

    public EventLogSelector setContains(String value){
        likeParameter = value;
        return this;
    }

    public EventLogSelector setAfterDate(Date date){
        afterDate = date;
        return this;
    }

    public EventLogSelector setBeforeDate(Date date){
        beforeDate = date;
        return this;
    }

    @Override
    public String getSqlQuery() {
        StringBuilder sql = new StringBuilder();
        if(table != null){
            String pref = Selector.pref(sql);
            sql.append(pref)
                    .append("table_id = ").append(table.getId());
        }
        if(likeParameter != null){
            String pref = Selector.pref(sql);
            sql.append(pref)
                    .append("(activity LIKE '%").append(likeParameter).append("%' OR ")
                    .append("surname LIKE '%").append(likeParameter).append("%' OR ")
                    .append("name LIKE '%").append(likeParameter).append("%' OR ")
                    .append("email LIKE '%").append(likeParameter).append("%' OR ")
                    .append("post_name LIKE '%").append(likeParameter).append("%' OR ")
                    .append("CONVERT(VARCHAR, timestamp, 20) LIKE '%").append(likeParameter).append("%')");
        }
        if(afterDate != null){
            String pref = Selector.pref(sql);
            sql.append(pref)
                    .append("timestamp >= CONVERT(DATETIME, '").append(afterDate).append("', 20)");
        }
        if(beforeDate != null){
            String pref = Selector.pref(sql);
            sql.append(pref)
                    .append("timestamp <= CONVERT(DATETIME, '").append(beforeDate).append("', 20)");
        }
        sql.append(" ORDER BY timestamp");

        return LOG_SELECT + sql;
    }
}
