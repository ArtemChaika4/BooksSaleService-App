package ua.edu.dnu.booksales.selector;

import ua.edu.dnu.booksales.model.Position;

public class EmployeeSelector implements Selector{
    private static final String EMPLOYEE_SELECT = "SELECT * FROM employees " +
            "LEFT JOIN positions ON employees.position_id = positions.position_id";
    private String likeParameter;
    private String orderBy;
    private Position position;

    public EmployeeSelector setContains(String value){
        likeParameter = value;
        return this;
    }

    public EmployeeSelector setPosition(Position position){
        this.position = position;
        return this;
    }

    public EmployeeSelector setSortedByName(){
        return setSortedBy("name");
    }

    public EmployeeSelector setSortedBySurname(){
        return setSortedBy("surname");
    }

    public EmployeeSelector setSortedByPatronymic(){
        return setSortedBy("patronymic");
    }

    private EmployeeSelector setSortedBy(String columnName){
        orderBy = columnName;
        return this;
    }

    @Override
    public String getSqlQuery() {
        StringBuilder sql = new StringBuilder();
        if(position != null){
            String pref = Selector.pref(sql);
            sql.append(pref)
                    .append("positions.position_id = ").append(position.getId());
        }
        if(likeParameter != null){
            String pref = Selector.pref(sql);
            sql.append(pref)
                    .append("(name LIKE '%").append(likeParameter).append("%' OR ")
                    .append("surname LIKE '%").append(likeParameter).append("%' OR ")
                    .append("patronymic LIKE '%").append(likeParameter).append("%' OR ")
                    .append("post_name LIKE '%").append(likeParameter).append("%' OR ")
                    .append("email LIKE '%").append(likeParameter).append("%')");
        }
        if(orderBy != null){
            sql.append(" ORDER BY ").append(orderBy);
        }
        return EMPLOYEE_SELECT + sql;
    }
}
