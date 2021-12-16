package shipping.courier.entities;

import com.fasterxml.jackson.annotation.*;
import shipping.courier.specifications.NoEmptyProduct;

import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Abstract class to construct a generic {@link Order}.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PlacedOrder.class, name = "placed")
})
public abstract class AbstractOrder implements Order {

    private static int fakeId = 0;
    private final transient String id;
    private final transient Date orderDate;
    private final transient String product;

    /**
     * Construct a generic Order.
     * @param currentOrder the current state of a generic Order
     */
    protected AbstractOrder(final OrderSnapshot currentOrder) {
        if (!new NoEmptyProduct().isSatisfiedBy(currentOrder))
            throw new IllegalArgumentException("Product MUST NOT be null or empty!");

        this.product = currentOrder.getProduct();
        this.id = currentOrder.getId() + fakeId++; // TODO think about id generation
        this.orderDate = currentOrder.getOrderDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderSnapshot getSnapshot() {
        return new OrderSnapshot(this.id, this.product, this.orderDate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", "Order" + "[", "]")
                .add("id='" + id + "'")
                .add("orderDate=" + orderDate)
                .add("product='" + product + "'")
                .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractOrder that = (AbstractOrder) o;
        return id.equals(that.id) && orderDate.equals(that.orderDate) && product.equals(that.product);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, orderDate, product);
    }
}
