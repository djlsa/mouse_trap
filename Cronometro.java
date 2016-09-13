import java.util.TimeZone;


public class Cronometro {
	private long
		inicio, fim;

	public Cronometro() {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
	}

	public void inicio() {
		inicio = System.currentTimeMillis();
		fim = 0;
	}

	public String fim() {
		if(fim == 0) {
			fim = System.currentTimeMillis();
		}
		return String.format("%1$tMm %1$tSs %1$tLms", fim - inicio);
	}
}
