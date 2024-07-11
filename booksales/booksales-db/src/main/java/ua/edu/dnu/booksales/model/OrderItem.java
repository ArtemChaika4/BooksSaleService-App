package ua.edu.dnu.booksales.model;

public class OrderItem {
    private int id;
    private Goods goods;
    private int number;

    public OrderItem(){
        goods = new Goods();
        number = 0;
    }

    public OrderItem(Goods goods, int number) {
        this.goods = goods;
        this.number = number;
    }

    public OrderItem(int id, Goods goods, int number) {
        this.id = id;
        this.goods = goods;
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getTotalPrice(){
        return number * goods.getPrice();
    }

    public void addNumber(int number){
        this.number += number;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "goods=" + goods +
                ", number=" + number +
                '}';
    }
}
