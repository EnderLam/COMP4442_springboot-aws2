package group16.comp4442_project.model;

public class Order {
    private int id;
    private int userId;
    private int productId;
    private int quantity;
    private String status;

    public int getId(){ return id; }
    public void setId(int id){ this.id=id; }

    public int getUserId(){ return userId; }
    public void setUserId(int userId){ this.userId=userId; }

    public int getProductId(){ return productId; }
    public void setProductId(int productId){ this.productId=productId; }

    public int getQuantity(){ return quantity; }
    public void setQuantity(int quantity){ this.quantity=quantity; }

    public String getStatus(){ return status; }
    public void setStatus(String status){ this.status=status; }
}