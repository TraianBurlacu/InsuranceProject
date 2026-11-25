package com.endava.insuranceapp.infrastructure.persistence.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

@Entity
@Table(
        name = "clients",
        uniqueConstraints = @UniqueConstraint(name = "uk_clients_identifier", columnNames = "identifier")
)
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClientType type;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 50)
    private String identifier;

    @Column(length = 200)
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(length = 300)
    private String address;

    @Column(length = 255)
    private String primaryAddress;

    public Long getId() { return id; }

    public ClientType getType() { return type; }
    public void setType(ClientType type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPrimaryAddress() { return primaryAddress; }
    public void setPrimaryAddress(String primaryAddress) { this.primaryAddress = primaryAddress; }
}
