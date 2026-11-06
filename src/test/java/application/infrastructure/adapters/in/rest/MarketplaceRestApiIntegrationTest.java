package application.infrastructure.adapters.in.rest;

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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end test of the REST API following the business flow of section 6.1.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MarketplaceRestApiIntegrationTest {

    private static final String HEADER = "X-User-Email";
    private static final String ADMIN = "admin@nexusmarket.com";
    private static final String SELLER = "rest.seller@nexusmarket.com";
    private static final String BUYER = "rest.buyer@nexusmarket.com";
    private static final String OTHER_BUYER = "rest.other@nexusmarket.com";
    private static final String OPERATOR = "rest.operator@nexusmarket.com";
    private static final String SUPERVISOR = "rest.supervisor@nexusmarket.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ResultActions call(MockHttpServletRequestBuilder request, String userEmail, Object body) throws Exception {
        if (userEmail != null) {
            request.header(HEADER, userEmail);
        }
        if (body != null) {
            request.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(body));
        }
        return mockMvc.perform(request);
    }

    @Test
    @Order(1)
    @DisplayName("RG-01: operations without an authenticated user are rejected with 401")
    void unauthenticatedRequestsAreRejected() throws Exception {
        call(get("/api/products"), null, null).andExpect(status().isUnauthorized());
        call(get("/api/products"), "nobody@nexusmarket.com", null).andExpect(status().isUnauthorized());
        call(post("/api/auth/login"), null, Map.of("email", ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.role").value("ADMIN"));
    }

    @Test
    @Order(2)
    @DisplayName("Users: buyers self-register; admin registers internal users; sellers cannot self-register")
    void registerUsers() throws Exception {
        call(post("/api/users/register"), null, Map.of("identifier", "REST-BUY-1", "identityDocument", "CC-REST-1",
                "fullName", "Rest Buyer", "email", BUYER, "mainAddress", "742 Evergreen Terrace", "commercialStatus", "ENABLED"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("BUYER"));
        call(post("/api/users/register"), null, Map.of("identifier", "REST-BUY-2", "identityDocument", "CC-REST-2",
                "fullName", "Other Buyer", "email", OTHER_BUYER, "mainAddress", "1 Other St", "commercialStatus", "ENABLED"))
                .andExpect(status().isCreated());
        // identity document must be unique
        call(post("/api/users/register"), null, Map.of("identifier", "REST-BUY-3", "identityDocument", "CC-REST-1",
                "fullName", "Dup", "email", "dup@nexusmarket.com", "mainAddress", "x", "commercialStatus", "ENABLED"))
                .andExpect(status().isConflict());

        call(post("/api/users"), ADMIN, Map.of("identifier", "REST-OP-1", "identityDocument", "CC-REST-OP",
                "fullName", "Rest Operator", "email", OPERATOR, "role", "OPERATOR_LOGISTIC"))
                .andExpect(status().isCreated());
        call(post("/api/users"), ADMIN, Map.of("identifier", "REST-SUP-1", "identityDocument", "CC-REST-SUP",
                "fullName", "Rest Supervisor", "email", SUPERVISOR, "role", "SUPERVISOR"))
                .andExpect(status().isCreated());
        // a seller cannot be created as a plain user, and a buyer cannot create internal users
        call(post("/api/users"), ADMIN, Map.of("identifier", "X", "identityDocument", "X", "fullName", "X",
                "email", "x@nexusmarket.com", "role", "SELLER")).andExpect(status().isBadRequest());
        call(post("/api/users"), BUYER, Map.of("identifier", "Y", "identityDocument", "Y", "fullName", "Y",
                "email", "y@nexusmarket.com", "role", "ADMIN")).andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    @DisplayName("Sellers: the admin incorporates the seller with its first warehouse; the supervisor cannot")
    void incorporateSeller() throws Exception {
        Map<String, Object> body = Map.of("identifier", "REST-SEL-1", "identityDocument", "NIT-REST-1", "fullName", "Rest Seller",
                "email", SELLER, "taxIdentifier", "TAX-REST-1", "corporateName", "Rest Seller Corp",
                "firstWarehouse", Map.of("warehouseId", "REST-WH-1", "name", "Rest Seller Warehouse", "location", "Industrial Zone 5"));
        call(post("/api/sellers"), SUPERVISOR, body).andExpect(status().isForbidden());
        call(post("/api/sellers"), ADMIN, body)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
        call(get("/api/warehouses/REST-WH-1"), SELLER, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("SELLER"))
                .andExpect(jsonPath("$.ownerId").value("REST-SEL-1"));
        call(post("/api/warehouses"), ADMIN, Map.of("warehouseId", "REST-WH-MKT", "name", "Marketplace Hub",
                "location", "Central Av 100", "type", "MARKETPLACE"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerId").value("NEXUSMARKET"));
    }

    @Test
    @Order(4)
    @DisplayName("Catalog and inventory: only the seller registers products; stock is added to its warehouse")
    void catalogAndInventory() throws Exception {
        call(post("/api/products"), ADMIN, Map.of("productId", "REST-P-1", "sku", "REST-SKU-1", "name", "Laptop",
                "price", 1200.0, "type", "PHYSICAL", "status", "PUBLISHED")).andExpect(status().isForbidden());
        call(post("/api/products"), SELLER, Map.of("productId", "REST-P-1", "sku", "REST-SKU-1", "name", "Laptop",
                "description", "16GB RAM", "price", 1200.0, "type", "PHYSICAL", "status", "SUSPENDED",
                "variants", List.of(Map.of("name", "Color", "value", "Silver"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sellerId").value("REST-SEL-1"));
        call(post("/api/products"), SELLER, Map.of("productId", "REST-P-2", "sku", "REST-SKU-2", "name", "E-book",
                "price", 20.0, "type", "DIGITAL", "status", "PUBLISHED")).andExpect(status().isCreated());

        // Not published yet: not visible in the public catalog, cannot be added to a cart
        call(get("/api/products"), BUYER, null).andExpect(jsonPath("$[?(@.productId=='REST-P-1')]").isEmpty());
        call(put("/api/products/REST-P-1/publish"), SELLER, null).andExpect(jsonPath("$.status").value("PUBLISHED"));
        call(get("/api/products"), BUYER, null).andExpect(jsonPath("$[?(@.productId=='REST-P-1')]").isNotEmpty());

        // Inventory: buyers never manage inventory; digital products have no inventory
        call(post("/api/inventory/add"), BUYER, Map.of("productId", "REST-P-1", "warehouseId", "REST-WH-1", "quantity", 5))
                .andExpect(status().isForbidden());
        call(post("/api/inventory/add"), SELLER, Map.of("productId", "REST-P-2", "warehouseId", "REST-WH-1", "quantity", 5))
                .andExpect(status().isBadRequest());
        call(post("/api/inventory/add"), SELLER, Map.of("productId", "REST-P-1", "warehouseId", "REST-WH-1", "quantity", 5))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.availableStock").value(5));
        call(post("/api/inventory/mark-damaged"), OPERATOR, Map.of("productId", "REST-P-1", "warehouseId", "REST-WH-1", "quantity", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.damagedStock").value(1));
    }

    @Test
    @Order(5)
    @DisplayName("Orders: cart -> checkout (reserve) -> payment (invoice); buyers only see their own data")
    void cartCheckoutAndPayment() throws Exception {
        call(post("/api/carts/REST-BUY-1/items"), OTHER_BUYER, Map.of("productId", "REST-P-1", "quantity", 1))
                .andExpect(status().isForbidden());
        call(post("/api/carts/REST-BUY-1/items"), BUYER, Map.of("productId", "REST-P-1", "quantity", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CART"))
                .andExpect(jsonPath("$.total").value(2400.0));
        call(post("/api/orders/checkout/REST-BUY-1"), BUYER, Map.of("orderId", "REST-ORD-1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"))
                .andExpect(jsonPath("$.shippingAddress").value("742 Evergreen Terrace"))
                .andExpect(jsonPath("$.items[0].warehouseId").value("REST-WH-1"));
        call(get("/api/inventory?productId=REST-P-1&warehouseId=REST-WH-1"), OPERATOR, null)
                .andExpect(jsonPath("$.availableStock").value(2))
                .andExpect(jsonPath("$.reservedStock").value(2));

        call(get("/api/orders/REST-ORD-1"), OTHER_BUYER, null).andExpect(status().isForbidden());
        call(post("/api/orders/REST-ORD-1/pay"), BUYER, Map.of("paymentToken", "tok_visa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
        call(get("/api/invoices/order/REST-ORD-1"), BUYER, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(2400.0));

        // Digital-only order: delivered immediately after payment
        call(post("/api/carts/REST-BUY-1/items"), BUYER, Map.of("productId", "REST-P-2", "quantity", 1)).andExpect(status().isOk());
        call(post("/api/orders/checkout/REST-BUY-1"), BUYER, Map.of("orderId", "REST-ORD-2")).andExpect(status().isCreated());
        call(post("/api/orders/REST-ORD-2/pay"), BUYER, Map.of("paymentToken", "tok_visa"))
                .andExpect(jsonPath("$.status").value("DELIVERED_FINALIZED"));
    }

    @Test
    @Order(6)
    @DisplayName("Logistics: the operator prepares, dispatches (sale outflow) and confirms delivery")
    void logistics() throws Exception {
        call(post("/api/shipments/prepare"), SELLER, Map.of("shipmentId", "REST-SHP-1", "orderId", "REST-ORD-1"))
                .andExpect(status().isForbidden());
        call(post("/api/shipments/prepare"), OPERATOR, Map.of("shipmentId", "REST-SHP-1", "orderId", "REST-ORD-1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PREPARING"));
        call(put("/api/shipments/REST-SHP-1/dispatch"), OPERATOR, Map.of("carrier", "FedEx", "trackingNumber", "FX-1"))
                .andExpect(jsonPath("$.status").value("IN_TRANSIT"));
        call(get("/api/inventory?productId=REST-P-1&warehouseId=REST-WH-1"), SELLER, null)
                .andExpect(jsonPath("$.reservedStock").value(0))
                .andExpect(jsonPath("$.lastMovementType").value("OUTFLOW_SALE"));
        call(put("/api/shipments/REST-SHP-1/confirm-delivery"), OPERATOR, null)
                .andExpect(jsonPath("$.status").value("DELIVERED"));
        call(get("/api/orders/REST-ORD-1"), SELLER, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED_FINALIZED"));
    }

    @Test
    @Order(7)
    @DisplayName("Returns and refunds: buyer requests, admin approves and refunds")
    void returnsAndRefunds() throws Exception {
        call(post("/api/returns/request"), BUYER, Map.of("returnId", "REST-RET-1", "orderId", "REST-ORD-1",
                "productId", "REST-P-1", "reason", "DEFECTIVE"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("REQUESTED"));
        call(put("/api/returns/REST-RET-1/approve"), OPERATOR, null).andExpect(status().isForbidden());
        call(put("/api/returns/REST-RET-1/approve"), ADMIN, null).andExpect(jsonPath("$.status").value("APPROVED"));
        call(post("/api/returns/process-refund"), ADMIN, Map.of("refundId", "REST-REF-1", "returnId", "REST-RET-1", "reusable", true))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PROCESSED"))
                .andExpect(jsonPath("$.amount").value(2400.0));
        call(get("/api/inventory?productId=REST-P-1&warehouseId=REST-WH-1"), SELLER, null)
                .andExpect(jsonPath("$.availableStock").value(4))
                .andExpect(jsonPath("$.lastMovementType").value("RETURN"));
    }

    @Test
    @Order(8)
    @DisplayName("Reports and audit: supervisor consults; blocked users cannot operate")
    void reportsAuditAndBlocking() throws Exception {
        call(get("/api/reports/summary"), BUYER, null).andExpect(status().isForbidden());
        call(get("/api/reports/summary"), SUPERVISOR, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usersByRole.SELLER").isNumber());
        call(get("/api/audit/aggregate/REST-ORD-1"), SUPERVISOR, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].action").value("CHECKOUT"));
        call(put("/api/users/REST-BUY-2/block"), SUPERVISOR, null).andExpect(status().isForbidden());
        call(put("/api/users/REST-BUY-2/block"), ADMIN, null).andExpect(jsonPath("$.status").value("BLOCKED"));
        call(get("/api/products"), OTHER_BUYER, null).andExpect(status().isUnauthorized());
    }
}
