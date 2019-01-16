package br.com.dantas.ponto.inteligente.dto;

public class EmpresaDto {

	private Long id;
	private String razaoSocial;
	private String cnpj;

	public EmpresaDto() {

	}

	public EmpresaDto(Long id, String razaoSocial, String cnpj) {
        this.id = id;
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

}
