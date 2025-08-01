package com.customeronboarding.admin.service;

import com.customeronboarding.admin.dto.DashboardMetricsDTO;
import com.customeronboarding.admin.dto.KycReverifyRequestDTO;
import com.customeronboarding.admin.dto.KycStatusResponseDTO;
import com.customeronboarding.admin.entity.Customer;
import com.customeronboarding.admin.entity.KycDocuments;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminService {

    String updateKycStatus(KycStatusResponseDTO request);

    KycStatusResponseDTO getKycStatus(Long customerId);

    List<KycStatusResponseDTO> getAllKycByStatus(String status);

    List<Customer> getAllCustomers();

    String deleteCustomer(Long customerId);

    KycDocuments getKycDocuments(Long customerId);

    List<Customer> getCustomersWithoutKyc();

    String markKycReverifyRequired(KycReverifyRequestDTO request);

    List<Customer> searchCustomersByName(String name);
    List<Customer> searchCustomersByEmail(String email);

    DashboardMetricsDTO getDashboardMetrics();

}
