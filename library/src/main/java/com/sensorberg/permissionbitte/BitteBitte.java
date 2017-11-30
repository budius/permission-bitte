package com.sensorberg.permissionbitte;

/**
 * Do you want permission to do stuff? This interface will let you know when it's clear.
 */
public interface BitteBitte {
	/**
	 * You got all permissions, go crazy
	 */
	void yesYouCan();

	/**
	 * Sorry bra!
	 */
	void noYouCant();

	/**
	 * Maybe if you ask nicer,
	 * show some rationale maybe?
	 */
	void askNicer();

}
