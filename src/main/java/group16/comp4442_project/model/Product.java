package group16.comp4442_project.model;

public class Product {
    private int id;
    private String name;
    private double price;
    private int stock;
    private String imageUrl;
    private int sellerUserId;
    private String seller;

    public int getId(){ return id; }
    public void setId(int id){ this.id=id; }

    public String getName(){ return name; }
    public void setName(String name){ this.name=name; }

    public double getPrice(){ return price; }
    public void setPrice(double price){ this.price=price; }

    public int getStock(){ return stock; }
    public void setStock(int stock){ this.stock=stock; }

    public String getImageUrl(){ return imageUrl; }
    public void setImageUrl(String imageUrl){ this.imageUrl=imageUrl; }

    public int getSellerUserId(){ return sellerUserId; }
    public void setSellerUserId(int sellerUserId){ this.sellerUserId=sellerUserId; }

    public String getSeller(){ return seller; }
    public void setSeller(String seller){ this.seller=seller; }
}