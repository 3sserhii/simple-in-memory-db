package sshekenia.db;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InMemoryDatabase implements Database {
    Map<String, String> records = new HashMap<>();
    Map<String, Set<String>> valueIndex = new HashMap<>();

    @Override
    public void set(String key, String value) {
        String previous = records.get(key);
        if (previous != null) {
            if (!previous.equals(value)) {
                valueIndex.get(previous).remove(key);
            }
        }

        valueIndex.computeIfAbsent(value, k -> new HashSet<>()).add(key);
        records.put(key, value);
    }

    @Override
    public String get(String key) {
        return records.get(key);
    }

    @Override
    public void delete(String key) {
        String value = records.get(key);
        if (value != null) {
            records.remove(key);
            valueIndex.get(value).remove(key);
        }
    }

    @Override
    public long count(String value) {
        Set<String> keys = valueIndex.get(value);
        if (keys == null) {
            return 0;
        }
        return keys.size();
    }
}
