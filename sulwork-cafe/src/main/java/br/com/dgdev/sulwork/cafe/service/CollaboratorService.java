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
        return collaboratorRepository.findCollaboratorByCpf(cpf);
    }

    public Long insertNewCollaborator(String name, String cpf) {
        Optional<CollaboratorDTO> existingCollaborator = collaboratorRepository.findCollaboratorByCpf(cpf);
        if (existingCollaborator.isPresent()) {
            throw new IllegalArgumentException("Colaborador já cadastrado!");
        }
        return collaboratorRepository.insertNewCollaborator(name, cpf);
    }
}
