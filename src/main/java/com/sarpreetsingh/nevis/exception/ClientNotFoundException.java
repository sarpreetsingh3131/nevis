package com.sarpreetsingh.nevis.exception;

import java.util.UUID;

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(UUID id) {
        super("Client [id=" + id + "] not found");
    }
}
