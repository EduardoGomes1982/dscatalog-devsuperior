package com.devsuperior.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.RoleDTO;
import com.devsuperior.dscatalog.entities.Role;
import com.devsuperior.dscatalog.repositories.RoleRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class RoleService {
	@Autowired
	private RoleRepository repository;

	@Transactional(readOnly = true)
	public Page<RoleDTO> findAllPaged(Pageable pageable) {
		Page<Role> list = repository.findAll(pageable);
		return list.map(c -> new RoleDTO(c));
	}

	@Transactional(readOnly = true)
	public RoleDTO findByID(Long id) {
		Optional<Role> obj = repository.findById(id);
		Role entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new RoleDTO(entity);
	}

	@Transactional(readOnly = false)
	public RoleDTO insert(RoleDTO dto) {
		Role entity = new Role(null, dto.getAuthority());
		entity = repository.save(entity);
		return new RoleDTO(entity);
	}

	@Transactional(readOnly = false)
	public RoleDTO update(Long id, RoleDTO dto) {
		try {
			Role entity = repository.getOne(id);
			entity.setAuthority(dto.getAuthority());
			entity = repository.save(entity);
			return new RoleDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Entity not found " + id);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity Violation");
		}
	}
}
