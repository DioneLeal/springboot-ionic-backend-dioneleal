package com.dioneleal.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dioneleal.cursomc.domain.Cliente;
import com.dioneleal.cursomc.domain.ItemPedido;
import com.dioneleal.cursomc.domain.PagamentoComBoleto;
import com.dioneleal.cursomc.domain.Pedido;
import com.dioneleal.cursomc.domain.enums.EstadoPagamento;
import com.dioneleal.cursomc.repositories.ItemPedidoRepository;
import com.dioneleal.cursomc.repositories.PagamentoRepository;
import com.dioneleal.cursomc.repositories.PedidoRepository;
import com.dioneleal.cursomc.security.UserSS;
import com.dioneleal.cursomc.services.exceptions.AuthorizationException;
import com.dioneleal.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository repo;

	@Autowired
	private BoletoService service;

	@Autowired
	private PagamentoRepository pagamentoRepository;

	@Autowired
	ProdutoService produtoService;

	@Autowired
	ItemPedidoRepository itemPedidoRepository;

	@Autowired
	private ClienteService clienteService;

	public Pedido find(Integer id) {

		Optional<Pedido> categoria = repo.findById(id);

		return categoria.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName()));
	}

	@Transactional
	public Pedido insert(Pedido obj) {
		obj.setId(null);
		obj.setCliente(clienteService.find(obj.getCliente().getId()));
		obj.setInstante(new Date());
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		if (obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto boleto = (PagamentoComBoleto) obj.getPagamento();
			service.preencherPagamentoComBoleto(boleto, obj.getInstante());
		}
		obj = repo.save(obj);
		pagamentoRepository.save(obj.getPagamento());
		for (ItemPedido ip : obj.getItens()) {
			ip.setDesconto(0.0);
			ip.setProduto(produtoService.find(ip.getProduto().getId()));
			ip.setPreco(ip.getProduto().getPreco());
			ip.setPedido(obj);
		}
		itemPedidoRepository.saveAll(obj.getItens());
		return obj;
	}

	public Page<Pedido> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		UserSS user = UserService.authenticated();
		if (user == null) {
			throw new AuthorizationException("Acesso negado");
		}
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		Cliente cliente = clienteService.find(user.getId());
		return repo.findByCliente(cliente, pageRequest);
	}
}
