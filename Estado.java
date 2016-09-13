import java.util.AbstractMap;
import java.util.LinkedList;

public class Estado implements Comparable<Estado> {

	private static final int
	LINHAS = 6,
	COLUNAS = 6;

	// esta classe representa cada bloco
	private class Bloco {
		public static final int
		MOVE_ESQUERDA = 1,
		MOVE_DIREITA = 2,
		MOVE_CIMA = 4,
		MOVE_BAIXO = 8;
		private int
		linha, coluna, tamanho;

		public Bloco(int coluna, int linha, int tamanho) {
			this.coluna = coluna; this.linha = linha; this.tamanho = tamanho;
		}

		// cria um novo bloco baseado em outro com um determinado movimento aplicado
		public Bloco(Bloco outro, int movimento) {
			int moveColuna = (movimento == MOVE_ESQUERDA ? -1 : (movimento == MOVE_DIREITA ? 1 : 0));
			this.coluna = outro.coluna + moveColuna;
			int moveLinha = (movimento == MOVE_CIMA ? -1 : (movimento == MOVE_BAIXO ? 1 : 0));
			this.linha = outro.linha + moveLinha;
			this.tamanho = outro.tamanho;
		}
	}

	// guarda o pai do estado atual para se poder percorrer a solução
	private Estado pai = null;

	private Bloco[] blocos;

	// mapa das células do jogo para facilitar a dedução dos movimentos possíveis e o output para ecrã
	private char[][] mapa = new char[LINHAS][COLUNAS];

	// lista de movimentos possíveis de cada bloco (implementada como lista de pares Bloco-Movimento,
	// em que o movimento é uma das constantes definidas na classe Bloco)
	private LinkedList<AbstractMap.SimpleEntry<Bloco, Integer>>
	movimentosPossiveis = new LinkedList<AbstractMap.SimpleEntry<Bloco, Integer>>();

	// representação ilustrativa do jogo para output para ecrã
	private String representacao;

	// representação numérica de um estado, serve para indicar se um determinado estado já foi
	// testado como sendo solução
	private int hashCode;

	// lista que terá todos os passos da solução caso este seja o estado final
	private LinkedList<Estado> solucao = null;

	// CONSTRUTORES

	// este construtor é usado para criar as instancias dadas pelo enunciado, cada bloco está representado
	// por arrays de 3 elementos
	public Estado(int[][] dados) {
		blocos = new Bloco[dados.length];
		int i = 0;
		for(int[] bloco : dados ) {
			if(bloco.length == 3)
				blocos[i++] = new Bloco(bloco[0], bloco[1], bloco[2]);
		}
		atualizaRepresentacao();
	}

	// este construtor serve para criar os sucessores, passando uma lista de blocos criados com o construtor da
	// classe Bloco que indica o movimento a dar a um determinado bloco
	public Estado(Bloco[] blocos) {
		this.blocos = blocos;
		atualizaRepresentacao();
	}

	// método que indica se o estado é uma solução, verificando se o primeiro bloco está encostado à parede da direita
	public boolean eSolucao() {
		return blocos[0].coluna == COLUNAS - blocos[0].tamanho + 1;
	}

	// retorna uma lista com todos os estados até à solução
	public LinkedList<Estado> solucao() {
		if(solucao == null) {
			solucao = new LinkedList<Estado>();
			Estado atual = this;
			while(atual != null) {
				solucao.push(atual);
				atual = atual.pai;
			}
		}
		return solucao;
	}

