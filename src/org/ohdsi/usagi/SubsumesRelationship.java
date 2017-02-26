package org.ohdsi.usagi;

import org.ohdsi.utilities.files.Row;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;

import static com.sleepycat.persist.model.Relationship.*;

@Entity
public class SubsumesRelationship {
	@PrimaryKey(sequence="Sequence_Namespace")
	public int id;
	
	@SecondaryKey(relate=MANY_TO_ONE)
	public int parentConceptId;
	@SecondaryKey(relate=MANY_TO_ONE)
	public int childConceptId;
	
	public SubsumesRelationship() {}
	
	public SubsumesRelationship(Row row) {
		parentConceptId = row.getInt("concept_id_1");
		childConceptId = row.getInt("concept_id_2");
	}
}
