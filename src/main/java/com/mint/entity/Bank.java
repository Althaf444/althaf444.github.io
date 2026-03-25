package com.mint.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "banks")
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String apiEndpoint;

    // QUAN HỆ 1-NHIỀU: 1 Ngân hàng có thể có nhiều lượt liên kết tài khoản
    @OneToMany(mappedBy = "bank", cascade = CascadeType.ALL)
    private List<BankConnection> connections;

    // --- Getters và Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getApiEndpoint() { return apiEndpoint; }
    public void setApiEndpoint(String apiEndpoint) { this.apiEndpoint = apiEndpoint; }

    public List<BankConnection> getConnections() { return connections; }
    public void setConnections(List<BankConnection> connections) { this.connections = connections; }
}
