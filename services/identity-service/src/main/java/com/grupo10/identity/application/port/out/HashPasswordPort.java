package com.grupo10.identity.application.port.out;

public interface HashPasswordPort {

    String hash(String rawPassword);

    boolean verify(String rawPassword, String hash);
}
