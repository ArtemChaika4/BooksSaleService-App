package ua.edu.dnu.booksales.selector;

import ua.edu.dnu.booksales.model.GoodsStatus;
import ua.edu.dnu.booksales.model.GoodsType;

public class GoodsSelector implements Selector{
    private static final String GOODS_SELECT = "SELECT * FROM goods " +
            "LEFT JOIN goods_type ON goods.goods_type_id = goods_type.goods_type_id";
    private String likeParameter;
    private String orderBy;
    private GoodsType type;
    private GoodsStatus status;

    public GoodsSelector setGoodsType(GoodsType type){
        this.type = type;
        return this;
    }

    public GoodsSelector setGoodsStatus(GoodsStatus status){
        this.status = status;
        return this;
    }
    public GoodsSelector setContains(String value){
        likeParameter = value;
        return this;
    }

    public GoodsSelector setSortedByTitle(){
        return setSortedBy("title");
    }

    public GoodsSelector setSortedByAuthor(){
        return setSortedBy("author");
    }

    public GoodsSelector setSortedByPrice(){
        return setSortedBy("price");
    }

    public GoodsSelector setSortedByAmount(){
        return setSortedBy("amount");
    }

    private GoodsSelector setSortedBy(String columnName){
        orderBy = columnName;
        return this;
    }

    @Override
    public String getSqlQuery() {
        StringBuilder sql = new StringBuilder();
        if(type != null){
            String pref = Selector.pref(sql);
            sql.append(pref)
                    .append("goods_type.goods_type_id = ").append(type.getId());
        }
        if(status != null){
            String pref = Selector.pref(sql);
            sql.append(pref)
                    .append("goods_status_id = ").append(status.getId());
        }
        if(likeParameter != null){
            String pref = Selector.pref(sql);
            sql.append(pref)
                    .append("(title LIKE '%").append(likeParameter).append("%' OR ")
                    .append("author LIKE '%").append(likeParameter).append("%' OR ")
                    .append("type_name LIKE '%").append(likeParameter).append("%' OR ")
                    .append("amount LIKE '%").append(likeParameter).append("%' OR ")
                    .append("price LIKE '%").append(likeParameter).append("%')");
        }
        if(orderBy != null){
            sql.append(" ORDER BY ").append(orderBy);
        }

        return GOODS_SELECT + sql;
    }
}
