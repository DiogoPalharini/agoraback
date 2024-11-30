package com.example.api2024.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.api2024.service.HistoricoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.example.api2024.entity.Adm;
import com.example.api2024.repository.AdmRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3030"})
@RestController
@RequestMapping("/adm")
public class AdmController {

    @Autowired
    private AdmRepository admRepository;

     @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HistoricoService historicoService;

    @Autowired
    private ObjectMapper objectMapper;

    // Listar todos os administradores
    @GetMapping("/listar")
    public List<Adm> listarAdm() {
        return admRepository.findAll();
    }
    
    // Obter informações do administrador pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<Adm> getAdmById(@PathVariable Long id) {
        Optional<Adm> adm = admRepository.findById(id);
        if (adm.isPresent()) {
            return ResponseEntity.ok(adm.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // Obter o tipo do administrador pelo email
    @GetMapping("/{email}/tipo")
    public String getTipo(@PathVariable String email) throws Exception {
        return admRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Adm não encontrado"))
                .getTipo();
    }

    // Obter informações do administrador pelo email
    @GetMapping("/{email}/infoAdm")
    public Adm getInfoAdm(@PathVariable String email) throws Exception {
        return admRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Adm não encontrado"));
    }

    // Método de criação de administrador com token
    @PostMapping("/criar")
    public ResponseEntity<Map<String, String>> criarAdm(
        @RequestBody Adm novoAdm,
        @RequestParam Long idSuperAdm) throws JsonProcessingException {
    Optional<Adm> superAdm = admRepository.findById(idSuperAdm);

    if (superAdm.isEmpty() || !"1".equals(superAdm.get().getTipo())) {
        return ResponseEntity.status(403).body(Map.of("message", "Acesso negado: Apenas super administradores podem criar novos administradores."));
    }

    novoAdm.setSenha("12345678");

    String token = UUID.randomUUID().toString();
    novoAdm.setTokenRedefinicao(token);
    Adm novoAdministrador = admRepository.save(novoAdm);

    // Retorna a resposta imediatamente
    ResponseEntity<Map<String, String>> response = ResponseEntity.ok(Map.of("message", "Administrador criado com sucesso!"));

    // Envia o e-mail de forma assíncrona
    CompletableFuture.runAsync(() -> {
        try {
            enviarEmailBoasVindas(novoAdm.getEmail(), token);
        } catch (MessagingException e) {
            System.err.println("Erro ao enviar e-mail: " + e.getMessage());
        }
    });

        historicoService.cadastrarHistorico(
            idSuperAdm,
            "criacao",
            "admin",
            novoAdministrador.getId(),
            objectMapper.writeValueAsString(novoAdministrador),
            null
    );
        return response;
}
    
    private void enviarEmailBoasVindas(String emailDestino, String token) throws MessagingException {
        MimeMessage mensagem = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensagem, "utf-8");
    
        String mensagemHtml = "<h3>Bem-vindo ao sistema!</h3>"
                + "<p>Olá, você foi cadastrado como administrador em nosso sistema.</p>"
                + "<p><a href='http://localhost:5173/redefinir-senha?token=" + token + "'>Clique aqui para redefinir sua senha</a></p>";
    
        helper.setTo(emailDestino);
        helper.setSubject("Bem-vindo ao Sistema - Redefinição de Senha");
        helper.setText(mensagemHtml, true);
        helper.setFrom("adm06887@gmail.com");
    
        mailSender.send(mensagem);
    }
    
    // Método para redefinir senha usando o token
    @PostMapping("/redefinir-senha")
    public ResponseEntity<String> redefinirSenha(
            @RequestParam String token,
            @RequestBody String novaSenha) {
        Optional<Adm> admOpt = admRepository.findByTokenRedefinicao(token);
    
        if (admOpt.isEmpty()) {
            return ResponseEntity.status(400).body("Token inválido.");
        }
    
        Adm adm = admOpt.get();
        if (adm.getIsSenhaRedefinida()) {
            return ResponseEntity.status(400).body("A senha já foi redefinida anteriormente.");
        }
    
        adm.setSenha(passwordEncoder.encode(novaSenha));
        adm.setIsSenhaRedefinida(true);
        adm.setTokenRedefinicao(null); // Invalida o token
        admRepository.save(adm);
    
        return ResponseEntity.ok("Senha redefinida com sucesso!");
    }
    
    
 // Atualizar administrador por ID (somente super administrador)
    @PutMapping("/{id}")
    public ResponseEntity<String> atualizarAdm(
            @PathVariable Long id,
            @RequestBody Adm admAtualizado,
            @RequestParam Long idSuperAdm) throws JsonProcessingException {

        Optional<Adm> superAdm = admRepository.findById(idSuperAdm);

        // Verifica se o solicitante é um super administrador (tipo == '1')
        if (superAdm.isEmpty() || !"1".equals(superAdm.get().getTipo())) {
            return ResponseEntity.status(403)
                    .body("Acesso negado: Apenas super administradores podem atualizar administradores.");
        }

        Optional<Adm> admExistente = admRepository.findById(id);
        if (admExistente.isPresent()) {
            Adm adm = admExistente.get();
            adm.setNome(admAtualizado.getNome());
            adm.setEmail(admAtualizado.getEmail());
            adm.setCpf(admAtualizado.getCpf());
            adm.setTelefone(admAtualizado.getTelefone());
            adm.setSenha(admAtualizado.getSenha());
            adm.setTipo(admAtualizado.getTipo());
            adm.setAtivo(admAtualizado.getAtivo());
            Adm novoAdministrador = admRepository.save(adm);

            historicoService.cadastrarHistorico(
                    idSuperAdm,
                    "edicao",
                    "admin",
                    novoAdministrador.getId(),
                    objectMapper.writeValueAsString(novoAdministrador),
                    null
            );

            return ResponseEntity.ok("Administrador atualizado com sucesso!");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Atualizar o status ativo do administrador por ID
    @PatchMapping("/atualizarStatus/{id}/{idSuperAdm}")
    public ResponseEntity<String> patchStatus(@PathVariable Long id, @PathVariable Long idSuperAdm, @RequestBody Adm admStatus) throws JsonProcessingException {
        Optional<Adm> admExistente = admRepository.findById(id);

        if (admExistente.isPresent()) {
            Adm adm = admExistente.get();
            Boolean novoStatus = admStatus.getAtivo();

            if (novoStatus == null) {
                return ResponseEntity.badRequest().body("O valor de 'ativo' deve ser fornecido.");
            }

            adm.setAtivo(novoStatus);
            admRepository.save(adm);

            String status = novoStatus ? "ativado" : "desativado";
            String alteracao = novoStatus ? "ativacao" : "desativacao";

            historicoService.cadastrarHistorico(
                    idSuperAdm,
                    alteracao,
                    "admin",
                    adm.getId(),
                    objectMapper.writeValueAsString(adm),
                    null
            );

            return ResponseEntity.ok("Administrador " + status + " com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Administrador não encontrado.");
        }
    }


    // Excluir administrador por ID (somente super administrador)
    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<String> excluirAdm(
            @PathVariable Long id,
            @RequestParam Long idSuperAdm) throws JsonProcessingException {

        Optional<Adm> superAdm = admRepository.findById(idSuperAdm);

        // Verifica se o usuário solicitante é um super administrador (tipo == '1')
        if (superAdm.isEmpty() || !"1".equals(superAdm.get().getTipo())) {
            return ResponseEntity.status(403)
                    .body("Acesso negado: Apenas super administradores podem excluir administradores.");
        }

        Optional<Adm> administrador = admRepository.findById(id);

        historicoService.cadastrarHistorico(
                idSuperAdm,
                "delecao",
                "admin",
                id,
                objectMapper.writeValueAsString(administrador),
                null
        );

        admRepository.deleteById(id);
        return ResponseEntity.ok("Administrador excluído com sucesso.");
    }
}
