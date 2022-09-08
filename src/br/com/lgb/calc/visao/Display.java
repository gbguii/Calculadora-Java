package br.com.lgb.calc.visao;


import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import br.com.lgb.calc.modelo.Memoria;
import br.com.lgb.calc.modelo.MemoriaObservador;

@SuppressWarnings("serial")
public class Display extends JPanel implements MemoriaObservador{
	/** Define uma label para  o display. */
	private final JLabel label;
	
	public Display() {
		// Se adiciona como observador na classe Memoria
		Memoria.getInstancia().registrarObservador(this);
		// Define a cor de funco.
		setBackground(new Color(46,49, 50));
		// Define a label vindo da classe observada
		label = new JLabel(Memoria.getInstancia().getTextoAtual());
		label.setForeground(Color.white);
		// Define a fonte da label
		label.setFont(new Font("courier",Font.PLAIN, 30));
		// Define o layout
		setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 20));
		// Adiciona a label ao componente.
		add(label);
	}
	
	@Override
	public void valorAlterado(String novoValor) {
		label.setText(novoValor);
	}
}
