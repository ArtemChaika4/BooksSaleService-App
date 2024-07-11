package ua.edu.dnu.booksales.selector;

public interface Selector {
    String getSqlQuery();
    static String pref(StringBuilder sql){
        return sql.length() == 0 ? " WHERE " : " AND ";
    }
}
