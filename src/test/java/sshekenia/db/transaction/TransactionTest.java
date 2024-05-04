package sshekenia.db.transaction;

import sshekenia.db.Database;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class TransactionTest {
    @Mock
    Database database;
    @InjectMocks
    Transaction transaction;

    @Test
    void shouldRollbackAllModificationsInReverseOrder() {
        transaction.logModification(new TransactionLogEntry("a", null, "10"));
        transaction.logModification(new TransactionLogEntry("a", "10", "20"));
        transaction.logModification(new TransactionLogEntry("a", "20", null));

        transaction.rollback();

        InOrder order = inOrder(database);

        order.verify(database).set("a", "20");
        order.verify(database).set("a", "10");
        order.verify(database).delete("a");
    }
}