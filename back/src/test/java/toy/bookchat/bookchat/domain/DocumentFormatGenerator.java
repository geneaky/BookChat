package toy.bookchat.bookchat.domain;

import org.springframework.restdocs.snippet.Attributes;

import static org.springframework.restdocs.snippet.Attributes.key;

public interface DocumentFormatGenerator {

    static Attributes.Attribute getConstraints(String key, String value) {
        return key(key).value(value);
    }
}
