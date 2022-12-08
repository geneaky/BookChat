package toy.bookchat.bookchat.config.log;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import java.util.Locale;
import org.hibernate.engine.jdbc.internal.FormatStyle;

public class CustomP6pySqlFormat implements MessageFormattingStrategy {

    private static String chooseDDLOrBasicFormatter(String sql, String tmpsql) {
        if (isDDL(tmpsql)) {
            return FormatStyle.DDL.getFormatter().format(sql);
        }
        return FormatStyle.HIGHLIGHT.getFormatter()
            .format(FormatStyle.BASIC.getFormatter().format(sql));
    }

    private static boolean isDDL(String tmpsql) {
        return tmpsql.startsWith("create") || tmpsql.startsWith("alter") || tmpsql.startsWith(
            "comment");
    }

    private static boolean hasNotSql(String sql) {
        return sql == null || sql.trim().equals("");
    }

    private static boolean isStatement(String category) {
        return Category.STATEMENT.getName().equals(category);
    }

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category,
        String prepared, String sql, String url) {
        sql = formatSql(category, sql);
        return now + "|" + elapsed + "ms|" + category + "|connection " + connectionId + "|" + sql;
    }

    private String formatSql(String category, String sql) {
        if (hasNotSql(sql)) {
            return sql;
        }

        // Only format Statement, distinguish DDL And DML
        if (isStatement(category)) {
            sql = chooseDDLOrBasicFormatter(sql, sql.trim().toLowerCase(Locale.ROOT));
        }

        return sql;
    }
}
