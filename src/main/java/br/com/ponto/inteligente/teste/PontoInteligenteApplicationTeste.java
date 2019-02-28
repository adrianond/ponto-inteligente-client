package br.com.ponto.inteligente.teste;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import br.com.dantas.ponto.inteligente.constantes.PontoInteligenteApplicationConstants;
import br.com.dantas.ponto.inteligente.dto.EmpresaDto;
import br.com.dantas.ponto.inteligente.exception.PontoInteligenteApplicationException;
import br.com.dantas.ponto.inteligente.service.ConsultarEmpresaCadastrada;
import br.com.dantas.ponto.inteligente.service.PontoInteligenteService;

public class PontoInteligenteApplicationTeste {

	public static void main(String[] args) throws PontoInteligenteApplicationException, JsonParseException, JsonMappingException, IOException {

		PontoInteligenteService servico = new PontoInteligenteService();
		ConsultarEmpresaCadastrada consultarEmpresaCadastrada = new ConsultarEmpresaCadastrada();
		EmpresaDto empresa = null;
		
		try {
			//empresa = servico.sendGetPontoInteligenteApi(PontoInteligenteApplicationConstants.URI,PontoInteligenteApplicationConstants.RECURSO);
			
			empresa = consultarEmpresaCadastrada.consultarEmpresa(PontoInteligenteApplicationConstants.URI,PontoInteligenteApplicationConstants.RECURSO);
			
			
			System.out.print("Dados da empresa:"+"ID:"+empresa.getId()+" "+"Razão Social:"+empresa.getRazaoSocial()+" "+"CNPJ:"+empresa.getCnpj());
		} catch (PontoInteligenteApplicationException e) {
			System.out.println(e.getMenssagem() + e.getCausa());
		}
	}

}
