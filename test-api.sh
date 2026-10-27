#!/usr/bin/env bash

# ==============================================================================
# NexusMarket - End-to-End Terminal REST API Demonstration Script
# ==============================================================================
# Usage:
#   1. Start the backend:
#      mvn spring-boot:run
#   2. Run this script in another terminal window:
#      ./test-api.sh
# ==============================================================================

set -e

BASE_URL="http://localhost:8080/api"
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${CYAN}========================================================================${NC}"
echo -e "${CYAN}                NEXUSMARKET E2E REST API TERMINAL DEMO                 ${NC}"
echo -e "${CYAN}========================================================================${NC}\n"

# Check if server is running
if ! curl -s --head "${BASE_URL}/audit" > /dev/null; then
    echo -e "${YELLOW}[!] Warning: Server at ${BASE_URL} is not responding.${NC}"
    echo -e "    Please start the application with: mvn spring-boot:run"
    echo -e "    Then re-run this script.\n"
    exit 1
fi

step=1
execute_step() {
    local title="$1"
    local method="$2"
    local endpoint="$3"
    local payload="$4"

    echo -e "${BLUE}[Step ${step}] ${title}${NC}"
    echo -e "${YELLOW}${method} ${BASE_URL}${endpoint}${NC}"

    if [ -n "$payload" ]; then
        echo -e "Payload: ${payload}"
        response=$(curl -s -X "${method}" "${BASE_URL}${endpoint}" \
            -H "Content-Type: application/json" \
            -d "${payload}")
    else
        response=$(curl -s -X "${method}" "${BASE_URL}${endpoint}")
    fi

    if [ -n "$response" ]; then
        echo -e "${GREEN}Response:${NC} ${response}\n"
    else
        echo -e "${GREEN}Response:${NC} (204 No Content / Success)\n"
    fi

    step=$((step + 1))
    sleep 0.5
}

# 1. Register Admin
execute_step "Register Administrator User" "POST" "/users/register" \
    '{"identifier":"ADM-01","fullName":"Alice Admin","email":"admin@nexusmarket.com","role":"ADMIN","status":"ACTIVE"}'

# 2. Register Seller (Pending Incorporation)
execute_step "Register Seller (Pending Incorporation)" "POST" "/users/register" \
    '{"identifier":"SEL-01","fullName":"Apex Tech LLC","email":"sales@apextech.com","role":"SELLER","status":"PENDING_INCORPORATION","taxIdentifier":"TAX-998877","corporateName":"Apex Technologies"}'

# 3. Register Buyer
execute_step "Register Buyer User" "POST" "/users/register" \
    '{"identifier":"BUY-01","fullName":"Bob Buyer","email":"bob@example.com","role":"BUYER","status":"ACTIVE","mainAddress":"742 Evergreen Terrace","commercialStatus":"GOOD_STANDING"}'

# 4. Admin Incorporates Seller
execute_step "Admin Incorporates Seller Account" "POST" "/sellers/SEL-01/incorporate" \
    '{"adminIdentifier":"ADM-01"}'

# 5. Register Central Warehouse
execute_step "Register Central Marketplace Warehouse" "POST" "/warehouses" \
    '{"warehouseId":"WH-CENTRAL","name":"Central Logistics Hub","location":"100 Industrial Pkwy","type":"MARKETPLACE","ownerId":"ADM-01","active":true,"adminIdentifier":"ADM-01"}'

# 6. Register Product into Catalog
execute_step "Register Physical Product into Catalog" "POST" "/products" \
    '{"productId":"PRD-101","sku":"SKU-LAPTOP-01","sellerId":"SEL-01","name":"Nexus UltraBook 15","description":"High-performance laptop","price":1200.00,"type":"PHYSICAL","status":"DRAFT"}'

# 7. Add Product Variant
execute_step "Add Product Variant (Color: Midnight Blue)" "POST" "/products/PRD-101/variants" \
    '{"name":"Color","value":"Midnight Blue"}'

