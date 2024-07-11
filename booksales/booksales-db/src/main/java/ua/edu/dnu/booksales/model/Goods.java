package ua.edu.dnu.booksales.model;

public class Goods {
    private int id;
    private String title;
    private String author;
    private int amount;
    private int price;
    private GoodsStatus status;
    private GoodsType type;

    public Goods(){
        id = 0;
        title = "";
        author = "";
        amount = 0;
        price = 0;
        status = GoodsStatus.AVAILABLE;
        type = new GoodsType();
    }

    public Goods(String title, String author, int amount, int price, GoodsStatus status, GoodsType type) {
        this.title = title;
        this.author = author;
        this.amount = amount;
        this.price = price;
        this.status = status;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public GoodsStatus getStatus() {
        return status;
    }

    public void setStatus(GoodsStatus status) {
        this.status = status;
    }

    public GoodsType getType() {
        return type;
    }

    public void setType(GoodsType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", amount=" + amount +
                ", price=" + price +
                ", status=" + status +
                ", type=" + type +
                '}';
    }
}
