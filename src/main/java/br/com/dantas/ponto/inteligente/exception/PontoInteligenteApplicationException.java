package br.com.dantas.ponto.inteligente.exception;

public class PontoInteligenteApplicationException extends Exception{
	
	private static final long serialVersionUID = 1L;
	private String menssagem;
	private String causa;

	public PontoInteligenteApplicationException(String menssagem, String causa) {
		this.menssagem = menssagem;
		this.causa = causa;
	}

	public String getMenssagem() {
		return menssagem;
	}

	public void setMenssagem(String menssagem) {
		this.menssagem = menssagem;
	}

	public String getCausa() {
		return causa;
	}

	public void setCausa(String causa) {
		this.causa = causa;
	}

}
