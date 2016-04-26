package de.fh_dortmund.kekru001.projektarbeit.util;

import junit.framework.Assert;

import org.junit.Test;

public class SimpleUnitTest {

	@Test
	public void test(){
		long result = Math.round(98.7);
		Assert.assertEquals(99, result);
	}
}
