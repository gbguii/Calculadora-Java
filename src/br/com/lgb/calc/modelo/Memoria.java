package br.com.lgb.calc.modelo;

import java.util.ArrayList;
import java.util.List;

public class Memoria {
	/** Tipos de comando que pode ser selecionado no teclado. */
	public enum TipoComando {
		LIMPAR, SINAL, PORC ,NUMERO, DIV, MULT, SUB, SOMA, IGUAL, VIRGULA;
	}
	/** Intancia da classe. */
	private static final Memoria instancia = new Memoria();
	/** Lista de observadores da classe. */
	private final List<MemoriaObservador> observadores = new ArrayList<>();
	/** N�mero inicial para mostrar no display */
	private String textoAtual = "";
	/** N�mero que fica no buffer */
	private String textoBuffer = "";
	/** Define se deve substituir o n�mero atual no display pelo pr�ximo digitada */
	private boolean substituir = false;
	/** Ultimo comando selecionado no teclado. */
	private TipoComando ultimaOperacao = null;
	
	/**
	 * Construtor padr�o.
	 */
	private Memoria() {
		
	}
	
	/**
	 * Adiciona a lista de observadores um observador.
	 * @param observador um observador da classe.
	 */
	public void registrarObservador(MemoriaObservador observador) {
		observadores.add(observador);
	}
	
	/**
	 * Retorna a instancia da classe.
	 * @return a instancia da classe.
	 */
	public static Memoria getInstancia() {
		return instancia;
	}
	
	/**
	 * Retorna o n�mero atual do display, caso seja vazio retorna 0.
	 * @return o n�mero atual do display, caso seja vazio retorna 0.
	 */
	public String getTextoAtual() {
		return textoAtual.isEmpty() ? "0" : textoAtual;
	}
	
	/**
	 * Define o display depois de selecionar um comando no teclado.
	 * @param texto qual comando foi selecionado no teclado.
	 */
	public void processarComando(String texto) {
		// Recupera o comando selecionado no teclado.
		TipoComando tipoComando = detectarTipoComando(texto);
		// Verifica se o comando � v�lido.
		if(tipoComando == null) {
			return;
		}
		// Verifica se foi selecionado para limpar o display.
		else if(tipoComando == TipoComando.LIMPAR) {
			// Limpa o display.
			textoAtual = "";
			// Limpa o buffer.
			textoBuffer = "";
			// Define como falso se deve subtituir o valor do display.
			substituir = false;
			// Limpa a ultima opera��o selecionada.
			ultimaOperacao = null;
		}
		// Verifica se foi selecionada para mudar o sinal e se o n�mero j� esta negativo.
		else if(tipoComando == TipoComando.SINAL && textoAtual.contains("-")) {
			// Define o n�mero atual para retirar o sinal negativo.
			textoAtual = textoAtual.substring(1);
		}
		// Verifica se foi selecionada para mudar o sinal e se o n�mero n�o � igual a zero
		else if(tipoComando == TipoComando.SINAL && this.getTextoAtual() != "0" && 
				// e n�o cont�m o sinal negativo.
				!textoAtual.contains("-")) {
			// Muda para negativo o n�mero.
			textoAtual = "-" + textoAtual;
		}
		// Verifica se foi selecionado um n�mero.
		else if(tipoComando == TipoComando.NUMERO) {
			// Define o n�mero atual do display.
			textoAtual = substituir ? texto : textoAtual + texto;
			// Define como falso para substituir o n�mero.
			substituir = false;
		}
		// Verifica se foi selecionado para adicionar virgula.
		else if(tipoComando == TipoComando.VIRGULA) {
			// Se o n�mero for igual a zero.
			if(textoAtual.isEmpty()) {
				// Define o n�mero atual com a virgula.
				textoAtual = getTextoAtual() + texto;
			}else {
				// Define o n�mero atual com virgula.
				textoAtual = textoAtual + texto;
			}
		}
		// Verifica se foi selecionado porcentagem
		else if(tipoComando == TipoComando.PORC) {
			// define a opera��o de porcentagem;
			ultimaOperacao = tipoComando;
			// Define o n�mero de buffer passando o atual;
			textoBuffer = textoAtual;
			// Define o n�mero atual.
			textoAtual = obterResultadoOperacao();
		}else {
			// Define que deve substituir o n�mero do display para o pr�ximo digitado.
			substituir = true;
			// Define o n�mero atual.
			textoAtual = obterResultadoOperacao();
			// Define o n�mero de buffer passando o atual.
			textoBuffer = textoAtual;
			// define a opera��o selecionada.
			ultimaOperacao = tipoComando;
		}
		// Informa os observadores que o valor alterou.
		observadores.forEach(o -> o.valorAlterado(this.getTextoAtual()));
	}
	
