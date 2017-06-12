package org.ohdsi.usagi;

import org.ohdsi.utilities.files.Row;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;

import static com.sleepycat.persist.model.Relationship.*;

@Entity
public class ParentChildRelationShip {
	@PrimaryKey(sequence="Sequence_Namespace")
	public int id;
	
	@SecondaryKey(relate=MANY_TO_ONE)
	public int parentConceptId;
	@SecondaryKey(relate=MANY_TO_ONE)
	public int childConceptId;
	
	public ParentChildRelationShip() {}
	
	public ParentChildRelationShip(Row row) {
		parentConceptId = row.getInt("ancestor_concept_id");
		childConceptId = row.getInt("descendant_concept_id");
	}
}
