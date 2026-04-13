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

    // Required by JPA
    protected Bank() {}

    private Bank(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.apiEndpoint = builder.apiEndpoint;
        this.connections = builder.connections;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String apiEndpoint;
        private List<BankConnection> connections;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder apiEndpoint(String apiEndpoint) { this.apiEndpoint = apiEndpoint; return this; }
        public Builder connections(List<BankConnection> connections) { this.connections = connections; return this; }

        public Bank build() { return new Bank(this); }
    }

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
