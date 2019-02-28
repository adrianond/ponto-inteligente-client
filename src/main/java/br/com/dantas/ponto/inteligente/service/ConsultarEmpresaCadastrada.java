package br.com.dantas.ponto.inteligente.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import br.com.dantas.ponto.inteligente.dto.EmpresaDto;
import br.com.dantas.ponto.inteligente.exception.PontoInteligenteApplicationException;
import br.com.dantas.ponto.inteligente.response.Response;


public class ConsultarEmpresaCadastrada {

	private StringBuffer response = null;
	private HttpResponse resposta = null;
	private String jsonRetorno = "";
	private ObjectMapper mapperObj = null;
	Response<EmpresaDto> empresaDto = null;

	
	public EmpresaDto consultarEmpresa(String uri, String recurso) throws PontoInteligenteApplicationException{

		String url = uri.concat(recurso);
		response = new StringBuffer();
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet httpPost = new HttpGet(url);

		try {
			resposta = (HttpResponse) client.execute(httpPost);

			if (resposta.getStatusLine().getStatusCode() != 200) {
				if (resposta.getStatusLine().getStatusCode() == 400) {
					throw new PontoInteligenteApplicationException("Código do erro: " + String.valueOf(resposta.getStatusLine().getStatusCode() + " :"),
							"Ocorreu erro na consulta da empresa cadastrada: " + consultarEmpresaCadastrada(resposta).getErrors());
				} else {
					throw new PontoInteligenteApplicationException("Ocorreu erro na consulta da empresa cadastrada: ", 
							"[Código do erro: " + String.valueOf(resposta.getStatusLine().getStatusCode()) + "]");
				}
			} 
			empresaDto = consultarEmpresaCadastrada(resposta);
		} catch (PontoInteligenteApplicationException e) {
			throw new PontoInteligenteApplicationException(e.getMenssagem(), e.getCausa());
		}catch (Exception e) {
			throw new PontoInteligenteApplicationException("Ocorreu erro na consulta da empresa cadastrada: ["+ e.getMessage()+ "]",  
					e.getCause().toString());
		}
		return empresaDto.getData();
	}
	
	private Response<EmpresaDto> consultarEmpresaCadastrada(HttpResponse resposta)
			throws PontoInteligenteApplicationException {
		
		response = new StringBuffer();
		mapperObj = new ObjectMapper();
		BufferedReader rd;
		try {
			rd = new BufferedReader(new InputStreamReader(((org.apache.http.HttpResponse) resposta).getEntity().getContent()));
			while ((jsonRetorno = rd.readLine()) != null) {
				response.append(jsonRetorno);
			}
			 empresaDto = mapperObj.readValue(response.toString(), new TypeReference<Response<EmpresaDto>>() {
				});
			
		} catch (IllegalStateException | IOException e) {
			throw new PontoInteligenteApplicationException("Ocorreu erro na consulta da e,mpresa cadastrada: ",  e.getMessage());
		}
		return empresaDto;
	}
}
