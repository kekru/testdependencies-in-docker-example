package de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.entities;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Benutzer implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long id;
	private String name;
	private String email;
	private String password;
}
