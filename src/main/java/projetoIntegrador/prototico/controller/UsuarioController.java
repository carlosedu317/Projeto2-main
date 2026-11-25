package projetoIntegrador.prototico.controller;

import projetoIntegrador.prototico.repository.*;
import projetoIntegrador.prototico.model.*;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = {
    "http://localhost:5500",
    "http://127.0.0.1:5500"
})
public class UsuarioController {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository repository;

    public UsuarioController(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<Usuario> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {

        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Cadastro público: sempre cria CLIENTE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario criar(@RequestBody @Valid Usuario usuario) {

        if (repository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este e-mail já está cadastrado"
            );
        }

        usuario.setSenha(
                passwordEncoder.encode(usuario.getSenha())
        );

        usuario.setTipoUsuario(TipoUsuario.CLIENTE);

        return repository.save(usuario);
    }

    // Atualização administrativa
    @PutMapping("/{id}")
    public Usuario atualizar(
            @PathVariable Long id,
            @RequestBody @Valid Usuario usuario) {

        Usuario usuarioExistente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado"
                ));

        usuarioExistente.setNome(usuario.getNome());
        usuarioExistente.setTelefone(usuario.getTelefone());
        usuarioExistente.setEmail(usuario.getEmail());
        usuarioExistente.setEndereco(usuario.getEndereco());
        usuarioExistente.setTipoUsuario(usuario.getTipoUsuario());

        usuarioExistente.setSenha(
                passwordEncoder.encode(usuario.getSenha())
        );

        return repository.save(usuarioExistente);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {

        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Usuário não encontrado"
            );
        }

        repository.deleteById(id);
    }
}
