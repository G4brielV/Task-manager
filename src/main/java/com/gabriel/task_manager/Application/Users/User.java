package com.gabriel.task_manager.Application.Users;

import jakarta.persistence.*;
import lombok.*;


@Builder
@Table(name = "users")
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of="id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    public void atualizarSenha(String senhaCriptografada) {
        this.password = senhaCriptografada;
    }

}
