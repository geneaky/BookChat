package toy.bookchat.bookchat.config.query;

import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class MySqlCustomDialect extends MySQL8Dialect {

    public MySqlCustomDialect() {
        registerFunction("group_concat",
            new StandardSQLFunction("group_concat", StandardBasicTypes.STRING));
    }
}
