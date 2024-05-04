package sshekenia.db.transaction;

import sshekenia.db.Database;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;

@RequiredArgsConstructor
public class Transaction {
    private final Database database;
    private final LinkedList<TransactionLogEntry> transactionLog = new LinkedList<>();

    public void logModification(TransactionLogEntry entry) {
        transactionLog.add(entry);
    }

    public void rollback() {
        while (!transactionLog.isEmpty()) {
            TransactionLogEntry lastOperation = transactionLog.pollLast();
            if (lastOperation.previousValue() == null) {
                database.delete(lastOperation.key());
            } else {
                database.set(lastOperation.key(), lastOperation.previousValue());
            }
        }
    }
}
