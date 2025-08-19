package net.nagispace.productbackend;

import net.nagispace.productbackend.controller.ProductsApiController;
import net.nagispace.productbackend.service.AuthService;
import net.nagispace.productbackend.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = ProductsApiController.class)
class ProductsApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /products returns 401 when not authenticated")
    void productsGet_unauthenticated_returns401() throws Exception {
        when(authService.isAuthenticated("Bearer bad")).thenReturn(false);

        mockMvc.perform(get("/products")
                        .header("Authorization", "Bearer bad"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /products returns 200 and list when authenticated")
    void productsGet_authenticated_returnsOkWithList() throws Exception {
        when(authService.isAuthenticated("Bearer token")).thenReturn(true);
        Product p = new Product().id(1L).name("Test Product");
        when(productService.getAllProducts()).thenReturn(List.of(p));

        mockMvc.perform(get("/products")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("DELETE /products/{id} returns 403 when not admin")
    void productsDelete_notAdmin_returns403() throws Exception {
        when(authService.isAdmin("Bearer token")).thenReturn(false);

        mockMvc.perform(delete("/products/{id}", 10L)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /products/{id} returns 204 when admin and product exists")
    void productsDelete_adminAndExists_returns204() throws Exception {
        when(authService.isAdmin("Bearer token")).thenReturn(true);
        when(productService.getProductById(10L)).thenReturn(Optional.of(new Product().id(10L)));
        doNothing().when(productService).deleteProduct(10L);

        mockMvc.perform(delete("/products/{id}", 10L)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /products/{id} returns 404 when admin but product not found")
    void productsDelete_adminNotFound_returns404() throws Exception {
        when(authService.isAdmin("Bearer token")).thenReturn(true);
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/products/{id}", 999L)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /products/{id} returns 401 when not authenticated")
    void productById_unauthenticated_returns401() throws Exception {
        when(authService.isAuthenticated("Bearer bad")).thenReturn(false);

        mockMvc.perform(get("/products/{id}", 5L)
                        .header("Authorization", "Bearer bad"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /products/{id} returns 200 with product when authenticated and found")
    void productById_authenticatedFound_returns200() throws Exception {
        when(authService.isAuthenticated("Bearer token")).thenReturn(true);
        when(productService.getProductById(5L)).thenReturn(Optional.of(new Product().id(5L).name("X")));

        mockMvc.perform(get("/products/{id}", 5L)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    @DisplayName("GET /products/{id} returns 404 when authenticated but not found")
    void productById_authenticatedNotFound_returns404() throws Exception {
        when(authService.isAuthenticated("Bearer token")).thenReturn(true);
        when(productService.getProductById(5L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/products/{id}", 5L)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /products/{id} returns 403 when not admin")
    void productPut_notAdmin_returns403() throws Exception {
        when(authService.isAdmin("Bearer token")).thenReturn(false);

        Product body = new Product().name("Updated");
        mockMvc.perform(put("/products/{id}", 3L)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /products/{id} returns 200 with updated product when admin and exists")
    void productPut_adminExists_returns200() throws Exception {
        when(authService.isAdmin("Bearer token")).thenReturn(true);
        Product body = new Product().name("Updated");
        Product updated = new Product().id(3L).name("Updated");
        when(productService.updateProduct(eq(3L), any(Product.class))).thenReturn(Optional.of(updated));

        mockMvc.perform(put("/products/{id}", 3L)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    @DisplayName("PUT /products/{id} returns 404 when admin but product not found")
    void productPut_adminNotFound_returns404() throws Exception {
        when(authService.isAdmin("Bearer token")).thenReturn(true);
        when(productService.updateProduct(eq(42L), any(Product.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/products/{id}", 42L)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /products returns 403 when not admin")
    void productsPost_notAdmin_returns403() throws Exception {
        when(authService.isAdmin("Bearer token")).thenReturn(false);

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /products returns 201 when admin")
    void productsPost_admin_returns201() throws Exception {
        when(authService.isAdmin("Bearer token")).thenReturn(true);
        Product body = new Product().name("New");
        Product created = new Product().id(100L).name("New");
        when(productService.createProduct(any(Product.class))).thenReturn(created);

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100));
    }
}