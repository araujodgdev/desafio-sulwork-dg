package br.com.dgdev.sulwork.cafe.service;

import br.com.dgdev.sulwork.cafe.dto.CollaboratorDTO;
import br.com.dgdev.sulwork.cafe.repository.CollaboratorRepository;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class CollaboratorService {
    
    private final CollaboratorRepository collaboratorRepository;

    public CollaboratorService(CollaboratorRepository collaboratorRepository) {
        this.collaboratorRepository = collaboratorRepository;
    }

    public Optional<CollaboratorDTO> findCollaboratorByCpf(String cpf) {
        return collaboratorRepository.findCollaboratorByCpf(normalizeCpf(cpf));
    }

    public Long findOrCreateCollaborator(String name, String cpf) {
        return collaboratorRepository.findOrCreateCollaborator(name.trim(), normalizeCpf(cpf));
    }

    private String normalizeCpf(String cpf) {
        return cpf.replaceAll("\\D", "");
    }
}
