package br.com.dantas.ponto.inteligente.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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

		String url = uri.concat(recurso);
		Response<EmpresaDto> resposta = null;

		// String token =
		// getToken(PontoInteligenteApplicationConstants.URI_TOKEN);

		try {
			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setRequestMethod("GET");
			// con.setRequestProperty("Authorization", token);
			conn.setRequestProperty("User-Agent", PontoInteligenteApplicationConstants.USER_AGENT);

			// passo os parametros para o JwtAuthenticationTokenFilter para gerar o token
			// con.setRequestProperty("user_email", "admin@kazale.com");
			// con.setRequestProperty("user_password", "123456");

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				if (conn.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
					throw new PontoInteligenteApplicationException("C�digo do erro: " + String.valueOf(conn.getResponseCode() + " :"),
							"Ocorreu erro na consulta da empresa cadastrada: "
									+ consultarEmpresaCadastrada(conn.getErrorStream()).getErrors());
				} else {
					throw new PontoInteligenteApplicationException("Ocorreu erro na consulta da empresa cadastrada: ",
							"[C�digo do erro: " + String.valueOf(conn.getResponseCode()) + "]");
				}
			}
			resposta = consultarEmpresaCadastrada(conn.getInputStream());
		} catch (IOException e) {
			throw new PontoInteligenteApplicationException("Erro na chamada da API", e.getMessage());
		}
		if (!Optional.ofNullable(resposta.getData()).isPresent()) {
			log.info("Erro na chamada da API");
			throw new PontoInteligenteApplicationException("Erro na chamada da API", "Empresa n�o encontrada");
		}
		return resposta.getData();
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
				throw new PontoInteligenteApplicationException("Erro na chamada da API de gera��o de Token",
						con.getResponseMessage());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				// aqui converto um Generics em um objeto java
				responseToken = mapperObj.readValue(output, new TypeReference<Response<TokenDto>>() {
				});
				System.out.println(output);
				token = responseToken.getData();
			}
			con.disconnect();
		} catch (MalformedURLException e) {
			throw new PontoInteligenteApplicationException("Erro na chamada da API de gera��o de Token",
					e.getMessage());
		} catch (IOException e) {
			throw new PontoInteligenteApplicationException("Erro na chamada da API de gera��o de Token",
					e.getMessage());
		}
		return token.getToken();
	}

	private EmpresaDto getEmpresaDtoObject(String jsonInString) throws PontoInteligenteApplicationException {

		EmpresaDto empresa = null;
		mapperObj = new ObjectMapper();

		try {
			response = mapperObj.readValue(jsonInString, new TypeReference<Response<EmpresaDto>>() {
			});
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

	private Response<EmpresaDto> consultarEmpresaCadastrada(InputStream inputStream)
			throws PontoInteligenteApplicationException {

		BufferedReader in;
		String inputLine;
		StringBuffer resp = new StringBuffer();
		mapperObj = new ObjectMapper();
		try {
			in = new BufferedReader(new InputStreamReader(inputStream));
			while ((inputLine = in.readLine()) != null) {
				resp.append(inputLine);
			}
			in.close();
			response = mapperObj.readValue(resp.toString(), new TypeReference<Response<EmpresaDto>>() {
			});
		} catch (IOException e) {
			throw new PontoInteligenteApplicationException("Erro na chamada da API", e.getMessage());
		}
		return response;
	}
}
