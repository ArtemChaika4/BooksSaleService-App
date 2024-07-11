package ua.edu.dnu.booksales.model;


public class ActiveUser {
    private static ActiveUser active;
    private Employee employee;

    public ActiveUser() {}


    public void setEmployee(Employee employee){
        this.employee = employee;
    }

    public Employee getEmployee(){
        return employee;
    }

    public static ActiveUser getInstance() {
        if(active == null){
            active = new ActiveUser();
        }
        return active;
    }
}