	/**
	 * Retorna o resultado da opera��o selecionada.
	 * @return  o resultado da opera��o selecionada.
	 */
	private String obterResultadoOperacao() {
		// Se selecionado para limpar o display ou selecionado para 
		if(ultimaOperacao == null || ultimaOperacao == TipoComando.IGUAL || 
				(textoAtual.isEmpty() && ultimaOperacao == TipoComando.SINAL)) {
			return textoAtual;
		}
		double numeroBuffer = 0;
		double numeroAtual = 0;
		if(!textoBuffer.isEmpty()) {
			numeroBuffer = Double.parseDouble(textoBuffer.replace(",", "."));
		}
		if(!textoAtual.isEmpty()) {
			numeroAtual = Double.parseDouble(textoAtual.replace(",", "."));
		}
		double resultadoMatematico = 0;
		if(ultimaOperacao == TipoComando.SOMA) {
			resultadoMatematico  = numeroBuffer + numeroAtual;
		}else if(ultimaOperacao == TipoComando.SUB) {
			resultadoMatematico  = numeroBuffer - numeroAtual;
		}else if(ultimaOperacao == TipoComando.MULT) {
			resultadoMatematico  = numeroBuffer * numeroAtual;
		}else if(ultimaOperacao == TipoComando.DIV) {
			resultadoMatematico  = numeroBuffer / numeroAtual;
		}else if(ultimaOperacao == TipoComando.PORC) {
			resultadoMatematico = numeroBuffer / 100;
		}
		String resultadoString =  Double.toString(resultadoMatematico).replace(".", ",");
		boolean inteiro = resultadoString.endsWith(",0");
		return inteiro? resultadoString.replace(",0", "") : resultadoString;
	}
	
	/**
	 * Retorna o tipo do comando selecionado.
	 * @param texto texto do bot�o selecionado.
	 * @return o tipo do comando selecionado.
	 */
	private TipoComando detectarTipoComando(String texto) {
		TipoComando resultado = null;
		if(textoAtual.isEmpty() && texto == "0") {
			return null;
		}
		try {
			Integer.parseInt(texto);
			resultado = TipoComando.NUMERO;
		}catch(NumberFormatException e) {
			// quando n�o for n�mero
			if("AC".equals(texto)) {
				resultado =  TipoComando.LIMPAR;
			}else if("/".equals(texto)) {
				resultado =  TipoComando.DIV;
			}else if("x".equals(texto)) {
				resultado =  TipoComando.MULT;
			}else if("+".equals(texto)) {
				resultado =  TipoComando.SOMA;
			}else if("-".equals(texto)) {
				resultado =  TipoComando.SUB;
			}else if(",".equals(texto) && !textoAtual.contains(",")) {
				resultado =  TipoComando.VIRGULA;
			}else if("=".equals(texto)) {
				resultado =  TipoComando.IGUAL;
			}else if("+-".equals(texto)) {
				resultado =  TipoComando.SINAL;
			}else if("%".equals(texto)) {
				resultado =  TipoComando.PORC;
			}
		}
		return resultado;
	}
}
