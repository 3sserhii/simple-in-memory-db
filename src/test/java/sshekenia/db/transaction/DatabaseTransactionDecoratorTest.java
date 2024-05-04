package sshekenia.db.transaction;

import sshekenia.db.Database;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseTransactionDecoratorTest {
    @Mock
    TransactionManager transactionManager;
    @Mock
    Transaction transaction;
    @Mock
    Database database;

    @InjectMocks
    DatabaseTransactionDecorator decorator;

    @Test
    void shouldSaveDataDirectlyToStorageWithoutActiveTransaction() {
        decorator.set("a", "10");
        decorator.delete("b");

        verify(database).set("a", "10");
        verify(database).delete("b");
    }

    @Test
    void shouldExecuteReadOnlyOperationsWithoutTransaction() {
        when(database.get("a")).thenReturn("10");
        when(database.count("10")).thenReturn(1L);

        assertThat(decorator.get("a")).isEqualTo("10");
        assertThat(decorator.count("10")).isEqualTo(1L);
    }

    @Test
    void shouldAddModifyingOperationsToTransactionLog() {
        when(transactionManager.getCurrentTransaction()).thenReturn(transaction);
        when(database.get("b")).thenReturn("10");
        when(database.get("a")).thenReturn("5");

        decorator.set("a", "10");
        decorator.delete("b");

        InOrder inOrder = inOrder(transaction);
        inOrder.verify(transaction).logModification(new TransactionLogEntry("a", "5", "10"));
        inOrder.verify(transaction).logModification(new TransactionLogEntry("b", "10", null));
    }
}