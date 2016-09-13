import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class GeradorSolucoes {

	public static final Estado
	INSTANCIA_A = new Estado( new int[][] {
			{ 1, 3,  2 },
			{ 6, 1, -3 },
			{ 5, 6,  2 },
			{ 1, 6,  3 }
	}),
	INSTANCIA_B = new Estado( new int[][] {
			{ 1, 3,  2 },
			{ 4, 1, -3 },
			{ 4, 4,  3 },
			{ 4, 5,  3 },
			{ 3, 5, -2 },
			{ 1, 5, -2 }
	}),
	INSTANCIA_C = new Estado( new int[][] {
			{ 2, 3,  2 },
			{ 1, 1, -3 },
			{ 2, 1, -2 },
			{ 4, 1, -3 }, // No enunciado este bloco est� definido com comprimento -2 mas est� com -3 na ilustra��o por baixo
			{ 5, 1, -2 },
			{ 6, 1, -2 },
			{ 3, 4, -2 },
			{ 4, 4,  3 },
			{ 6, 5, -2 },
			{ 3, 6,  3 }
	}),
	INSTANCIA_D = new Estado( new int[][] {
			{ 2, 3,  2 },
			{ 4, 4, -3 },
			{ 5, 3, -3 },
			{ 6, 1, -2 },
			{ 6, 3, -2 },
			{ 5, 6,  2 },
			{ 1, 4,  3 },
			{ 3, 5, -2 },
			{ 1, 5, -2 },
			{ 4, 2,  2 }
	}),
	INSTANCIA_E = new Estado( new int[][] {
			{ 1, 3,  2 },
			{ 1, 1, -2 },
			{ 2, 2,  2 },
			{ 4, 1,  3 },
			{ 4, 2, -2 },
			{ 3, 3, -2 },
			{ 3, 5, -2 },
			{ 4, 4,  2 },
			{ 4, 5,  2 },
			{ 6, 3, -3 },
			{ 4, 6,  3 }
	});

	private int
	expansoes,
	geracoes;
	// fila a ser utilizada pelos algoritmos em profundidade e em largura simples
	private LinkedList<Estado> fila = new LinkedList<Estado>();
	// fila para o algoritmo que d� prioridade aos sucessores que indica como sendo melhores
	private PriorityQueue<Estado> filaPrioritaria = new PriorityQueue<Estado>();
	// mapa de estados j� testados para evitar que se perca tempo a gerar sucessores desnecess�rios
	private HashSet<Integer> visitados = new HashSet<Integer>();
	// estado final caso seja encontrada uma solu��o
	private Estado estadoFinal;

	private Cronometro cronometro = new Cronometro();
	private String tempo;

	// permite reutilizar o gerador para correr outros algoritmos
	private void reset() {
		expansoes = 0;
		geracoes = 0;
		estadoFinal = null;
		fila.clear();
		filaPrioritaria.clear();
		visitados.clear();
	}

	// algoritmo Depth-first Search, faz a procura em profundidade
	public void DFS(Estado estado) {
		reset();
		estado.reset();
		cronometro.inicio();
		// a fila � usada como pilha, ou seja, o primeiro elemento a entrar � o �ltimo a sair
		fila.push(estado);
		// adiciona-se o estado inicial � lista de estados visitados
		visitados.add(estado.hashCode());
		// conta-se a expans�o do estado inicial
		expansoes++;
		while(!fila.isEmpty()) {
			// se o estado atual tem sucessores
			if(estado.temMaisSucessores()) {
				// gera-se o pr�ximo sucessor
				Estado sucessor = estado.proximoSucessor();
				// se ainda n�o se testou este estado
				if(!visitados.contains(sucessor.hashCode())) {
					// conta-se mais uma gera��o
					geracoes++;
					// se a gera��o for solu��o, termina
					if(sucessor.eSolucao()) {
						estadoFinal = sucessor;
						tempo = cronometro.fim();
						return;
					}
					// adiciona-se o estado � lista de visitados
					visitados.add(sucessor.hashCode());
					// adiciona-se o estado � pilha de n�s a expandir
					fila.push(sucessor);
				}
				// caso contr�rio
			} else {
				// tira-se o pr�ximo estado da pilha
				estado = fila.pop();
				// se o estado tem sucessores ent�o conta-se mais uma expans�o
				if(estado.temMaisSucessores())
					expansoes++;
			}
		}
	}

	// algoritmo Breadth-first Search, faz a procura em largura
	public void BFS(Estado estado, boolean usarFilaPrioritaria) {
		reset();
		estado.reset();
		// a fila � usada como fila normal ou priorit�ria, no primeiro caso o primeiro elemento a entrar
		// ser� o primeiro a sair, no segundo caso existe uma ordena��o indicada pelo metodo compareTo()
		// do objeto da classe Estado adicionada, que neste caso devolve sempre primeiro os estados em que
		// o rato est� mais pr�ximo da parede do lado direito (sa�da)
		Queue<Estado> fila = usarFilaPrioritaria ? this.filaPrioritaria : this.fila;
		cronometro.inicio();
		// adiciona-se o estado inicial � fila de estados a expandir
		fila.add(estado);
		// adiciona-se o estado inicial � fila de estados visitados
		visitados.add(estado.hashCode());
		while(!fila.isEmpty()) {
			// retira-se o pr�ximo estado da fila
			estado = fila.remove();
			// conta-se uma expans�o
			expansoes++;
			// geram-se todos os sucessores
			while(estado.temMaisSucessores()) {
				// gera-se o pr�ximo sucessor
				Estado sucessor = estado.proximoSucessor();
				// se ainda n�o se testou este estado
				if(!visitados.contains(sucessor.hashCode())) {
					// conta-se mais uma gera��o
					geracoes++;
					if(sucessor.eSolucao()) {
						estadoFinal = sucessor;
						tempo = cronometro.fim();
						return;
					}
					// adiciona-se o estado � lista de visitados
					visitados.add(sucessor.hashCode());
					// adiciona-se o estado � fila de n�s a expandir
					fila.add(sucessor);
				}
			}
		}
	}

	public String toString() {
		// indica-se se foi ou n�o encontrada solu��o e em caso positivo mostra-se informa��o sobre os resultados obtidos
		return estadoFinal == null ?
			"Solu��o n�o encontrada" + System.lineSeparator() :
			String.format("Gera��es: %d Expans�es: %d Movimentos: %d Tempo: %s %s", geracoes, expansoes, estadoFinal.solucao().size(), tempo, System.lineSeparator());
	}

	// mostram-se todos os estados desde o inicial at� chegar � solu��o
	public void mostraPassosSolucao() {
		if(estadoFinal != null) {
			LinkedList<Estado> solucao = this.estadoFinal.solucao();
			Iterator<Estado> i = solucao.iterator();
			while(i.hasNext())
				System.out.println(i.next());
		}
	}

	public static void main(String[] args) {
		GeradorSolucoes s = new GeradorSolucoes();
		Estado[] instancias = new Estado[] {
				INSTANCIA_A,
				INSTANCIA_B,
				INSTANCIA_C,
				INSTANCIA_D,
				INSTANCIA_E
		};
		char atual = 'A';
		for(Estado instancia : instancias) {
			System.out.println("-----------");
			System.out.println("INSTANCIA " + (atual++));
			System.out.println("-----------");

			s.DFS(instancia);
			System.out.println("DFS:");
			System.out.println(s);
			//s.mostraPassosSolucao();

			s.BFS(instancia, false);
			System.out.println("BFS:");
			System.out.println(s);
			//s.mostraPassosSolucao();

			s.BFS(instancia, true);
			System.out.println("BFS-PriorityQueue:");
			System.out.println(s);
			//s.mostraPassosSolucao();

			System.out.println();
		}
	}
}
