package application.infrastructure.adapters.in.rest;

import application.domain.enums.ProductStatus;
import application.domain.enums.ProductType;
import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.enums.WarehouseType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MarketplaceRestApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    @DisplayName("REST API: Register Admin, Seller, and Buyer")
    void testUserRegistration() throws Exception {
        // Admin
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "identifier", "ADM-01",
                                "fullName", "Alice Administrator",
                                "email", "alice@nexusmarket.com",
                                "role", UserRole.ADMIN,
                                "status", UserStatus.ACTIVE
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.identifier").value("ADM-01"));

        // Seller
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "identifier", "SEL-01",
                                "fullName", "Best Electronics LLC",
                                "email", "sales@bestelectronics.com",
                                "role", UserRole.SELLER,
                                "status", UserStatus.PENDING_INCORPORATION,
                                "taxIdentifier", "TAX-998877",
                                "corporateName", "Best Electronics Inc"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING_INCORPORATION"));

        // Buyer
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "identifier", "BUY-01",
                                "fullName", "Bob Buyer",
                                "email", "bob@example.com",
                                "role", UserRole.BUYER,
                                "status", UserStatus.ACTIVE,
                                "mainAddress", "742 Evergreen Terrace",
                                "commercialStatus", "GOOD_STANDING"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("BUYER"));
    }

    @Test
    @Order(2)
    @DisplayName("REST API: Incorporate Seller by Admin")
    void testSellerIncorporation() throws Exception {
        mockMvc.perform(post("/api/sellers/SEL-01/incorporate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "adminIdentifier", "ADM-01"
                        ))))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/SEL-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @Order(3)
    @DisplayName("REST API: Register Warehouse")
    void testWarehouseRegistration() throws Exception {
        mockMvc.perform(post("/api/warehouses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "warehouseId", "WH-CENTRAL",
                                "name", "Central Logistics Hub",
                                "location", "100 Industrial Parkway",
                                "type", WarehouseType.MARKETPLACE,
                                "ownerId", "ADM-01",
                                "active", true,
                                "adminIdentifier", "ADM-01"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @Order(4)
    @DisplayName("REST API: Register, Update Price, Add Variant, and Publish Product")
    void testProductCatalogManagement() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "productId", "PRD-101",
                                "sku", "SKU-LAPTOP-01",
                                "sellerId", "SEL-01",
                                "name", "Nexus UltraBook 15",
                                "description", "High performance laptop",
                                "price", 1200.00,
                                "type", ProductType.PHYSICAL,
                                "status", ProductStatus.DRAFT
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DRAFT"));

        mockMvc.perform(put("/api/products/PRD-101/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("newPrice", 1150.00))))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/products/PRD-101/variants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "Color", "value", "Midnight Blue"))))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/products/PRD-101/publish"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/PRD-101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"))
                .andExpect(jsonPath("$.price").value(1150.00));
    }

    @Test
    @Order(5)
    @DisplayName("REST API: Manage Inventory Stock and Allocation")
    void testInventoryManagement() throws Exception {
        mockMvc.perform(post("/api/inventory/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "inventoryId", "INV-101",
                                "productId", "PRD-101",
                                "warehouseId", "WH-CENTRAL",
                                "quantity", 50
                        ))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/inventory/total-available/PRD-101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAvailableStock").value(50));
    }

    @Test
    @Order(6)
    @DisplayName("REST API: Shopping Cart operations and Checkout")
    void testCartAndCheckout() throws Exception {
        mockMvc.perform(post("/api/carts/BUY-01/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "productId", "PRD-101",
                                "unitPrice", 1150.00,
                                "quantity", 2
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2300.00));

        mockMvc.perform(post("/api/orders/checkout/BUY-01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "orderId", "ORD-501",
                                "shippingAddress", "742 Evergreen Terrace"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"))
                .andExpect(jsonPath("$.totalAmount").value(2300.00));
    }

    @Test
    @Order(7)
    @DisplayName("REST API: Process Order Payment and Issue Invoice")
    void testPaymentAndInvoice() throws Exception {
        mockMvc.perform(post("/api/orders/ORD-501/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "paymentToken", "tok_visa_valid_999",
                                "buyerEmail", "bob@example.com"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentSuccess").value(true));

        mockMvc.perform(post("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "invoiceId", "INV-501",
                                "orderId", "ORD-501",
                                "buyerId", "BUY-01",
                                "taxIdentifier", "TAX-BOB-1234",
                                "billingAddress", "742 Evergreen Terrace"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ISSUED"))
                .andExpect(jsonPath("$.totalAmount").value(2300.00));
    }

    @Test
    @Order(8)
    @DisplayName("REST API: Prepare, Dispatch, and Finalize Delivery")
    void testLogisticsFulfillment() throws Exception {
        mockMvc.perform(post("/api/shipments/prepare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "shipmentId", "SHP-501",
                                "orderId", "ORD-501",
                                "warehouseId", "WH-CENTRAL",
                                "destinationAddress", "742 Evergreen Terrace"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PREPARING"));

        mockMvc.perform(put("/api/shipments/SHP-501/dispatch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "carrier", "FedEx Express",
                                "trackingNumber", "FDX-12345678"
                        ))))
                .andExpect(status().isNoContent());

        mockMvc.perform(put("/api/shipments/SHP-501/confirm-delivery"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/orders/ORD-501"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED_FINALIZED"));
    }

    @Test
    @Order(9)
    @DisplayName("REST API: Request Return, Approve, and Process Refund")
    void testReturnAndRefund() throws Exception {
        mockMvc.perform(post("/api/returns/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "returnId", "RET-501",
                                "orderId", "ORD-501",
                                "buyerId", "BUY-01",
                                "productId", "PRD-101",
                                "reason", "DEFECTIVE"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("REQUESTED"));

        mockMvc.perform(put("/api/returns/RET-501/approve"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/returns/process-refund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "refundId", "REF-501",
                                "returnId", "RET-501",
                                "warehouseId", "WH-CENTRAL",
                                "reusable", true,
                                "refundAmount", 1150.00
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PROCESSED"));
    }
}
