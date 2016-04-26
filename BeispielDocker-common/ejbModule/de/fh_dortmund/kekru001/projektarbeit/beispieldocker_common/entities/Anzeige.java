package de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.entities;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Anzeige implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long id;
	private String titel;
	private String text;
	private boolean gewerblich;
	private int preis;
	private Benutzer ersteller;
	
	public Anzeige(Benutzer ersteller){
		this.ersteller = ersteller;
	}
}
