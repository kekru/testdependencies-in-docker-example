package de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.util;

public class BeispielDockerUtils {

	public static String getPropertyOrSystemEnv(String key){
		String s = System.getProperty(key);
		if(s != null){
			return s;
		}
		
		return System.getenv(key);
	}
}
