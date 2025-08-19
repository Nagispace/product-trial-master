package net.nagispace.productbackend;

import net.nagispace.productbackend.controller.WishlistController;
import net.nagispace.productbackend.entity.UserEntity;
import net.nagispace.productbackend.service.AuthService;
import net.nagispace.productbackend.service.UserService;
import net.nagispace.productbackend.service.WishlistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.model.Product;
import org.openapitools.model.User;
import org.openapitools.model.Wishlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = WishlistController.class)
class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WishlistService wishlistService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /wishlist returns 403 when not authenticated")
    void wishlistGet_notAuthenticated_returns403() throws Exception {
        when(authService.isAuthenticated("Bearer bad")).thenReturn(false);

        mockMvc.perform(get("/wishlist")
                        .header("Authorization", "Bearer bad"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /wishlist returns 200 with wishlist when authenticated")
    void wishlistGet_authenticated_returns200() throws Exception {
        String auth = "Bearer token";
        when(authService.isAuthenticated(auth)).thenReturn(true);

        User apiUser = new User().email("user@example.com").username("user1");
        UserEntity userEntity = org.mockito.Mockito.mock(UserEntity.class);

        when(authService.getCurrentUser(auth)).thenReturn(apiUser);
        when(userService.toEntity(apiUser)).thenReturn(userEntity);

        Wishlist wishlist = new Wishlist().id(1L);
        when(wishlistService.getWishlist(userEntity)).thenReturn(wishlist);

        mockMvc.perform(get("/wishlist")
                        .header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /wishlist returns 403 when not authenticated")
    void wishlistPost_notAuthenticated_returns403() throws Exception {
        when(authService.isAuthenticated("Bearer bad")).thenReturn(false);

        mockMvc.perform(post("/wishlist")
                        .header("Authorization", "Bearer bad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /wishlist returns 200 with updated wishlist when authenticated")
    void wishlistPost_authenticated_returns200() throws Exception {
        String auth = "Bearer token";
        when(authService.isAuthenticated(auth)).thenReturn(true);

        User apiUser = new User().email("user@example.com").username("user1");
        UserEntity userEntity = org.mockito.Mockito.mock(UserEntity.class);

        when(authService.getCurrentUser(auth)).thenReturn(apiUser);
        when(userService.toEntity(apiUser)).thenReturn(userEntity);

        Wishlist updated = new Wishlist().id(2L);
        when(wishlistService.addProduct(org.mockito.Mockito.eq(userEntity), any(Product.class)))
                .thenReturn(updated);

        Product body = new Product().id(10L).name("P1");

        mockMvc.perform(post("/wishlist")
                        .header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }
}