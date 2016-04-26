package de.fh_dortmund.kekru001.projektarbeit.beispieldocker_web.jsfbeans;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import lombok.Getter;
import de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.beans.KleinanzeigenRemote;
import de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.entities.Anzeige;
import de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.entities.Benutzer;

@ManagedBean
@RequestScoped
public class KleinanzeigenJSF {
	
	@EJB
	private KleinanzeigenRemote kleinanzeigen;
	
	@Getter
	private Anzeige neueAnzeige = new Anzeige(new Benutzer());
	
	public List<Anzeige> getAnzeigen(){
		return kleinanzeigen.findAll();
	}
	
	public void addAnzeige(){
		kleinanzeigen.save(neueAnzeige);
	}
}
