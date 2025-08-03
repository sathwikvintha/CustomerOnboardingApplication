package com.customeronboarding.customer.repository;

import java.util.Optional;
import com.customeronboarding.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmail(String email);
    boolean existsByUserId(Long userId);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByUserId(Long userId);
    @Query("SELECT c FROM Customer c JOIN c.user u WHERE u.username = :username")
    Optional<Customer> findByUsername(@Param("username") String username);
//    Optional<Customer> findByUser_Username(String username);


}