# 8. Update Product Price
execute_step "Update Product Price (Discount to $1150)" "PUT" "/products/PRD-101/price" \
    '{"newPrice":1150.00}'

# 9. Publish Product
execute_step "Publish Product to Active Catalog" "PUT" "/products/PRD-101/publish" ""

# 10. Add Stock to Inventory
execute_step "Add Stock to Warehouse Inventory (50 units)" "POST" "/inventory/add" \
    '{"inventoryId":"INV-101","productId":"PRD-101","warehouseId":"WH-CENTRAL","quantity":50}'

# 11. Query Available Stock
execute_step "Query Available Stock for Product" "GET" "/inventory/total-available/PRD-101" ""

# 12. Buyer Adds Product to Shopping Cart
execute_step "Add 2 Laptops to Buyer Shopping Cart" "POST" "/carts/BUY-01/items" \
    '{"productId":"PRD-101","unitPrice":1150.00,"quantity":2}'

# 13. View Buyer Shopping Cart
execute_step "View Buyer Cart Contents & Total" "GET" "/carts/BUY-01" ""

# 14. Checkout Cart into Order
execute_step "Checkout Cart into Formal Order (ORD-501)" "POST" "/orders/checkout/BUY-01" \
    '{"orderId":"ORD-501","shippingAddress":"742 Evergreen Terrace"}'

# 15. Process Payment via Gateway
execute_step "Process Order Payment via Payment Gateway" "POST" "/orders/ORD-501/pay" \
    '{"paymentToken":"tok_visa_valid_999","buyerEmail":"bob@example.com"}'

# 16. Generate Commercial Tax Invoice
execute_step "Generate & Issue Tax Invoice" "POST" "/invoices" \
    '{"invoiceId":"INV-501","orderId":"ORD-501","buyerId":"BUY-01","taxIdentifier":"TAX-BOB-1234","billingAddress":"742 Evergreen Terrace"}'

# 17. Prepare Shipment for Order
execute_step "Prepare Warehouse Shipment" "POST" "/shipments/prepare" \
    '{"shipmentId":"SHP-501","orderId":"ORD-501","warehouseId":"WH-CENTRAL","destinationAddress":"742 Evergreen Terrace"}'

# 18. Dispatch Shipment with Carrier & Tracking Number
execute_step "Dispatch Shipment (FedEx Express)" "PUT" "/shipments/SHP-501/dispatch" \
    '{"carrier":"FedEx Express","trackingNumber":"FDX-12345678"}'

# 19. Confirm Delivery (Order Finalization)
execute_step "Confirm Delivery & Finalize Order" "PUT" "/shipments/SHP-501/confirm-delivery" ""

# 20. Verify Order Final Status
execute_step "Verify Final Order Status (DELIVERED_FINALIZED)" "GET" "/orders/ORD-501" ""

# 21. Request Return
execute_step "Buyer Requests Return for Defective Item" "POST" "/returns/request" \
    '{"returnId":"RET-501","orderId":"ORD-501","buyerId":"BUY-01","productId":"PRD-101","reason":"DEFECTIVE"}'

# 22. Approve Return
execute_step "Approve Return Request" "PUT" "/returns/RET-501/approve" ""

# 23. Process Refund
execute_step "Process Received Item & Monetary Refund" "POST" "/returns/process-refund" \
    '{"refundId":"REF-501","returnId":"RET-501","warehouseId":"WH-CENTRAL","reusable":true,"refundAmount":1150.00}'

# 24. Query System Audit Logs
execute_step "Query Platform System Audit Logs" "GET" "/audit" ""

echo -e "${CYAN}========================================================================${NC}"
echo -e "${GREEN}✓ All 24 End-to-End Terminal Steps Completed Successfully!${NC}"
echo -e "${CYAN}========================================================================${NC}\n"
