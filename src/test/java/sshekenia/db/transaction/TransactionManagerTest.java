package sshekenia.db.transaction;

import sshekenia.db.Database;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class TransactionManagerTest {
    @Mock
    Database database;
    @InjectMocks
    TransactionManager transactionManager;

    @Test
    void shouldAddNewTransactionOnBegin() {
        transactionManager.begin();
        assertThat(transactionManager.getTransactions()).hasSize(1);

        transactionManager.begin();
        assertThat(transactionManager.getTransactions()).hasSize(2);
    }

    @Test
    void shouldClearAllActiveTransactionsOnCommit() {
        transactionManager.begin();
        transactionManager.begin();
        assertThat(transactionManager.getTransactions()).hasSize(2);

        transactionManager.commit();
        assertThat(transactionManager.getTransactions()).hasSize(0);
    }

    @Test
    void shouldRollbackLatestTransactionOnRollback() {
        transactionManager.begin();
        transactionManager.begin();
        assertThat(transactionManager.getTransactions()).hasSize(2);

        transactionManager.rollback();
        assertThat(transactionManager.getTransactions()).hasSize(1);
        transactionManager.rollback();
        assertThat(transactionManager.getTransactions()).hasSize(0);
    }

    @Test
    void shouldThrowAnExceptionOnCommitWithoutActiveTransactions() {
        assertThatThrownBy(transactionManager::commit)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("NO TRANSACTION");
    }

    @Test
    void shouldThrowAnExceptionOnRollbackWithoutActiveTransactions() {
        assertThatThrownBy(transactionManager::rollback)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("NO TRANSACTION");
    }
}