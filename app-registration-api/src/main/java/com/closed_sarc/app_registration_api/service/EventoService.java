package com.closed_sarc.app_registration_api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.closed_sarc.app_registration_api.domain.entities.Evento;
import com.closed_sarc.app_registration_api.domain.entities.Usuario;
import com.closed_sarc.app_registration_api.domain.repositories.EventoRepository;
import com.closed_sarc.app_registration_api.domain.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventoService {
    
    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;

    public Evento create(Evento evento) {
        try {
            if (evento.getTitulo() == null || evento.getTitulo().trim().isEmpty()) {
                throw new RuntimeException("Título do evento é obrigatório");
            }
            if (evento.getUsuario() != null && evento.getUsuario().getId() != null) {
                Usuario usuario = usuarioRepository.findById(evento.getUsuario().getId())
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
                evento.setUsuario(usuario);
            }
            
            Evento saved = eventoRepository.save(evento);
            return saved;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar evento: " + e.getMessage());
        }
    }

    public List<Evento> findAll() {
        try {
            return eventoRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar eventos: " + e.getMessage());
        }
    }

    public Evento findById(UUID id) {
        try {
            return eventoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Evento não encontrado"));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar evento por ID");
        }
    }

    public void delete(UUID id) {
        try {
            if (!eventoRepository.existsById(id)) {
                throw new RuntimeException("Evento não encontrado");
            }
            eventoRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir evento: " + e.getMessage());
        }
    }
    
    public List<Evento> findByUsuarioId(UUID usuarioId) {
        try {
            return eventoRepository.findByUsuarioId(usuarioId);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar eventos por usuário");
        }
    }
}