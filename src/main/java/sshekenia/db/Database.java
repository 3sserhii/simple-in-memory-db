package sshekenia.db;

public interface Database {
    void set(String key, String value);

    String get(String key);

    void delete(String key);

    long count(String value);
}
