package br.com.lgb.calc.modelo;

@FunctionalInterface
public interface MemoriaObservador {
	
	public void valorAlterado(String novoValor);
}
