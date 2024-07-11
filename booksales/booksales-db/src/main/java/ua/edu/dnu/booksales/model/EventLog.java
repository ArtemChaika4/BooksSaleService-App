package ua.edu.dnu.booksales.model;

import java.sql.Timestamp;

public class EventLog {
    private int id;
    private String activity;
    private DBTable table;
    private Timestamp timestamp;
    private Employee employee;

    public EventLog(){
        id = 0;
        activity = "";
        timestamp = new Timestamp(System.currentTimeMillis());
        employee = new Employee();
    }

    public EventLog(String activity, DBTable table){
        this.activity = activity;
        timestamp = new Timestamp(System.currentTimeMillis());
        employee = ActiveUser.getInstance().getEmployee();
        this.table = table;
    }

    public EventLog(String activity, DBTable table, Timestamp timestamp, Employee employee) {
        this.activity = activity;
        this.timestamp = timestamp;
        this.employee = employee;
        this.table = table;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public DBTable getTable() {
        return table;
    }

    public void setTable(DBTable table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return "EventLog{" +
                "id=" + id +
                ", activity='" + activity + '\'' +
                ", table=" + table +
                ", timestamp=" + timestamp +
                ", employee=" + employee +
                '}';
    }
}
