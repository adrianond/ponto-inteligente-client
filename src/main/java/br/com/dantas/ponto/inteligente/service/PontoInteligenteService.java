package br.com.dantas.ponto.inteligente.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import br.com.dantas.ponto.inteligente.constantes.PontoInteligenteApplicationConstants;
import br.com.dantas.ponto.inteligente.dto.EmpresaDto;
import br.com.dantas.ponto.inteligente.exception.PontoInteligenteApplicationException;
import br.com.dantas.ponto.inteligente.response.Response;
import br.com.dantas.ponto.inteligente.response.TokenDto;

public class PontoInteligenteService {

	private static final Logger log = LoggerFactory.getLogger(PontoInteligenteService.class);
	static ObjectMapper mapperObj = null;
	Response<EmpresaDto> response = null;
	Response<TokenDto> responseToken = null;

	public EmpresaDto sendGetPontoInteligenteApi(String uri, String recurso)
			throws PontoInteligenteApplicationException {

		StringBuffer resp = null;
		String url = uri.concat(recurso);
		EmpresaDto emp = null;
		
		String token = getToken(PontoInteligenteApplicationConstants.URI_TOKEN);
		
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", token);
			con.setRequestProperty("User-Agent", PontoInteligenteApplicationConstants.USER_AGENT);
			
			//passo os parametros para o JwtAuthenticationTokenFilter para gerar o token
			//con.setRequestProperty("user_email", "admin@kazale.com");
			//con.setRequestProperty("user_password", "123456");
            
			int responseCode = con.getResponseCode();

			log.info("Enviando 'GET' request para API");
			log.info("Código response: " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			resp = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				resp.append(inputLine);
			}
			in.close();
		} catch (MalformedURLException e) {
			throw new PontoInteligenteApplicationException("Erro na chamada da API", e.getMessage());
		} catch (IOException e) {
			throw new PontoInteligenteApplicationException("Erro na chamada da API", e.getMessage());
		}
		System.out.println(resp.toString());
		emp = getEmpresaDtoObject(resp.toString());
		if (!Optional.ofNullable(emp).isPresent()) {
			log.info("Erro na chamada da API");
			throw new PontoInteligenteApplicationException("Erro na chamada da API", "Empresa não encontrada");
		}
		return emp;
	}

	private String getToken(String uriToken) throws PontoInteligenteApplicationException {

		URL obj;
		TokenDto token = new TokenDto();
		mapperObj = new ObjectMapper();
		
		try {
			obj = new URL(uriToken);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			 con.setDoOutput(true);

			String input = "{\"email\":\"admin@kazale.com\",\"senha\":\"123456\"}";
			OutputStream os = con.getOutputStream();
			os.write(input.getBytes());
			os.flush();
			
			
			if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new PontoInteligenteApplicationException("Erro na chamada da API de geração de Token", con.getResponseMessage());
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				// aqui converto um Generics em um objeto java
				responseToken = mapperObj.readValue(output, new TypeReference<Response<TokenDto>>() {});
				System.out.println(output);
				token = responseToken.getData();
			}
			con.disconnect();
		} catch (MalformedURLException e) {
			throw new PontoInteligenteApplicationException("Erro na chamada da API de geração de Token", e.getMessage());
		} catch (IOException e) {
			throw new PontoInteligenteApplicationException("Erro na chamada da API de geração de Token", e.getMessage());
		}
		return token.getToken();
	}

	private EmpresaDto getEmpresaDtoObject(String jsonInString) throws PontoInteligenteApplicationException {
		
		EmpresaDto empresa = null;
		mapperObj = new ObjectMapper();

		try {
			// aqui converto um Generics em um objeto java
			response = mapperObj.readValue(jsonInString, new TypeReference<Response<EmpresaDto>>() {});
		} catch (JsonParseException e) {
			throw new PontoInteligenteApplicationException("Erro no parse Json to EmpresaDto",
					"Erro no parse Json to EmpresaDto -> " + e.getMessage());
		} catch (JsonMappingException e) {
			throw new PontoInteligenteApplicationException("Erro no parse Json to EmpresaDto",
					"Erro no parse Json to EmpresaDto -> " + e.getMessage());
		} catch (IOException e) {
			throw new PontoInteligenteApplicationException("Erro no parse Json to EmpresaDto", e.getMessage());
		}
		return empresa = response.getData();
	}

}
