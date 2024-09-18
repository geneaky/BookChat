package toy.bookchat.bookchat.domain.common;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.support.RepositorySupport;

class RepositorySupportTest extends RepositoryTest {

  @Test
  void DESC_정렬조건_조회_성공() throws Exception {
    PageRequest pageRequest = PageRequest.of(0, 1, Sort.by("id").descending());
    QTest test = new QTest("test");
    OrderSpecifier[] orderSpecifiers = RepositorySupport.extractOrderSpecifierFrom(test,
        pageRequest);

    Order resultOrder = orderSpecifiers[0].getOrder();
    Expression resultTarget = orderSpecifiers[0].getTarget();

    assertThat(resultOrder).isEqualTo(Order.DESC);
    assertThat(resultTarget).isEqualTo(test.id);
  }

  @Test
  void ASC_정렬조건_조회_성공() throws Exception {
    PageRequest pageRequest = PageRequest.of(0, 1, Sort.by("id").ascending());
    QTest test = new QTest("test");
    OrderSpecifier[] orderSpecifiers = RepositorySupport.extractOrderSpecifierFrom(
        test,
        pageRequest);

    Order resultOrder = orderSpecifiers[0].getOrder();
    Expression resultTarget = orderSpecifiers[0].getTarget();

    assertThat(resultOrder).isEqualTo(Order.ASC);
    assertThat(resultTarget).isEqualTo(test.id);
  }

  public class SupportTest {

    private Long id;
  }

  public class QTest extends EntityPathBase<SupportTest> {

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QTest(String variable) {
      super(SupportTest.class, forVariable(variable));
    }
  }
}