package toy.bookchat.bookchat.domain.common;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public abstract class RepositorySupport {

    public static <T> OrderSpecifier[] extractOrderSpecifierFrom(final EntityPathBase<T> qClass,
        final Pageable pageable) {
        return pageable.getSort()
            .stream()
            .map(sort -> toOrderSpecifier(qClass, sort))
            .collect(Collectors.toList()).toArray(OrderSpecifier[]::new);
    }

    private static <T> OrderSpecifier toOrderSpecifier(final EntityPathBase<T> qClass,
        final Sort.Order sortOrder) {
        final Order order = toOrder(sortOrder);
        final PathBuilder<T> pathBuilder = new PathBuilder<>(qClass.getType(),
            qClass.getMetadata());
        return new OrderSpecifier(order, pathBuilder.get(sortOrder.getProperty()));
    }

    private static Order toOrder(final Sort.Order sortOrder) {
        if (sortOrder.isAscending()) {
            return Order.ASC;
        }
        return Order.DESC;
    }
}
