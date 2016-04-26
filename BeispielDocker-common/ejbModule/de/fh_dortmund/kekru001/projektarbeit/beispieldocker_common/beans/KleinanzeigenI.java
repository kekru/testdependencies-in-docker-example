package de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.beans;

import java.util.List;

import de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.entities.Anzeige;
import de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.entities.Benutzer;

public interface KleinanzeigenI {

	void save(Anzeige a);
	void save(Benutzer b);
	List<Anzeige> findAll();
}
