package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    private String nome;

    @OneToMany(mappedBy = "cidade", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Comercio> comercios;
}

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comercio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    private String nome;
    
    @NotNull
    private String responsavel;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TipoComercio tipo;

    @ManyToOne
    @JoinColumn(name = "cidade_id", nullable = false)
    private Cidade cidade;
}

public enum TipoComercio {
    FARMACIA, PADARIA, POSTO_GASOLINA, LANCHONETE
}

// Repositórios
@Repository
public interface CidadeRepository extends JpaRepository<Cidade, Long> {}

@Repository
public interface ComercioRepository extends JpaRepository<Comercio, Long> {}

// Serviços
@Service
public class CidadeService {
    @Autowired
    private CidadeRepository cidadeRepository;

    public List<Cidade> listarCidades() {
        return cidadeRepository.findAll();
    }

    public Cidade salvarCidade(Cidade cidade) {
        return cidadeRepository.save(cidade);
    }

    public Cidade atualizarCidade(Long id, Cidade cidade) {
        if (cidadeRepository.existsById(id)) {
            cidade.setId(id);
            return cidadeRepository.save(cidade);
        }
        return null;
    }

    public void deletarCidade(Long id) {
        cidadeRepository.deleteById(id);
    }

    public Cidade buscarPorId(Long id) {
        return cidadeRepository.findById(id).orElse(null);
    }
}

@Service
public class ComercioService {
    @Autowired
    private ComercioRepository comercioRepository;

    public List<Comercio> listarComercios() {
        return comercioRepository.findAll();
    }

    public Comercio salvarComercio(Comercio comercio) {
        return comercioRepository.save(comercio);
    }

    public Comercio atualizarComercio(Long id, Comercio comercio) {
        if (comercioRepository.existsById(id)) {
            comercio.setId(id);
            return comercioRepository.save(comercio);
        }
        return null;
    }

    public void deletarComercio(Long id) {
        comercioRepository.deleteById(id);
    }
}

// Controladores
@RestController
@RequestMapping("/cidades")
public class CidadeController {
    @Autowired
    private CidadeService cidadeService;

    @GetMapping
    public ResponseEntity<List<Cidade>> listar() {
        return ResponseEntity.ok(cidadeService.listarCidades());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cidade> buscarPorId(@PathVariable Long id) {
        Cidade cidade = cidadeService.buscarPorId(id);
        return cidade != null ? ResponseEntity.ok(cidade) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Cidade> adicionar(@Valid @RequestBody Cidade cidade) {
        return ResponseEntity.ok(cidadeService.salvarCidade(cidade));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cidade> atualizar(@PathVariable Long id, @Valid @RequestBody Cidade cidade) {
        Cidade atualizada = cidadeService.atualizarCidade(id, cidade);
        return atualizada != null ? ResponseEntity.ok(atualizada) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        cidadeService.deletarCidade(id);
        return ResponseEntity.noContent().build();
    }
}

@RestController
@RequestMapping("/comercios")
public class ComercioController {
    @Autowired
    private ComercioService comercioService;

    @GetMapping
    public ResponseEntity<List<Comercio>> listar() {
        return ResponseEntity.ok(comercioService.listarComercios());
    }

    @PostMapping
    public ResponseEntity<Comercio> adicionar(@Valid @RequestBody Comercio comercio) {
        return ResponseEntity.ok(comercioService.salvarComercio(comercio));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comercio> atualizar(@PathVariable Long id, @Valid @RequestBody Comercio comercio) {
        Comercio atualizado = comercioService.atualizarComercio(id, comercio);
        return atualizado != null ? ResponseEntity.ok(atualizado) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        comercioService.deletarComercio(id);
        return ResponseEntity.noContent().build();
    }
}
