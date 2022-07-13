// Return the set of products that were ordered by all customers
fun Shop.getProductsOrderedByAll(): Set<Product> =
    customers.fold(
        customers.flatMap { customer -> customer.getOrderedProducts() }.toSet(),
        { orderedByAllCustomers, customer ->
            orderedByAllCustomers.intersect(customer.getOrderedProducts())
        })

fun Customer.getOrderedProducts(): List<Product> =
    orders.flatMap(Order::products)