package sshekenia.db.transaction;

import sshekenia.db.Database;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DatabaseTransactionDecorator implements Database {
    private final TransactionManager transactionManager;
    private final Database database;

    @Override
    public void set(String key, String value) {
        Transaction currentTransaction = transactionManager.getCurrentTransaction();
        if (currentTransaction != null) {
            String previousValue = database.get(key);
            currentTransaction.logModification(new TransactionLogEntry(key, previousValue, value));
        }
        database.set(key, value);
    }

    @Override
    public String get(String key) {
        return database.get(key);
    }

    @Override
    public void delete(String key) {
        Transaction currentTransaction = transactionManager.getCurrentTransaction();
        if (currentTransaction != null) {
            String previousValue = database.get(key);
            if (previousValue != null) {
                currentTransaction.logModification(new TransactionLogEntry(key, previousValue, null));
            }
        }
        database.delete(key);
    }

    @Override
    public long count(String value) {
        return database.count(value);
    }
}
