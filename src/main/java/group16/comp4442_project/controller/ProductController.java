package group16.comp4442_project.controller;

import group16.comp4442_project.config.AuthInterceptor;
import group16.comp4442_project.model.Product;
import group16.comp4442_project.model.User;
import group16.comp4442_project.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service){
        this.service = service;
    }

    @GetMapping
    public List<Product> getAll(){
        return service.getProducts();
    }

    @PostMapping
    public String add(
            HttpServletRequest request,
            @RequestBody Product p
    ){
        User user = (User) request.getAttribute(AuthInterceptor.AUTH_USER);
        String role = user.getRole();
        if (!"seller".equalsIgnoreCase(role) && !"admin".equalsIgnoreCase(role)) {
            return "Only seller/admin can add products";
        }
        return service.add(p, user);
    }

    @PostMapping("/upload")
    public String upload(
            HttpServletRequest request,
            @RequestParam("name") String name,
            @RequestParam("price") double price,
            @RequestParam("stock") int stock,
            @RequestParam("file") MultipartFile file
    ){
        User user = (User) request.getAttribute(AuthInterceptor.AUTH_USER);
        String role = user.getRole();
        if (!"seller".equalsIgnoreCase(role) && !"admin".equalsIgnoreCase(role)) {
            return "Only seller/admin can upload products";
        }

        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setStock(stock);
        return service.saveProduct(p, file, user);
    }

    @PutMapping("/{id}")
    public String update(
            @PathVariable int id,
            HttpServletRequest request,
            @RequestBody Product p
    ) {
        User user = (User) request.getAttribute(AuthInterceptor.AUTH_USER);
        String role = user.getRole();
        if (!"seller".equalsIgnoreCase(role) && !"admin".equalsIgnoreCase(role)) {
            return "Only seller/admin can edit products";
        }
        Product existing = service.getProduct(id);
        if (existing == null) {
            return "Product not found";
        }
        if ("seller".equalsIgnoreCase(role) && !user.getUsername().equals(existing.getSeller())) {
            return "Seller can only edit own products";
        }
        return service.update(id, p);
    }

    @DeleteMapping("/{id}")
    public String delete(
            @PathVariable int id,
            HttpServletRequest request
    ){
        User user = (User) request.getAttribute(AuthInterceptor.AUTH_USER);
        String role = user.getRole();
        if (!"seller".equalsIgnoreCase(role) && !"admin".equalsIgnoreCase(role)) {
            return "Only seller/admin can remove products";
        }
        Product existing = service.getProduct(id);
        if (existing == null) {
            return "Product not found";
        }
        if ("seller".equalsIgnoreCase(role) && !user.getUsername().equals(existing.getSeller())) {
            return "Seller can only remove own products";
        }
        return service.delete(id);
    }
}