package sshekenia.db.transaction;

import sshekenia.db.Database;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
public class TransactionManager {
    private final Database database;
    private final LinkedList<Transaction> transactions = new LinkedList<>();

    public void begin() {
        transactions.add(new Transaction(database));
    }

    public void commit() {
        validateActiveTransaction();
        transactions.clear();
    }

    public void rollback() {
        validateActiveTransaction();
        transactions.pollLast().rollback();
    }

    private void validateActiveTransaction() {
        if (transactions.isEmpty()) {
            throw new IllegalStateException("NO TRANSACTION");
        }
    }

    public Transaction getCurrentTransaction() {
        return transactions.peekLast();
    }

    List<Transaction> getTransactions() {
        return transactions;
    }
}
