package uk.co.roteala.dataprocessingcommons.storages;

public interface StorageOperations<E> {
    void add(String column);

    void delete();

    void exists();
}
