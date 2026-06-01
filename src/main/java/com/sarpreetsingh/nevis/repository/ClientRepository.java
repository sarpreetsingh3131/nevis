package com.sarpreetsingh.nevis.repository;

import com.sarpreetsingh.nevis.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {

    boolean existsByEmail(String email);
}
