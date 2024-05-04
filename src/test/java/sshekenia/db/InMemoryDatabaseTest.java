package sshekenia.db;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryDatabaseTest {
    InMemoryDatabase database = new InMemoryDatabase();

    @Test
    void shouldSaveKeys() {
        database.set("a", "10");
        assertThat(database.get("a")).isEqualTo("10");

        database.set("b", "20");
        assertThat(database.get("b")).isEqualTo("20");
    }

    @Test
    void shouldCountKeysWithTheSameValue() {
        database.set("a", "10");
        database.set("b", "10");
        database.set("c", "20");

        assertThat(database.count("10")).isEqualTo(2);
        assertThat(database.count("20")).isEqualTo(1);
        assertThat(database.count("30")).isEqualTo(0);
    }

    @Test
    void shouldRemoveRecordAndValueCounterForKey() {
        database.set("a", "10");
        assertThat(database.get("a")).isEqualTo("10");
        assertThat(database.count("10")).isEqualTo(1);

        database.delete("a");
        assertThat(database.get("a")).isEqualTo(null);
        assertThat(database.count("10")).isEqualTo(0);
    }

    @Test
    void shouldRemoveOldIndexOnValueChange() {
        database.set("a", "10");
        assertThat(database.count("10")).isEqualTo(1);

        database.set("a", "20");
        assertThat(database.count("10")).isEqualTo(0);
        assertThat(database.count("20")).isEqualTo(1);
    }
}