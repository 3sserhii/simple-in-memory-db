package sshekenia.db;

import sshekenia.db.transaction.DatabaseTransactionDecorator;
import sshekenia.db.transaction.TransactionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InMemoryDatabaseFunctionalTest {
    Database database;
    TransactionManager transactionManager;

    @BeforeEach
    void setUp() {
        InMemoryDatabase storage = new InMemoryDatabase();
        transactionManager = new TransactionManager(storage);
        database = new DatabaseTransactionDecorator(transactionManager, storage);
    }

    @Test
    void testBasicOperationsWithoutTransaction() {
        database.set("a", "10");
        assertThat(database.get("a")).isEqualTo("10");

        database.delete("a");
        assertThat(database.get("a")).isNull();
    }

    @Test
    void testCountWithoutTransaction() {
        database.set("a", "10");
        database.set("b", "10");

        assertThat(database.count("10")).isEqualTo(2);
        assertThat(database.count("20")).isEqualTo(0);

        database.delete("a");
        assertThat(database.count("10")).isEqualTo(1);

        database.set("b", "30");
        assertThat(database.count("10")).isEqualTo(0);
        assertThat(database.count("30")).isEqualTo(1);
    }

    @Test
    void testTransactionRollback() {
        transactionManager.begin();

        database.set("a", "10");
        assertThat(database.get("a")).isEqualTo("10");

        transactionManager.rollback();

        assertThat(database.get("a")).isNull();
    }

    @Test
    void testNestedTransactionRollback() {
        transactionManager.begin();

        database.set("a", "10");
        assertThat(database.get("a")).isEqualTo("10");

        transactionManager.begin();

        database.set("a", "20");
        assertThat(database.get("a")).isEqualTo("20");

        transactionManager.rollback();

        assertThat(database.get("a")).isEqualTo("10");

        transactionManager.rollback();

        assertThat(database.get("a")).isNull();
    }

    @Test
    void testCommitInNestedTransaction() {
        transactionManager.begin();

        database.set("a", "30");
        assertThat(database.get("a")).isEqualTo("30");

        transactionManager.begin();

        database.set("a", "40");
        assertThat(database.get("a")).isEqualTo("40");

        transactionManager.commit();

        assertThat(database.get("a")).isEqualTo("40");

        assertThatThrownBy(transactionManager::rollback)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("NO TRANSACTION");
    }

    @Test
    void testDeletionRollbackInNestedTransaction() {
        database.set("a", "50");

        transactionManager.begin();
        assertThat(database.get("a")).isEqualTo("50");
        database.set("a", "60");

        transactionManager.begin();
        database.delete("a");
        assertThat(database.get("a")).isNull();

        transactionManager.rollback();
        assertThat(database.get("a")).isEqualTo("60");

        transactionManager.rollback();
        assertThat(database.get("a")).isEqualTo("50");
    }

    @Test
    void testCountWithTransactionsRollback() {
        database.set("a", "10");

        transactionManager.begin();
        assertThat(database.count("10")).isEqualTo(1);

        transactionManager.begin();
        database.delete("a");
        assertThat(database.count("10")).isEqualTo(0);

        transactionManager.rollback();
        assertThat(database.count("10")).isEqualTo(1);
    }
}
