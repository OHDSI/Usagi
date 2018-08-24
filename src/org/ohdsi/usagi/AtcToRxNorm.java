package org.ohdsi.usagi;

import java.util.HashSet;
import java.util.Set;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class AtcToRxNorm {
	@PrimaryKey
	public String		atc;
	public Set<Integer>	conceptIds	= new HashSet<Integer>();

	public AtcToRxNorm() {
	}

	public AtcToRxNorm(String atc) {
		this.atc = atc;
	}
}
