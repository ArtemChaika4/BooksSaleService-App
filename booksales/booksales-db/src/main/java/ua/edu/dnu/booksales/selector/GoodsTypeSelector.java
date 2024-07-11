package ua.edu.dnu.booksales.selector;

public class GoodsTypeSelector implements Selector{
    private static final String GOODS_TYPE_SELECT = "SELECT * FROM goods_type";
    private String likeParameter;
    private String orderBy;

    public GoodsTypeSelector setContains(String value){
        likeParameter = value;
        return this;
    }

    public GoodsTypeSelector setSortedByName(){
        return setSortedBy("type_name");
    }

    private GoodsTypeSelector setSortedBy(String columnName){
        orderBy = columnName;
        return this;
    }

    @Override
    public String getSqlQuery() {
        StringBuilder sql = new StringBuilder();
        if(likeParameter != null){
            String pref = " WHERE ";
            sql.append(pref)
                    .append("(type_name LIKE '%").append(likeParameter).append("%' OR ")
                    .append("type_description LIKE '%").append(likeParameter).append("%')");
        }
        if(orderBy != null){
            sql.append(" ORDER BY ").append(orderBy);
        }

        return GOODS_TYPE_SELECT + sql;
    }
}