	// método que auxilia a criação de objetos desta classe, populando um array que indica o conteúdo de cada célula
	// do jogo e quais os movimentos possiveis para cada bloco
	private void atualizaRepresentacao() {
		int numeroBlocos = blocos.length;
		char caracter = 'A';
		// coloca no array mapa os caracteres correspondentes a cada bloco
		for(int i = 0; i < numeroBlocos; i++) {
			Bloco bloco = blocos[i];
			int
			linha = bloco.linha - 1,
			coluna = bloco.coluna - 1,
			tamanho = bloco.tamanho;
			mapa[linha][coluna] = caracter;
			for(int j = 0; j < tamanho; j++) {
				mapa[linha][coluna + j] = caracter;
			}
			for(int j = tamanho + 1; j < 0; j++) {
				mapa[linha - j][coluna] = caracter;
			}
			if(caracter == 'A')
				caracter = 'a';
			caracter++;
		}
		// se não for solução, gera a lista de movimentos possíveis para cada bloco
		if(!eSolucao()) {
			for(int i = 0; i < numeroBlocos; i++) {
				Bloco bloco = blocos[i];
				int
				linha = bloco.linha - 1,
				coluna = bloco.coluna - 1,
				tamanho = bloco.tamanho,
				linha2 = linha - tamanho,
				coluna2 = coluna + tamanho;
				boolean
				horizontal = tamanho > 0,
				vertical = tamanho <0;

				if(horizontal && coluna > 0 && mapa[linha][coluna-1] == 0)
					movimentosPossiveis.offer(new AbstractMap.SimpleEntry<Bloco, Integer>(bloco, Bloco.MOVE_ESQUERDA));
				if(horizontal && coluna2 < mapa[linha].length && mapa[linha][coluna2] == 0)
					movimentosPossiveis.offer(new AbstractMap.SimpleEntry<Bloco, Integer>(bloco, Bloco.MOVE_DIREITA));
				if(vertical && linha > 0 && mapa[linha-1][coluna] == 0)
					movimentosPossiveis.offer(new AbstractMap.SimpleEntry<Bloco, Integer>(bloco, Bloco.MOVE_CIMA));
				if(vertical && linha2 < mapa.length && mapa[linha2][coluna] == 0)
					movimentosPossiveis.offer(new AbstractMap.SimpleEntry<Bloco, Integer>(bloco, Bloco.MOVE_BAIXO));

			}
		}
		// põe numa variável a ilustração do estado do jogo
		StringBuilder sb = new StringBuilder(LINHAS * COLUNAS + (LINHAS * 2));
		String sep = System.lineSeparator();
		for(int i = 0; i < LINHAS; i++) {
			for(int j = 0; j < COLUNAS; j++) {
				sb.append(' ');
				if(mapa[i][j] == 0)
					sb.append('.');
				else
					sb.append(mapa[i][j]);
				sb.append(' ');
			}
			sb.append(sep);
		}
		representacao = sb.toString();
		hashCode = representacao.hashCode();
	}

	// permite reutilizar o estado
	public void reset() {
		movimentosPossiveis.clear();
		atualizaRepresentacao();
	}

	// indica se é posível gerar mais sucessores, ou seja, se existem mais blocos com movimentos possíveis
	public boolean temMaisSucessores() {
		return !movimentosPossiveis.isEmpty();
	}

	// gera o próximo estado sucessor a partir da lista de movimentos possíveis
	public Estado proximoSucessor() {
		// próximo par bloco-movimento, ou null se não existir
		AbstractMap.SimpleEntry<Bloco, Integer>
		blocoMovimento = !movimentosPossiveis.isEmpty() ? movimentosPossiveis.poll() : null;
		if(blocoMovimento != null) {
			Bloco blocoAMover = blocoMovimento.getKey();
			int movimento = blocoMovimento.getValue();
			int numeroBlocos = blocos.length;
			// gera blocos a partir dos existentes no estado atual, aplicando movimento ao bloco a mover
			Bloco[] blocos = new Bloco[numeroBlocos];
			for(int i = 0; i < numeroBlocos; i++) {
				blocos[i] = new Bloco(this.blocos[i], (this.blocos[i] == blocoAMover ? movimento : 0));
			}
			// sucessor gerado!
			Estado sucessor = new Estado(blocos);
			sucessor.pai = this;
			return sucessor;
		}
		return null;
	}

	public String toString() {
		return representacao;
	}

	public int hashCode() {
		return hashCode;
	}

	// este método auxilia o algoritmo que usa uma fila prioritária, indicando que um estado é melhor do que outro, ou seja,
	// que é anterior a outro em termos de ordenação natural caso a distância do primeiro bloco (rato) à parede direita seja menor
	@Override
	public int compareTo(Estado e) {
		int distance1 = COLUNAS - blocos[0].coluna;
		int distance2 = COLUNAS - e.blocos[0].coluna;
		return distance1 < distance2 ? -1 : distance1 > distance2 ? 1 : 0;
	}
}
