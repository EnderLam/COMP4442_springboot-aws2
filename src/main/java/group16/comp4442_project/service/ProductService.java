package group16.comp4442_project.service;

import group16.comp4442_project.model.Product;
import group16.comp4442_project.model.User;
import group16.comp4442_project.dao.ProductDAO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ProductService {
    private final ProductDAO productDao;

    public ProductService(ProductDAO productDao) {
        this.productDao = productDao;
    }

    public List<Product> getProducts(){
        return productDao.findAll();
    }

    public String add(Product p, User seller){
        p.setSellerUserId(seller.getId());
        p.setSeller(seller.getUsername());
        productDao.save(p);
        return "Product added";
    }

    public String delete(int id){
        productDao.delete(id);
        return "Product removed";
    }

    public String update(int id, Product payload) {
        Product existing = productDao.findById(id).orElse(null);
        if (existing == null) {
            return "Product not found";
        }
        existing.setId(id);
        existing.setName(payload.getName());
        existing.setPrice(payload.getPrice());
        existing.setStock(payload.getStock());
        productDao.update(existing);
        return "Product updated";
    }

    public Product getProduct(int id) {
        return productDao.findById(id).orElse(null);
    }

    public String saveProduct(Product p, MultipartFile file, User seller) {
        try {
            String safeSeller = seller.getUsername().replaceAll("[^a-zA-Z0-9_-]", "_");
            Path base = Path.of("uploads", safeSeller);
            Files.createDirectories(base);
            String filename = file.getOriginalFilename() == null ? "product.jpg" : file.getOriginalFilename();
            Path target = base.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            p.setImageUrl("/uploads/" + safeSeller + "/" + filename);
            return add(p, seller);
        } catch (IOException e) {
            return "Upload failed: " + e.getMessage();
        }
    }
}