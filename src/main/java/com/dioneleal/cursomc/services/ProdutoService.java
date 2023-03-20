package com.dioneleal.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.dioneleal.cursomc.domain.Categoria;
import com.dioneleal.cursomc.domain.Produto;
import com.dioneleal.cursomc.repositories.CategoriaRepository;
import com.dioneleal.cursomc.repositories.ProdutoRepository;
import com.dioneleal.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ProdutoService {

	@Autowired
	private CategoriaRepository repository;

	@Autowired
	private ProdutoRepository repo;

	public Produto find(Integer id) {

		Optional<Produto> categoria = repo.findById(id);

		return categoria.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Produto.class.getName()));
	}

	public Page<Produto> search(String nome, List<Integer> ids, Integer page, Integer linesPerPage, String OrderBy,
			String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), OrderBy);
		List<Categoria> categorias = repository.findAllById(ids);
		return repo.findDistinctByNomeContainingAndCategoriasIn(nome, categorias, pageRequest);
	}

}
