package org.chainoptim.desktop.shared.enums;

public enum Feature {

    // Products
    PRODUCT,
    PRODUCT_STAGE,
    COMPONENT,
    UNIT_OF_MEASUREMENT,

    // Factories
    FACTORY,
    FACTORY_STAGE,
    RESOURCE_ALLOCATION_PLAN,
    FACTORY_INVENTORY,
    FACTORY_PRODUCTION_HISTORY,
    FACTORY_PERFORMANCE,

    // Warehouses
    WAREHOUSE,
    WAREHOUSE_INVENTORY,


    // Suppliers
    SUPPLIER,
    SUPPLIER_ORDER,
    SUPPLIER_SHIPMENT,
    SUPPLIER_PERFORMANCE,

    // Clients
    CLIENT,
    CLIENT_ORDER,
    CLIENT_SHIPMENT,
    CLIENT_EVALUATION;

    @Override
    public String toString() {
        return (name().charAt(0) + name().substring(1).toLowerCase()).replace("_", " ");
    }
}
