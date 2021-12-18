package shipping.courier.specifications;

import shipping.courier.entities.Order;
import shipping.courier.entities.OrderSnapshot;

/**
 * Specification to check that {@link Order} must NOT have null or empty product!
 */
public final class NoEmptyProduct implements Specification<OrderSnapshot> {

    private static final String EMPTY_PRODUCT = "";

    @Override
    public boolean isSatisfiedBy(final OrderSnapshot entity) {
        return entity.getProduct() != null && !entity.getProduct().equals(EMPTY_PRODUCT);
    }
}
