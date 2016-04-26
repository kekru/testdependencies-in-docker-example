package de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.beans;

import java.util.List;

import javax.ejb.Local;

import de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.entities.Anzeige;
import de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.entities.Benutzer;

@Local
public interface MongoLocal {
	Anzeige save(Anzeige a);
	Benutzer save(Benutzer b);
	Anzeige findByIdAnzeige(long id);
	Benutzer findByIdBenutzer(long id);
	List<Anzeige> findAllAnzeige();
	List<Benutzer> findAllBenutzer();
}
