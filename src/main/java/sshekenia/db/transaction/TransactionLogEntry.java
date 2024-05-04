package sshekenia.db.transaction;

public record TransactionLogEntry(String key, String previousValue, String newValue) {
}
