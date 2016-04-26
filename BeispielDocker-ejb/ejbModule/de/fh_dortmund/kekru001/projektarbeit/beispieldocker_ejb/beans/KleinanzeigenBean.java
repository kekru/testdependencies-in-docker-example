package de.fh_dortmund.kekru001.projektarbeit.beispieldocker_ejb.beans;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.beans.KleinanzeigenLocal;
import de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.beans.KleinanzeigenRemote;
import de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.beans.MongoLocal;
import de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.entities.Anzeige;
import de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.entities.Benutzer;

@Stateless
public class KleinanzeigenBean implements KleinanzeigenLocal, KleinanzeigenRemote, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@EJB
	private MongoLocal mongo;

	@Override
	public List<Anzeige> findAll() {
		return mongo.findAllAnzeige();
	}

	@Override
	public void save(Anzeige a) {
		mongo.save(a);
	}

	@Override
	public void save(Benutzer b) {
		mongo.save(b);
	}

}
