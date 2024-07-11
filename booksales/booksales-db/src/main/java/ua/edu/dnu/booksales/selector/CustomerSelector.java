package ua.edu.dnu.booksales.selector;

public class CustomerSelector implements Selector{
    private static final String CUSTOMER_SELECT = "SELECT * FROM customers";
    private String likeParameter;
    private String orderBy;

    public CustomerSelector setContains(String value){
        likeParameter = value;
        return this;
    }

    public CustomerSelector setSortedByName(){
        return setSortedBy("name");
    }

    public CustomerSelector setSortedBySurname(){
        return setSortedBy("surname");
    }

    public CustomerSelector setSortedByPatronymic(){
        return setSortedBy("patronymic");
    }

    private CustomerSelector setSortedBy(String columnName){
        orderBy = columnName;
        return this;
    }

    @Override
    public String getSqlQuery() {
        StringBuilder sql = new StringBuilder();
        if(likeParameter != null){
            String pref = " WHERE ";
            sql.append(pref)
                    .append("(name LIKE '%").append(likeParameter).append("%' OR ")
                    .append("surname LIKE '%").append(likeParameter).append("%' OR ")
                    .append("patronymic LIKE '%").append(likeParameter).append("%' OR ")
                    .append("address LIKE '%").append(likeParameter).append("%' OR ")
                    .append("phone LIKE '%").append(likeParameter).append("%')");
        }
        if(orderBy != null){
            sql.append(" ORDER BY ").append(orderBy);
        }
        return CUSTOMER_SELECT + sql;
    }
}
